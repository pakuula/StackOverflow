package main

import (
	"encoding/json"
	"fmt"
	"strings"
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

type ArrayOfTitles []Title // Use default json codec

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

// Implements json.Unmarshaler, used for testing
func (dst *InnerArray) UnmarshalJSON(bytes []byte) error {
	stream := string(bytes)
	dec := json.NewDecoder(strings.NewReader(stream))
	// read open bracket
	token, err := dec.Token()
	if err != nil {
		return err
	}
	delim, ok := token.(json.Delim)
	if !ok {
		return fmt.Errorf("Not an array: %s", stream)
	}
	if delim != '[' {
		return fmt.Errorf("'[' expected, got: %c", delim)
	}

	if !dec.More() {
		return fmt.Errorf("Missing array of titles: %s", string(bytes))
	}

	err = dec.Decode(&dst.Titles)
	if err != nil {
		return err
	}

	if !dec.More() {
		return fmt.Errorf("Missing number: %s", string(bytes))
	}
	err = dec.Decode(&dst.Number)
	if err != nil {
		return err
	}

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

// Implements json.Unmarshaler
func (dst *OuterArray) UnmarshalJSON(bytes []byte) error {
	stream := string(bytes)
	dec := json.NewDecoder(strings.NewReader(stream))
	// read open bracket
	token, err := dec.Token()
	if err != nil {
		return err
	}
	delim, ok := token.(json.Delim)
	if !ok {
		return fmt.Errorf("Not an array: %s", stream)
	}
	if delim != '[' {
		return fmt.Errorf("'[' expected, got: %c", delim)
	}

	if !dec.More() {
		return fmt.Errorf("Missing array of number: %s", string(bytes))
	}
	err = dec.Decode(&dst.Number)
	if err != nil {
		return err
	}

	if !dec.More() {
		return fmt.Errorf("Missing data: %s", string(bytes))
	}
	err = dec.Decode(&dst.Data)
	if err != nil {
		return err
	}

	return nil
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

	{
		zzz, err := json.Marshal(ia)
		if err != nil {
			panic(err)
		}

		var ia2 InnerArray
		err = json.Unmarshal(zzz, &ia2)
		fmt.Printf("Decoded inner array: %#v\n", ia2)
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
