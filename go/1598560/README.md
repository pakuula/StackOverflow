Пример к вопросу [Go обгоняет C на задаче с объёмным выводом](https://ru.stackoverflow.com/questions/1598560)

В каталоге [`volodarsky`](./volodarsky/) находится пример кода из вопроса, заданного пользователем *StanislavVolodarsky*.
Запуск `make` в этом каталоге собирает приложение Go и приложение C и измеряет время исполнения.

В каталоге `bench` находится бенчмарк для вывода через С и через Go. Бенчмарк создаёт файл, выводит одну и ту же строку *Lorem ipsum* заданное число раз, и закрывает файл.

В корне находится [`Makefile`](./Makefile), который запускает бенчмарк, а также выполняет [`Makefile`](./volodarsky/Makefile) из каталога `volodarsky`.

# Тесты в Linux

Результаты выполнения в **Windows Subsystem for Linux** на Core i7-1260P: команда `make` в корне проекта

```text
make[1]: Entering directory '/tmp/StackOverflow/go/1598560/volodarsky'
echo 8 | time -v ./go_app | wc -l
        Command being timed: "./go_app"
        User time (seconds): 0.63
        System time (seconds): 0.14
        Percent of CPU this job got: 68%
        Elapsed (wall clock) time (h:mm:ss or m:ss): 0:01.14
        Average shared text size (kbytes): 0
        Average unshared data size (kbytes): 0
        Average stack size (kbytes): 0
        Average total size (kbytes): 0
        Maximum resident set size (kbytes): 5576
        Average resident set size (kbytes): 0
        Major (requiring I/O) page faults: 18
        Minor (reclaiming a frame) page faults: 103
        Voluntary context switches: 21593
        Involuntary context switches: 1
        Swaps: 0
        File system inputs: 2864
        File system outputs: 0
        Socket messages sent: 0
        Socket messages received: 0
        Signals delivered: 0
        Page size (bytes): 4096
        Exit status: 0
100000000
echo 8 | time -v ./c_app | wc -l
        Command being timed: "./c_app"
        User time (seconds): 1.96
        System time (seconds): 1.62
        Percent of CPU this job got: 99%
        Elapsed (wall clock) time (h:mm:ss or m:ss): 0:03.60
        Average shared text size (kbytes): 0
        Average unshared data size (kbytes): 0
        Average stack size (kbytes): 0
        Average total size (kbytes): 0
        Maximum resident set size (kbytes): 1464
        Average resident set size (kbytes): 0
        Major (requiring I/O) page faults: 1
        Minor (reclaiming a frame) page faults: 67
        Voluntary context switches: 130
        Involuntary context switches: 0
        Swaps: 0
        File system inputs: 32
        File system outputs: 0
        Socket messages sent: 0
        Socket messages received: 0
        Signals delivered: 0
        Page size (bytes): 4096
        Exit status: 0
100000000
make[1]: Leaving directory '/tmp/StackOverflow/go/1598560/volodarsky'
goos: linux
goarch: amd64
pkg: some/bench
cpu: 12th Gen Intel(R) Core(TM) i7-1260P
BenchmarkGoToFile-16                  20          37655967 ns/op           65656 B/op          4 allocs/op
BenchmarkC_ToFile-16                  20          53903378 ns/op               0 B/op          0 allocs/op
BenchmarkGoToDevFile-16               20          16001887 ns/op           65656 B/op          4 allocs/op
BenchmarkC_ToDevFile-16               20          21368022 ns/op               0 B/op          0 allocs/op
BenchmarkGoToStdStream-16             20          18014334 ns/op           65536 B/op          1 allocs/op
BenchmarkC_ToStdStream-16             20          19094720 ns/op               0 B/op          0 allocs/op
PASS
ok      some/bench      3.450s
```

# Тесты в Alpine

Запуск в контейнере `docker` командами 
```
docker compose build --build-arg UID=`id -u`
docker compose run --rm bench
```

Результат запуска в Docker Desktop for Windows на Core i7-1260P.
```text
Attaching to bench-1
bench-1  | make: Entering directory '/data'
bench-1  | make[1]: Entering directory '/data/volodarsky'
bench-1  | echo 8 | time -v ./go_app | wc -l
bench-1  | 100000000
bench-1  |      Command being timed: "./go_app"
bench-1  |      User time (seconds): 1.12
bench-1  |      System time (seconds): 0.49
bench-1  |      Percent of CPU this job got: 40%
bench-1  |      Elapsed (wall clock) time (h:mm:ss or m:ss): 0m 3.99s
bench-1  |      Average shared text size (kbytes): 0
bench-1  |      Average unshared data size (kbytes): 0
bench-1  |      Average stack size (kbytes): 0
bench-1  |      Average total size (kbytes): 0
bench-1  |      Maximum resident set size (kbytes): 14080
bench-1  |      Average resident set size (kbytes): 0
bench-1  |      Major (requiring I/O) page faults: 18
bench-1  |      Minor (reclaiming a frame) page faults: 90
bench-1  |      Voluntary context switches: 256598
bench-1  |      Involuntary context switches: 2
bench-1  |      Swaps: 0
bench-1  |      File system inputs: 2864
bench-1  |      File system outputs: 0
bench-1  |      Socket messages sent: 0
bench-1  |      Socket messages received: 0
bench-1  |      Signals delivered: 0
bench-1  |      Page size (bytes): 4096
bench-1  |      Exit status: 0
bench-1  | echo 8 | time -v ./c_app | wc -l
bench-1  | 100000000
bench-1  |      Command being timed: "./c_app"
bench-1  |      User time (seconds): 4.17
bench-1  |      System time (seconds): 2.18
bench-1  |      Percent of CPU this job got: 99%
bench-1  |      Elapsed (wall clock) time (h:mm:ss or m:ss): 0m 6.39s
bench-1  |      Average shared text size (kbytes): 0
bench-1  |      Average unshared data size (kbytes): 0
bench-1  |      Average stack size (kbytes): 0
bench-1  |      Average total size (kbytes): 0
bench-1  |      Maximum resident set size (kbytes): 2160
bench-1  |      Average resident set size (kbytes): 0
bench-1  |      Major (requiring I/O) page faults: 1
bench-1  |      Minor (reclaiming a frame) page faults: 26
bench-1  |      Voluntary context switches: 263
bench-1  |      Involuntary context switches: 3
bench-1  |      Swaps: 0
bench-1  |      File system inputs: 40
bench-1  |      File system outputs: 0
bench-1  |      Socket messages sent: 0
bench-1  |      Socket messages received: 0
bench-1  |      Signals delivered: 0
bench-1  |      Page size (bytes): 4096
bench-1  |      Exit status: 0
bench-1  | make[1]: Leaving directory '/data/volodarsky'
bench-1  | goos: linux
bench-1  | goarch: amd64
bench-1  | pkg: some/bench
bench-1  | cpu: 12th Gen Intel(R) Core(TM) i7-1260P
bench-1  | BenchmarkGoToFile-16                       20          52814292 ns/op           65656 B/op          4 allocs/op
bench-1  | BenchmarkC_ToFile-16                       20          60146925 ns/op               0 B/op          0 allocs/op
bench-1  | BenchmarkGoToDevFile-16                    20          79052285 ns/op           65656 B/op          4 allocs/op
bench-1  | BenchmarkC_ToDevFile-16                    20          77794164 ns/op               0 B/op          0 allocs/op
bench-1  | BenchmarkGoToStdStream-16                  20          80463698 ns/op           65536 B/op          1 allocs/op
bench-1  | BenchmarkC_ToStdStream-16                  20          79424372 ns/op               0 B/op          0 allocs/op
bench-1  | PASS
bench-1  | ok   some/bench      9.007s
bench-1  | make: Leaving directory '/data'
bench-1 exited with code 0
```