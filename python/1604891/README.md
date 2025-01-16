К вопросу [Код долго думает после 4 символа](https://ru.stackoverflow.com/questions/1604891)

```python
PASSWORD = 'zxcgg'


async def check_password(attempt):
    print(f"Пробуем: {attempt}")

    if attempt == PASSWORD:
        print(f"Пароль найден: {attempt}")
        return True
    else:
        return False


async def async_bruteforce():
    characters = string.ascii_lowercase
    for length in range(1, len(PASSWORD) + 1):
        tasks = []
        for attempt in itertools.product(characters, repeat=length):
            attempt = ''.join(attempt)
            task = asyncio.create_task(check_password(attempt))
            tasks.append(task)

        # Ждём завершения всех задач текущей длины
        results = await asyncio.gather(*tasks)
        if any(results):
            return
```

Проблема в том, что `await asyncio.gather(*tasks)` создаёт
`26 ** 5 = 11881376` задач, что приводит к очень долгому ожиданию.

Для ускорения можно использовать шаблон `Producer-Consumer`:

```python
# главный процесс
async def task_builder(max_length: int, tasks: asyncio.Queue, password: ResutlStorage[str]):
    """Генератор пробных строк для подбора пароля"""
    print("task_builder: Начало работы")
    for attempt in iterate_passwords(max_length):
        await tasks.put(attempt)
        if password.has_result():
            print(f"task_builder: Пароль найден: {password.get_result()}")
            print("task_builder: Завершение работы")
            return

# Работники
async def task_executor(num: int, tasks: asyncio.Queue, password: ResutlStorage[str]):
    """Потребитель пробных строк, выполняет проверку пароля.
    
    Параметры:
    num -- номер работника
    tasks -- очередь задач
    password -- хранилище результата
    """
    print(f"task_executor {num}: Начало работы")
    while True:
        if password.has_result():
            print(f"task_executor {num}: Пароль найден другим работником")
            print(f"task_executor {num}: Завершение работы")
            return
        attempt = await tasks.get()
        print(f"task_executor {num}: Пробуем: {attempt}")
        if await check_password(attempt):
            print(f"task_executor {num}: Пароль найден: {attempt}")
            password.set_result(attempt)
            print(f"task_executor {num}: Завершение работы")
            return
```

[Полный код](./brute.py)

# Multiprocessing

`asyncio` только на словах асинхронная библиотека. Даже с ключевым словом `async` Пайтон продолжает
выполняться в один поток. Поэтому это решение только прикидывается параллельным, оставаясь в глубине
реализации сугубо синхронным однопоточным. Настоящая параллельность в Пайтоне реализуется только
через `multiprocessing`.

Шаблон `Producer-Consumer` можно реализовать с помощью `multiprocessing`:

```python
def parallel_brute_force(
    number_of_workers: int = Defaults.number_of_workers,
    password_max_length: int = Defaults.password_max_length,
    chunk_size: int = Defaults.chunk_size,
):
    """Параллельный подбор пароля.

    Параметры:
    - number_of_workers -- количество рабочих процессов.
    - password_max_length -- максимальная длина пароля.
    - chunk_size -- количество вариантов паролей, передаваемых рабочему процессу за один раз.
    """
    guess_queue = mp.Queue(number_of_workers)
    result_queue = mp.Queue(1)
    processes = []
    generator = password_generator(password_max_length)

    # запускаем рабочие процессы
    for i in range(number_of_workers):
        p = mp.Process(target=worker, args=(f"w_{i}", guess_queue, result_queue))
        processes.append(p)
        p.start()

    password = None
    while True:
        if not result_queue.empty():
            # пароль найден
            password = result_queue.get()
            print(f"main: Пароль найден: {password}")
            break
        chunk = list(itertools.islice(generator, chunk_size))
        if not chunk:
            # все варианты перебраны
            break
        worker_print(f"main: Передача набора: {chunk[0]} - {chunk[-1]}")
        guess_queue.put(chunk)

    # ставим в очередь рабочим процессам сигнал завершения
    for i in range(number_of_workers):
        guess_queue.put(None)
    # ждем завершения всех процессов
    for p in processes:
        p.join()

    # закрываем очереди
    guess_queue.close()
    result_queue.close()

    # печать результата
    if password is not None:
        print(f"Пароль найден: {password}")
    else:
        print("Пароль не найден")
    return True
```

Работники
```python
_print_allowed = False


def worker_print(*args, **kwargs):  # pylint: disable=redefined-outer-name
    """Печать сообщения в рабочем процессе"""
    if _print_allowed:
        print(*args, **kwargs)


def worker(name: str, queue_in: mp.Queue, queue_out: mp.Queue):
    """Рабочий процесс для проверки паролей.

    Параметры:
    - name -- имя рабочего процесса. Используется для отладки.
    - queue_in -- очередь входных данных. Из этой очереди рабочий процесс получает
      набор вариантов паролей для проверки.
    - queue_out -- очередь выходных данных. Если найден пароль, он будет помещен в эту очередь.
    """
    worker_print(f"worker {name}: Начало работы")
    while True:
        guesses = queue_in.get()
        if guesses is None:
            worker_print(f"worker {name}: Завершение работы")
            return
        worker_print(
            f"worker {name}: Получен набор вариантов {guesses[0]} - {guesses[-1]}",
        )
        for password in guesses:
            if check_password(password):
                worker_print(f"worker {name}: Пароль найден: {password}")
                queue_out.put(password)
                return
```

[Полный код](./multy.py)