package main

import (
	"fmt"
	"log"
	"syscall/js"
)

func onClick(this js.Value, args []js.Value) any {
	count++
	target := js.Global().Get("document").Call("getElementById", "_target")
	if target.IsNull() {
		log.Panic("No element with id _target")
	}
	html := fmt.Sprintf("<p>Button clicked %d times</p>", count)
	target.Set("innerHTML", html)

	return js.ValueOf(true)
}

var done = make(chan struct{})
var count = 0

func main() {
	defer func() { log.Println("WASM execution finished") }()

	log.Println("WASM execution started")
	target := js.Global().Get("document").Call("getElementById", "_target")
	if target.IsNull() {
		log.Panic("No element with id _target")
	}
	html := "<p>Hello from Go!</p>"
	target.Set("innerHTML", html)

	btn := js.Global().Get("document").Call("getElementById", "_goBtn")
	if btn.IsNull() {
		log.Panic("No element with id _goBtn")
	}
	btn.Set("onclick", js.FuncOf(onClick))
	btn.Set("disabled", false)

	// Wait until `done` is closed
	<-done
}
