"""Моудуль для проверки простоты чисел.

Содержит различные алгоритмы проверки простоты чисел и функции для поиска делителей.

Импортируемые функции:

- is_prime: Общая функция для проверки простоты числа. 
  Проверяет делимость на 2 и затем перебирает нечетные кандидаты в делители.
- is_prime_3: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2 и 3.
- is_prime_5: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2, 3 и 5.
- is_prime_7: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2, 3, 5 и 7.
- is_prime_11: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2, 3, 5, 7 и 11.
  Первый запуск этой функции может быть медленным из-за инициализации списка вычетов
  по модулям простых чисел 2, 3, 5, 7 и 11.
- is_prime_13: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2, 3, 5, 7, 11 и 13.
  Первый запуск этой функции может быть медленным из-за инициализации списка вычетов
  по модулям простых чисел 2, 3, 5, 7, 11 и 13.
- is_prime_17: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2, 3, 5, 7, 11, 13 и 17.
  Первый запуск этой функции может быть медленным из-за инициализации списка вычетов
  по модулям простых чисел 2, 3, 5, 7, 11, 13 и 17.
- is_prime_19: Функция проверки простоты числа,
  которая игнорирует кандидаты в делители, кратные 2, 3, 5, 7, 11, 13, 17 и 19.
  Первый запуск этой функции может быть медленным из-за инициализации списка вычетов
  по модулям простых чисел 2, 3, 5, 7, 11, 13, 17 и 19.
- is_prime_miller_rabin: Функция проверки простоты числа с использованием теста Миллера-Рабина.
"""

from .primality import (
    is_prime,
    is_prime_3,
    is_prime_5,
    is_prime_7,
    is_prime_11,
    is_prime_13,
    is_prime_17,
    is_prime_19,
    is_prime_miller_rabin,
    FilteringPrimeChecker,
    FilteringPrimeCheckerLoader
)
from .primality import (
    find_divisor_3,
    find_divisor_5,
    find_divisor_7,
    find_divisor_11,
    find_divisor_13,
    find_divisor_17,
    find_divisor_19,
)

__all__ = [
    "is_prime",
    "is_prime_3",
    "is_prime_5",
    "is_prime_7",
    "is_prime_11",
    "is_prime_13",
    "is_prime_17",
    "is_prime_19",
    "is_prime_miller_rabin",
    "find_divisor_3",
    "find_divisor_5",
    "find_divisor_7",
    "find_divisor_11",
    "find_divisor_13",
    "find_divisor_17",
    "find_divisor_19",
    "FilteringPrimeChecker",
    "FilteringPrimeCheckerLoader",
]

from .primes import (
    next_prime,
    next_prime_forward,
    next_prime_backward,
    Direction
)

__all__ += [
    "next_prime",
    "next_prime_forward",
    "next_prime_backward",
    "Direction",
]

from .lazy import (
    is_prime_11_lazy,
    is_prime_13_lazy,
    is_prime_17_lazy,
    is_prime_19_lazy,
    LazyStepsGenerator,
    LazyStepsIterator,
    LazyStepsPrimalityTest,
)

__all__ += [
    "is_prime_11_lazy",
    "is_prime_13_lazy",
    "is_prime_17_lazy",
    "is_prime_19_lazy",
    "LazyStepsGenerator",
    "LazyStepsIterator",
    "LazyStepsPrimalityTest",
]