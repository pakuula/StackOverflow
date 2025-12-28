"""Вспомогательный модуль для измерения времени выполнения функций."""
from typing import Callable
import time
from collections import namedtuple

BenchmarkResult = namedtuple("BenchmarkResult", ["total_time", "average_time"])

def benchmark(f: Callable, rounds: int) -> BenchmarkResult:
    """Измеряет время выполнения функции f, вызванной n раз.
    
    Возвращает общее время теста и среднее время на вызов.
    
    :param f: функция для тестирования
    :param rounds: количество вызовов функции
    :return: BenchmarkResult с общим и средним временем выполнения в секундах
    """

    start = time.time()
    for _ in range(rounds):
        f()
    end = time.time()
    dt = end - start
    return BenchmarkResult(total_time=dt, average_time=dt/rounds)
