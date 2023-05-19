package main

import (
	"bytes"
	"encoding/json"
	"fmt"
	"reflect"

	"example.org/reflect/price"
	"github.com/shopspring/decimal"
)

type ExampleRS struct {
	Total price.Price `json:"Total"`
}

type ResponseEntry struct {
	ExampleIntValue          int         `json:"ExampleValue"`
	ExampleStringArrayValues []string    `json:"ExampleStringArrayValues"`
	Price                    price.Price `json:"Price"`
}

type ExampleListRS struct {
	Data    []ResponseEntry `json:"Response"`
	PerPage uint            `json:"Count"`
	Pages   uint            `json:"Pages"`
}

func IsPrice(t reflect.Type) bool {
	return t == reflect.TypeOf(price.Price{})
}

type Visitor struct {
	Factor decimal.Decimal
}

func (v *Visitor) Process(val interface{}) {
	v.Accept(reflect.ValueOf(val))
}
func (v *Visitor) Accept(val reflect.Value) {
	switch val.Kind() {
	case reflect.Array:
	case reflect.Slice:
		for i := 0; i < val.Len(); i++ {
			v.Accept(val.Index(i))
		}
	case reflect.Map:
		for _, key := range val.MapKeys() {
			v.Accept(val.MapIndex(key))
		}
	case reflect.Pointer:
		if val.IsNil() {
			return
		}
		v.Accept(val.Elem())
	case reflect.Struct:
		if IsPrice(val.Type()) {
			amtField := val.FieldByName("Amount")
			amt := amtField.Interface().(decimal.Decimal)
			amt = amt.Mul(v.Factor)
			amtField.Set(reflect.ValueOf(amt))
			return
		}
		for i := 0; i < val.NumField(); i++ {
			v.Accept(val.Field(i))
		}
	default:
		return
	}
}

var defaultVisitor = Visitor{
	Factor: decimal.NewFromInt32(5),
}

func unmarshalJson(jsonDoc []byte) (interface{}, error) {
	for _, t := range []reflect.Type{
		reflect.TypeOf(ExampleRS{}),
		reflect.TypeOf(ExampleListRS{}),
	} {
		val := reflect.New(t)
		decoder := json.NewDecoder(bytes.NewReader(jsonDoc))
		decoder.DisallowUnknownFields()
		err := decoder.Decode(val.Interface())
		if err == nil {
			defaultVisitor.Accept(val)
			return val.Elem().Interface(), nil
		}
	}
	return nil, fmt.Errorf("unknown JSON structure: %s", string(jsonDoc))
}

func main() {
	fmt.Println("Demo1: plain visitor")
	demo1()
	fmt.Println("Demo2: decode ExampleRS document")
	demo2()
	fmt.Println("Demo2: decode ExampleListRS document")
	demo3()
}

func demo1() {
	amt, _ := decimal.NewFromString("12.345")
	test := ExampleRS{
		Total: price.Price{
			Amount:   amt,
			Currency: "BTC",
		},
	}
	fmt.Println("Before: ", test)
	defaultVisitor.Process(&test)
	fmt.Println("After:", test)
}

func demo2() {
	amt, _ := decimal.NewFromString("12.345")
	test := ExampleRS{
		Total: price.Price{
			Amount:   amt,
			Currency: "BTC",
		},
	}
	jsonDoc, _ := json.Marshal(test)
	fmt.Println("Before: ", test)
	val, err := unmarshalJson(jsonDoc)
	if err != nil {
		fmt.Println("failed: ", err.Error())
	} else {
		fmt.Printf("After: %v\n", val)
	}
}

func demo3() {
	amt1, _ := decimal.NewFromString("12.345")
	amt2, _ := decimal.NewFromString("54.321")
	test := ExampleListRS{
		Data: []ResponseEntry{
			{
				ExampleIntValue:          0,
				ExampleStringArrayValues: []string{"Helo"},
				Price: price.Price{
					Amount:   amt1,
					Currency: "BTC",
				},
			},
			{
				ExampleIntValue:          1,
				ExampleStringArrayValues: []string{"world"},
				Price: price.Price{
					Amount:   amt2,
					Currency: "ETH",
				},
			},
		},
		PerPage: 10,
		Pages:   2,
	}
	jsonDoc, _ := json.Marshal(test)
	fmt.Println("Before: ", test)
	val, err := unmarshalJson(jsonDoc)
	if err != nil {
		fmt.Println("failed: ", err.Error())
	} else {
		fmt.Printf("After: %v\n", val)
	}
}
