build:
	GOOS=js GOARCH=wasm go build -o example.wasm .

serve:
	bash -c "cp `go env GOROOT`/misc/wasm/wasm_exec.js ."
	go run ./server