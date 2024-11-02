Пример к вопросу [Go обгоняет C на задаче с объёмным выводом](https://ru.stackoverflow.com/questions/1598560)

В каталоге [`volodarsky`](./volodarsky/) находится пример кода из вопроса, заданного пользователем *StanislavVolodarsky*.
Запуск `make` в этом каталоге собирает приложение Go и приложение C и измеряет время исполнения.

В каталоге `bench` находится бенчмарк для вывода через С и через Go. Бенчмарк создаёт файл, выводит одну и ту же строку *Lorem ipsum* заданное число раз, и закрывает файл.

В корне находится [`Makefile`](./Makefile), который запускает бенчмарк, а также выполняет [`Makefile`](./volodarsky/Makefile) из каталога `volodarsky`.

Результаты выполнения на Core i7-1260P:

```text
make -C volodarsky
make[1]: Entering directory '/tmp/StackOverflow/go/1598560/volodarsky'
bash -c 'time echo 8 | ./go_app | wc -l'
100000000

real    0m1.620s
user    0m1.148s
sys     0m0.647s
bash -c 'time echo 8 | ./c_app | wc -l'
100000000

real    0m5.491s
user    0m3.319s
sys     0m3.300s
make[1]: Leaving directory '/tmp/StackOverflow/go/1598560/volodarsky'
go test -benchmem -bench 'BenchmarkGoout|BenchmarkCOut' ./bench
goos: linux
goarch: amd64
pkg: some/bench
cpu: 12th Gen Intel(R) Core(TM) i7-1260P
BenchmarkGoout-16              8         153202019 ns/op            4216 B/op          4 allocs/op
BenchmarkCOut-16               6         184548701 ns/op               2 B/op          0 allocs/op
PASS
ok      some/bench      2.668s
```