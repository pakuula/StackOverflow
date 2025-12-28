"""Сравнивает производительность различных функций проверки простоты числа."""

from dataclasses import dataclass
import math
from typing import Callable
from primes.benchmark import benchmark
from primes.primality import (
    _get_primes,
    is_prime,
    is_prime_3,
    is_prime_5,
    is_prime_7,
    is_prime_11,
    is_prime_13,
    is_prime_17,
    is_prime_19,
    is_prime_miller_rabin,
)

from .lazy import (
    LazyStepsGenerator,
    is_prime_11_lazy,
    is_prime_13_lazy,
    is_prime_17_lazy,
    is_prime_19_lazy,
)


@dataclass
class _PrimalityCheckerPerformance:
    base: float
    benchmarks: dict[str, float]


def _compare_primality_checkers(
    checker_functions: dict[str, Callable], num: int, rounds: int = 1000
):
    """Сравнивает производительность различных функций проверки простоты числа N.

    :param N: число для проверки на простоту
    :param rounds: количество повторений для усреднения времени
    :return: словарь с именами функций и их средним временем выполнения
    """

    results = {}
    base = benchmark(lambda: is_prime(num), rounds)
    for name, func in checker_functions.items():
        # Обернем функцию, чтобы она принимала N как аргумент
        test_func = lambda: func(num)
        benchmark_result = benchmark(test_func, rounds)
        results[name] = benchmark_result.average_time

    return _PrimalityCheckerPerformance(base=base.average_time, benchmarks=results)


# Benchmark для инициализации ленивых итераторов шагов
def _initialize_lazy_iterators(max_prime=17):
    primes = _get_primes(max_prime)
    size = math.prod(map(lambda p: p - 1, primes))
    lst = [0] * size


def _first_loop_lazy(max_prime=17):
    lazy_gen = LazyStepsGenerator(max_prime)
    primes = _get_primes(max_prime)
    size = math.prod(map(lambda p: p - 1, primes))
    for _ in range(size):
        lazy_gen.compute_next_step()


def demo_lazy(max_prime=17, rounds=10):
    primes = _get_primes(max_prime)
    steps_count = math.prod(p - 1 for p in primes)
    primes_product = math.prod(primes)

    print(
        f"Информация о ленивом генераторе шагов для простых чисел до {max_prime}:"
    )
    print(f"  Базовые простые числа: {primes}")
    print(f"  Произведение простых чисел: {primes_product}")
    print(f"  Количество шагов в цикле: {steps_count}")
    print(
        f"  Размер памяти для хранения шагов: ~{steps_count * 8} байт ({steps_count * 8 / 1024:.2f} KB)"
    )

    # Создаем генератор и показываем первые несколько шагов
    lazy_gen = iter(LazyStepsGenerator(max_prime))
    first_steps = [next(lazy_gen) for _ in range(min(10, steps_count))]
    candidate = 1
    candidates = []
    for step in first_steps:
        candidate += step
        candidates.append(candidate)
    print(f"  Первые {len(first_steps)} шагов: {first_steps}")
    print(f"  Первые {len(candidates)} кандидатов: {candidates}")
    
    _, lazy_init_time = benchmark(lambda: _initialize_lazy_iterators(max_prime), rounds=rounds)
    print(
        f"Инициализация памяти для ленивого итератора шагов для простых чисел до {max_prime}"
        f" заняла {lazy_init_time*1000:.6f} миллисекунд."
    )
    _, lazy_first_loop_time = benchmark(lambda: _first_loop_lazy(max_prime), rounds=rounds)
    print(
        f"Первый проход ленивого итератора шагов для простых чисел до {max_prime}"
        f" занял {lazy_first_loop_time*1000:.6f} миллисекунд."
    )


