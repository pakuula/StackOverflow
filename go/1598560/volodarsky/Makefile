LENGTH ?= 8
BSIZE ?= 65536

all: clean check

clean:
	@rm ./go_app ./c_app ||  true

check: ./go_app ./c_app
	@echo "C, no print"
	@time -v ./c_app -n $(LENGTH)
	@echo "C, print"
	@time -v ./c_app -b $(BSIZE) $(LENGTH) | wc -l
	@echo "C, print with file sstream lock"
	@time -v ./c_app -b $(BSIZE) -l $(LENGTH) | wc -l
	@echo "Go, no print"
	@time -v ./go_app -n $(LENGTH)
	@echo "Go, print"
	@time -v ./go_app -b $(BSIZE) $(LENGTH) | wc -l

go_app: main.go
	@go build -o go_app .

c_app: ./pure_c/main.c
	@gcc -o c_app -O3 ./pure_c/main.c