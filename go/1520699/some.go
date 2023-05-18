package main

import (
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
	return t.PkgPath() == "example.org/reflect/price" && t.Name() == "Price"
}

func HasPrice(response interface{}) (bool, *reflect.Value) {
	val := reflect.ValueOf(response)
	for val.Kind() == reflect.Pointer {
		val = val.Elem()
	}
	if val.Kind() != reflect.Struct {
		return false, nil
	}
	for i := 0; i < val.NumField(); i++ {
		field := val.Field(i)
		if IsPrice(field.Type()) {
			return true, &field
		}
	}
	return false, nil
}

func main() {
	amt, _ := decimal.NewFromString("12.345")
	test := ExampleRS{
		Total: price.Price{
			Amount:   amt,
			Currency: "BTC",
		},
	}
	fmt.Println("Before: ", test)
	has, field := HasPrice(&test)
	if has {
		amtField := field.FieldByName("Amount")
		amt := amtField.Interface().(decimal.Decimal)
		amt = amt.Mul(decimal.NewFromInt32(5))
		amtField.Set(reflect.ValueOf(amt))
	}
	fmt.Println("After:", test)
}
