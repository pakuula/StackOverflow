package main

import (
	"encoding/json"
	"fmt"
	"math"
)

var msg = `{
	"payload":[
	   0,
	   [
		  [
			 [
				{
				   "title":"Привет!"
				},
				{
				   "title":"123"
				}
			 ]
		  ],
		  1000
	   ]
	]
 }`

type Title struct {
	Title string `json:"title"` // Use default json codec
}

// Restore the object from the object created by the generic `json.Unmarshal`
func (t *Title) fromUnmarshalled(obj interface{}) error {
	mapObj, ok := obj.(map[string]interface{})
	if !ok {
		return fmt.Errorf("Title: Non a Title object: %#v", obj)
	}
	titleFieldObj, ok := mapObj["title"]
	if !ok {
		// Missing "title" field
		t.Title = ""
	} else {
		title, ok := titleFieldObj.(string)
		if !ok {
			return fmt.Errorf("Title: value for `title` is not a string: %#v", titleFieldObj)
		}
		t.Title = title
	}
	return nil
}

type ArrayOfTitles []Title // Use default json codec

// Restore the object from the object created by the generic `json.Unmarshal`
func (aot *ArrayOfTitles) fromUnmarshalled(obj interface{}) error {
	array, ok := obj.([]interface{})
	if !ok {
		return fmt.Errorf("ArrayOfTitles: not an array: %#v", obj)
	}
	res := make(ArrayOfTitles, len(array))
	for i, obj2 := range array {
		err := res[i].fromUnmarshalled(obj2)
		if err != nil {
			return err
		}
	}
	*aot = res
	return nil
}

type InnerArray struct {
	Titles []ArrayOfTitles
	Number int
}

// Implements json.Marshaler
func (ia InnerArray) MarshalJSON() ([]byte, error) {
	asArray := []interface{}{
		ia.Titles,
		ia.Number,
	}
	return json.Marshal(asArray)
}

// Optional: Implements json.Unmarshaler, used for testing
// func (dst *InnerArray) UnmarshalJSON(bytes []byte) error {
// 	var obj interface{}
// 	err := json.Unmarshal(bytes, &obj)
// 	if err != nil {
// 		return err
// 	}

// 	return dst.fromUnmarshalled(obj)
// }

// Restore the object from the object created by the generic `json.Unmarshal`
func (dst *InnerArray) fromUnmarshalled(obj interface{}) error {
	decodedArray, ok := obj.([]interface{})
	if !ok {
		return fmt.Errorf("Not an array: %#v", obj)
	}
	if len(decodedArray) != 2 {
		return fmt.Errorf("InnerArray: Two elements required, got %d", len(decodedArray))
	}

	possibleTitles := decodedArray[0]
	arrayOfTitlesArray, ok := possibleTitles.([]interface{})
	if !ok {
		return fmt.Errorf("InnerArray: Not an array of titles array: %v", arrayOfTitlesArray)
	}
	dst.Titles = make([]ArrayOfTitles, len(arrayOfTitlesArray))
	for i, obj := range arrayOfTitlesArray {
		dst.Titles[i] = ArrayOfTitles{}
		err := dst.Titles[i].fromUnmarshalled(obj)
		if err != nil {
			return err
		}
	}

	possibleNumber := decodedArray[1]
	number, ok := possibleNumber.(float64)
	if !ok {
		return fmt.Errorf("InnerArray: Not a number: %#v (type %T)", possibleNumber, possibleNumber)
	}
	dst.Number = int(math.Round(number))
	return nil
}

type OuterArray struct {
	Number int
	Data   InnerArray
}

// Implements json.Marshaler
func (oa OuterArray) MarshalJSON() ([]byte, error) {
	asArray := []interface{}{
		oa.Number,
		oa.Data,
	}
	return json.Marshal(asArray)
}

// Implements json.Unmarshaler, used to decode Message.Payload field
func (dst *OuterArray) UnmarshalJSON(bytes []byte) error {
	var obj interface{}
	err := json.Unmarshal(bytes, &obj)
	if err != nil {
		return err
	}

	return dst.fromUnmarshalled(obj)
}

// Restore the object from the object created by the generic `json.Unmarshal`
func (dst *OuterArray) fromUnmarshalled(obj interface{}) error {
	decodedArray, ok := obj.([]interface{})
	if !ok {
		return fmt.Errorf("Not an array: %#v", obj)
	}
	if len(decodedArray) != 2 {
		return fmt.Errorf("OuterArray: Two elements required, got %d", len(decodedArray))
	}

	possibleNumber := decodedArray[0]
	number, ok := possibleNumber.(float64)
	if !ok {
		return fmt.Errorf("OuterArray: Not a number: %#v (type %T)", possibleNumber, possibleNumber)
	}
	dst.Number = int(math.Round(number))

	return dst.Data.fromUnmarshalled(decodedArray[1])
}

type Message struct {
	Payload OuterArray `json:"payload"` // Use default json codec
}

func main() {
	ia := InnerArray{
		Titles: []ArrayOfTitles{
			{
				Title{Title: "Привет!"},
				Title{Title: "123"},
			},
		},
		Number: 1000,
	}

	oa := OuterArray{
		Number: 0,
		Data:   ia,
	}

	payload := Message{
		Payload: oa,
	}

	bytes, err := json.Marshal(payload)
	if err != nil {
		panic(err)
	}
	fmt.Printf("Object: %#v\nEncoded: %s\n", payload, string(bytes))

	var msgDecoded Message

	err = json.Unmarshal([]byte(msg), &msgDecoded)
	if err != nil {
		panic(err)
	}
	fmt.Printf("Decoded object: %#v\n", msgDecoded)

	var d map[string]interface{}
	err = json.Unmarshal([]byte(msg), &d)
	if err != nil {
		panic(err)
	}
	fmt.Println("Raw titles: ", d["payload"].([]interface{})[1].([]interface{})[0].([]interface{}))
	fmt.Printf("Decoded titles: %v\n", msgDecoded.Payload.Data.Titles)
}
