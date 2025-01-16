"""Пример перебора пароля с использованием asyncio
К вопросу https://ru.stackoverflow.com/questions/1604891
"""
import asyncio
import string
import time

PASSWORD = 'zxcg'

async def check_password(attempt):
    # print(f"Пробуем: {attempt}")
    await asyncio.sleep(0) # имитация задержки
    if attempt == PASSWORD:
        print(f"Пароль найден: {attempt}")
        return True
    else:
        return False

class ResutlStorage[T]:
    """Хранилище для результата поиска. Минимальная реализация, not thread safe"""
    def __init__(self):
        self.result : T = None # pylint: disable=undefined-variable
        self.found = False
    
    def set_result(self, result: T) -> None: # pylint: disable=undefined-variable
        """Сохранить результат, выставить флаг готовности"""
        self.result = result
        self.found = True
    
    def has_result(self) -> bool:
        """Вернуть True если результат найден."""
        return self.found
    
    def get_result(self) -> T: # pylint: disable=undefined-variable
        """Вернуть найденный результат"""
        return self.result

def iterate_passwords_fixed_length(length: int):
    """Генератор строк фиксированной длины"""
    print(f"iterate_passwords_fixed_length: len={length}")
    characters = string.ascii_lowercase
    if length == 0:
        yield ''
    else:
        for password in iterate_passwords_fixed_length(length - 1):
            for char in characters:
                yield password + char

def iterate_passwords(max_length: int):
    """Генератор строк длиной не более заданной"""
    print(f"iterate_passwords: length={max_length}")
    for length in range(1, max_length+1):
        yield from iterate_passwords_fixed_length(length)

async def task_builder(max_length: int, tasks: asyncio.Queue, password: ResutlStorage[str]):
    """Генератор пробных строк для подбора пароля"""
    print("task_builder: Начало работы")
    for attempt in iterate_passwords(max_length):
        await tasks.put(attempt)
        if password.has_result():
            print(f"task_builder: Пароль найден: {password.get_result()}")
            print("task_builder: Завершение работы")
            return

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

async def async_bruteforce(max_length: int, nworkers: int = 16):
    """Настраивает и запускает асинхронный перебор паролей
    Параметры:
    max_length -- максимальная длина пароля
    nworkers -- количество рабочих потоков для перебора паролей
    """
    tasks = asyncio.Queue(nworkers)
    password_store = ResutlStorage[str]()
    task_producer = task_builder(max_length, tasks, password_store)
    workers = [task_executor(i, tasks, password_store) for i in range(nworkers)]
    await asyncio.gather(task_producer, *workers)

print("\n Асинхронный перебор пароля:")
async_start_time = time.time()
asyncio.run(async_bruteforce(8))
print(f"Время асинхронного перебора: {time.time() - async_start_time:.2f} секунд")
