package main

import (
	"log"
	"net/http"
)

func main() {
	fs := http.FileServer(http.Dir("."))
	http.Handle("/", fs)

	log.Print("Listening on localhost:3000...")
	err := http.ListenAndServe("localhost:3000", nil)
	if err != nil {
		log.Fatal(err)
	}
}
