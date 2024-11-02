all: clean check

clean:
	@rm ./go_app ./c_app ||  true

check: ./go_app ./c_app
	echo 8 | time -v ./go_app | wc -l
	echo 8 | time -v ./c_app | wc -l

go_app: main.go
	@go build -o go_app .

c_app: ./pure_c/main.c
	@gcc -o c_app -O3 ./pure_c/main.c