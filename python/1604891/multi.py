"""Многопроцессорный подбор пароля. Игрушечный пример."""

import argparse
import itertools
import string
import multiprocessing as mp
import time

# Пароль, который нужно найти
_password = "zxcgg"


# Функция проверки предполагаемого пароля
def check_password(password: str) -> bool:
    """Проверка пароля"""
    return password == _password


def password_generator(max_length: int):
    """Генерация всех возможных паролей заданной длины"""

    def fixed_length_generator(length: int):
        chars = string.ascii_lowercase
        for password in itertools.product(chars, repeat=length):
            yield "".join(password)

    for length in range(1, max_length + 1):
        yield from fixed_length_generator(length)


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


class Defaults:
    """Параметры по умолчанию"""

    password_max_length = 8
    number_of_workers = mp.cpu_count() - 1
    chunk_size = 1000


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


if __name__ == "__main__":
    argparser = argparse.ArgumentParser(description="Параллельный подбор пароля")
    argparser.add_argument(
        "--max-length",
        "-l",
        type=int,
        default=Defaults.password_max_length,
        help=f"Максимальная длина пароля (по умолчанию {Defaults.password_max_length})",
    )
    argparser.add_argument(
        "--workers",
        "-w",
        type=int,
        default=Defaults.number_of_workers,
        help=f"Количество рабочих процессов (по умолчанию {Defaults.number_of_workers})",
    )
    argparser.add_argument(
        "--chunk-size",
        "-c",
        type=int,
        default=Defaults.chunk_size,
        help=f"Количество вариантов паролей, передаваемых рабочему процессу за один раз (по умолчанию {Defaults.chunk_size})",
    )
    argparser.add_argument(
        "--verbose",
        "-v",
        action="store_true",
        help="Вывод отладочной информации",
    )
    args = argparser.parse_args()

    print("Параллельный подбор пароля:")
    print("Параметры:")
    print(f"  Максимальная длина пароля: {argparser.parse_args().max_length}")
    print(f"  Количество рабочих процессов: {argparser.parse_args().workers}")
    print(
        f"  Число вариантов, передаваемых в рабочий процесс: {argparser.parse_args().chunk_size}"
    )
    _print_allowed = args.verbose

    start_time = time.time()
    parallel_brute_force(
        number_of_workers=argparser.parse_args().workers,
        password_max_length=argparser.parse_args().max_length,
        chunk_size=argparser.parse_args().chunk_size,
    )
    print(f"Время параллельного перебора: {time.time() - start_time:.2f} секунд")
