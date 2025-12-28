"""Содержит несколько реализаций проверки простоты числа."""

import math
import random
from typing import Tuple

primes_up_to_1000 = set((2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 
                         53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 
                         109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 
                         173, 179, 181, 191, 193, 197, 199, 211, 223, 227, 229, 
                         233, 239, 241, 251, 257, 263, 269, 271, 277, 281, 283, 
                         293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 
                         367, 373, 379, 383, 389, 397, 401, 409, 419, 421, 431, 
                         433, 439, 443, 449, 457, 461, 463, 467, 479, 487, 491, 
                         499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 
                         577, 587, 593, 599, 601, 607, 613, 617, 619, 631, 641, 
                         643, 647, 653, 659, 661, 673, 677, 683, 691, 701, 709, 
                         719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 
                         797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 
                         863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 
                         947, 953, 967, 971, 977, 983, 991, 997))


def find_divisor(n: int) -> int | None:
    """
    Находит минимальный нетривиальный делитель числа n методом перебора.
    Если такой делитель не найден, возвращает None.
    
    :param n: Число для поиска делителя.
    :return: Минимальный нетривиальный делитель числа n или None.
    """
    if n < 2:
        raise ValueError(f"n должно быть >= 2: {n}")
    if n == 2:
        return None
    if n % 2 == 0:
        return 2
    limit = math.isqrt(n)
    for i in range(3, limit + 1, 2):
        if n % i == 0:
            return i
    return None

def is_prime_general(n: int, find_divisor_func=find_divisor) -> bool:
    """
    Поверяет, является ли число простым, используя заданную функцию поиска делителя.
    
    Если делитель не найден, число простое.
    
    :param n: Число для проверки.
    :param find_divisor_func: Функция для поиска делителя числа. Возвращает делитель или None.
    :return: True, если число простое, иначе False.
    """
    if n < 2:
        return False
    divisor = find_divisor_func(n)
    return divisor is None

def is_prime(n: int) -> bool:
    """
    Поверяет, является ли число простым, используя общий метод поиска делителя.
    """
    return is_prime_general(n, find_divisor_func=find_divisor)

steps_2_3 = [2,4]
# Проверка на простоту, игнорируя кандидаты, которые делятся на 2, 3
def find_divisor_3(n: int) -> int | None:
    """
    Находит минимальный нетривиальный делитель числа n, игнорируя кандидаты, делящиеся на 2 и 3.
    """
    if n < 2:
        raise ValueError(f"n должно быть >= 2: {n}")
    if n in (2, 3):
        return None
    for p in (2, 3):
        if n % p == 0:
            return p
    limit = math.isqrt(n)
    candidate = 5
    while candidate <= limit:
        for step in steps_2_3:
            if n % candidate == 0:
                return candidate
            candidate += step
    return None

def is_prime_3(n: int) -> bool:
    """
    Проверяет, является ли число n простым, используя метод поиска делителей, 
    который игнорирует кандидаты, делящиеся на 2 и 3.
    """
    return is_prime_general(n, find_divisor_func=find_divisor_3)

residues_2_3_5 = [1,7,11,13,17,19,23,29]
steps_2_3_5 = [b-a for a, b in zip(residues_2_3_5[:-1], residues_2_3_5[1:])] + [2]
step_count_2_3_5 = len(steps_2_3_5)

def find_divisor_5(n: int) -> int | None:
    """
    Находит минимальный нетривиальный делитель числа n, игнорируя кандидаты, делящиеся на 2, 3 и 5.
    
    :param n: Число для поиска делителя.
    :return: Минимальный нетривиальный делитель числа n или None.
    """
    if n < 2:
        raise ValueError(f"n должно быть >= 2: {n}")
    
    for p in (2, 3, 5):
        if n % p == 0:
            return None if n == p else p
    limit = math.isqrt(n)
    candidate = 1
    
    while True:
        for step in steps_2_3_5:
            candidate += step
            if candidate > limit:
                return None
            if n % candidate == 0:
                return candidate

def is_prime_5(n: int) -> bool:
    """
    Проверяет, является ли число n простым, используя метод поиска делителей, 
    который игнорирует кандидаты, делящиеся на 2, 3 и 5.
    """
    return is_prime_general(n, find_divisor_func=find_divisor_5)


