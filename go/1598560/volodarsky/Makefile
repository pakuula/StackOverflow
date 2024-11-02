all: check

check: ./go_app ./c_app
	bash -c 'time echo 8 | ./go_app | wc -l'
	bash -c 'time echo 8 | ./c_app | wc -l'

go_app: main.go
	go build -o go_app .

c_app: ./pure_c/main.c
	gcc -o c_app -O3 ./pure_c/main.c