if __name__ == "__main__":
    from argparse import ArgumentParser

    parser = ArgumentParser(
        description="Сравнивает производительность различных функций проверки простоты числа.",
        epilog="""
Если число N не является простым, будет найдено первое простое число, превосходящее N,
которое будет использоваться для сравнения функций проверки простоты.
        """,
    )
    subparsers = parser.add_subparsers(dest="command", help="Доступные команды")

    # Подкоманда для сравнения производительности
    compare_parser = subparsers.add_parser(
        "benchmark", help="Сравнить производительность функций проверки простоты"
    )
    compare_parser.add_argument(
        "N",
        type=int,
        nargs="?",
        default=87178291199,
        help="Число для проверки на простоту (по умолчанию: 87178291199).",
    )
    compare_parser.add_argument(
        "-r",
        "--rounds",
        type=int,
        default=30,
        help="Количество запусков каждой функции для усреднения времени.",
    )

    # Подкоманда для информации о lazy факторизаторах
    lazy_parser = subparsers.add_parser(
        "lazy", help="Показать информацию о ленивых факторизаторах"
    )
    lazy_parser.add_argument(
        "max_prime",
        type=int,
        nargs="?",
        default=17,
        help="Максимальное простое число для ленивого генератора.",
    )
    lazy_parser.add_argument(
        "-r",
        "--rounds",
        type=int,
        default=10,
        help="Количество запусков для измерения времени инициализации.",
    )

    args = parser.parse_args()

    # Если команда не указана, используем benchmark по умолчанию
    if args.command is None:
        args.command = "benchmark"
        args.N = 87178291199
        args.rounds = 30

    if args.command == "lazy":
        # Вывод информации о ленивых факторизаторах
        demo_lazy(args.max_prime, rounds=args.rounds)
        exit(0)
    N = args.N
    if N < 2:
        raise ValueError("Число N должно быть больше или равно 2.")
    if N % 2 == 0:
        N += 1  # Следующее нечетное число
    while not is_prime_miller_rabin(N):
        N += 2  # Проверяем только нечетные числа
    if args.rounds < 1:
        raise ValueError("Количество запусков должно быть не менее 1.")

    checker_functions = {
        "is_prime_3": is_prime_3,
        "is_prime_5": is_prime_5,
        "is_prime_7": is_prime_7,
        "is_prime_11": is_prime_11,
        "is_prime_11_lazy": is_prime_11_lazy,
        "is_prime_13": is_prime_13,
        "is_prime_13_lazy": is_prime_13_lazy,
        "is_prime_17": is_prime_17,
        "is_prime_17_lazy": is_prime_17_lazy,
        "is_prime_19": is_prime_19,
        "is_prime_19_lazy": is_prime_19_lazy,
        "is_prime_miller_rabin": is_prime_miller_rabin,
    }
    print("Инициализация функций проверки простоты числа...")
    for fun in checker_functions.values():
        fun(1234567)  # Предварительный вызов для инициализации
    print("Инициализация функций проверки простоты числа выполнена.")

    # Умеренно большое простое число для тестирования
    params = {"num": N, "rounds": args.rounds}
    # Большое простое число для тестирования
    # params = {"num": 2305843009213693951, "rounds": 1}
    performance = _compare_primality_checkers(checker_functions, **params)
    # fmt: off
    factor = (  # pylint: disable=invalid-name
        1_000_000
        if performance.base < 1e-3  # Менее миллисекунды
        else 1_000 if performance.base < 1 # Менее секунды
        else 1 # Больше секунды
    )
    # fmt: on

    # pylint: disable=invalid-name
    unit = {1_000_000: "микросек.", 1_000: "миллисек.", 1: "сек."}[factor]
    print(f"Проверка {params['num']}: количество запусков = {params['rounds']}")
    print(f"Базовая функция is_prime: {performance.base * factor:.3f} {unit}")
    for func_name, avg_time in performance.benchmarks.items():
        progress = performance.base / avg_time
        print(
            f"{func_name}: {avg_time * factor:.3f} {unit},"
            f" ускорение {progress:.2f} раз(а)"
        )