class FilteringPrimeChecker:
    """
    Проверяет простоту числа, фильтруя кандидатов на делители по модулю набора базовых простых чисел.
    
    Параметры:
        - base_primes: Список базовых простых чисел для фильтрации кандидатов.
    """
    def __init__(self, max_prime: int):
        if not is_prime(max_prime):
            raise ValueError(f"max_prime должно быть простым числом: {max_prime}")
        self.base_primes = _get_primes(max_prime)
        self.steps = self._init_steps(self.base_primes)
        
    @staticmethod
    def _init_steps(base_primes: list[int]) -> list[int]:
        primes_mul = 1
        for p in base_primes:
            primes_mul *= p

        steps = []
        last_residue = 1
        residue = 3

        while residue < primes_mul:
            if all(residue % p != 0 for p in base_primes):
                steps.append(residue - last_residue)
                last_residue = residue
            residue += 2
        # шаг от -1 до 1 по модулю primes_mul
        steps += [2]
        return steps

    def find_divisor(self, n):
        """Находит делитель числа n, используя фильтрацию по базовым простым числам."""
        if n < 2:
            raise ValueError(f"n должно быть >= 2: {n}")
        if n in self.base_primes:
            return None
        for p in self.base_primes:
            if n % p == 0:
                return p
        limit = math.isqrt(n)
        candidate = 1 # Первое простое число, не входящее в базовые
        while True:
            for step in self.steps:
                candidate += step
                if candidate > limit:
                    return None
                if n % candidate == 0:
                    return candidate
    
    def is_prime(self, n):
        """Проверяет, является ли число n простым."""
        return is_prime_general(n, find_divisor_func=self.find_divisor)

def _get_primes(max_prime: int) -> list[int]:
    """Возвращает список простых чисел до max_prime включительно."""
    if max_prime < 2:
        return []
    primes = [2]
    for num in range(3, max_prime + 1, 2):
        if is_prime(num):
            primes.append(num)
    return primes

class FilteringPrimeCheckerLoader:
    """
    Ленивый инициализатор FilteringPrimeChecker.
    
    Параметры:
        - max_prime: Максимальное простое число для фильтрации кандидатов.
    """
    def __init__(self, max_prime):
        if not is_prime(max_prime):
            raise ValueError(f"max_prime должно быть простым числом: {max_prime}")
        self.max_prime = max_prime
        self._checker = None

    @property
    def checker(self):
        if self._checker is None:
            self._checker = FilteringPrimeChecker(self.max_prime)
        return self._checker

    def find_divisor(self, n):
        """Находит делитель числа n, используя фильтрацию по базовым простым числам."""
        return self.checker.find_divisor(n)

    def is_prime(self, n):
        """Проверяет, является ли число n простым.
        
        Проверка осуществляется перебором кандидатов в делители.
        
        При проверке игнорирует кандидаты в делители, кратные базовым простым числам.
        """
        return self.checker.is_prime(n)

_primality_tester_7 = FilteringPrimeCheckerLoader(7)
find_divisor_7 = _primality_tester_7.find_divisor
is_prime_7 = _primality_tester_7.is_prime

_primality_tester_11 = FilteringPrimeCheckerLoader(11)
find_divisor_11 = _primality_tester_11.find_divisor
is_prime_11 = _primality_tester_11.is_prime

_primality_tester_13 = FilteringPrimeCheckerLoader(13)
find_divisor_13 = _primality_tester_13.find_divisor
is_prime_13 = _primality_tester_13.is_prime

_primality_tester_17 = FilteringPrimeCheckerLoader(17)
find_divisor_17 = _primality_tester_17.find_divisor
is_prime_17 = _primality_tester_17.is_prime

_primality_tester_19 = FilteringPrimeCheckerLoader(19)
find_divisor_19 = _primality_tester_19.find_divisor
is_prime_19 = _primality_tester_19.is_prime

def is_prime_miller_rabin(n, k=None):
    """Проверяет, является ли число n простым с помощью теста Миллера-Рабина.
    
    :param n: Число для проверки.
    :param k: Количество раундов проверки (чем больше, тем выше надежность).
    :return: True, если число вероятно простое, иначе False.
    """
    if n < 2:
        return False
    # Проверка делимости на малые простые числа
    small_primes = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47]
    for p in small_primes:
        if n == p:
            return True
        if n % p == 0:
            return False
    if k is None:
        k = max(5, n.bit_length() // 2)  # Автоматический выбор количества раундов
    # Представляем n-1 в виде d*(2^r)
    r, d = 0, n - 1
    while d % 2 == 0:
        d //= 2
        r += 1

    # Проведение k раундов проверки
    for _ in range(k):
        a = random.randint(2, n - 2)
        x = pow(a, d, n)
        if x == 1 or x == n - 1:
            # x равен 1 или -1 - нет смысла возводить в квадрат
            continue
        for _ in range(r - 1):
            # Возводим x в квадрат по модулю n
            x = pow(x, 2, n)
            if x == n - 1:
                # если x^2 ≡ -1 (mod n), то x^4 ≡ 1 (mod n)
                # это нам не даёт оснований считать n составным
                break
            if x == 1:
                # если x^2 ≡ 1 (mod n), но x не равен ±1, следовательно n составное
                return False
        else:
            # после всех итераций должно получиться число a^(n-1).
            # Мы должны были выйти из цикла по x ≡ 1 (mod n), но это не произошло.
            # значит, a^(n-1) !≡ 1 (mod n), и n составное
            return False
    # Если во всех попытках n не было отброшено как составное, считаем его простым
    return True

