"""Тесты для ленивой генерации шагов."""

from primes.lazy import (
    LazyStepsGenerator,
    LazyStepsIterator,
    LazyStepsPrimalityTest,
)

from primes.primes import primes_500


def test_lazy_step_iteration_3():
    generator = LazyStepsGenerator(3)
    iterator = LazyStepsIterator(generator)
    expected_steps = [4, 2]
    for expected_step in expected_steps:
        step = next(iterator)
        assert step == expected_step
    try:
        next(iterator)
    except StopIteration:
        assert True, "Итерация завершена."
    else:
        assert False, "Ожидалось исключение StopIteration."

def test_lazy_step_iteration_5():
    generator = LazyStepsGenerator(5)
    iterator = LazyStepsIterator(generator)
    expected_steps = [6, 4, 2, 4, 2, 4, 6, 2]
    for expected_step in expected_steps:
        step = next(iterator)
        assert step == expected_step
    try:
        next(iterator)
    except StopIteration:
        assert True, "Итерация завершена."
    else:
        assert False, "Ожидалось исключение StopIteration."

def test_lazy_step_iteration_5_multiple_iterators():
    generator = LazyStepsGenerator(5)
    iterator1 = LazyStepsIterator(generator)
    iterator2 = LazyStepsIterator(generator)
    expected_steps = [6, 4, 2, 4, 2, 4, 6, 2]
    for expected_step in expected_steps:
        step1 = next(iterator1)
        step2 = next(iterator2)
        assert step1 == expected_step
        assert step2 == expected_step
    try:
        next(iterator1)
    except StopIteration:
        assert True, "Итерация iterator1 завершена."
    else:
        assert False, "Ожидалось исключение StopIteration для iterator1."
    try:
        next(iterator2)
    except StopIteration:
        assert True, "Итерация iterator2 завершена."
    else:
        assert False, "Ожидалось исключение StopIteration для iterator2."

def test_lazy_step_iteration_7():
    generator = LazyStepsGenerator(7)
    iterator = LazyStepsIterator(generator)
    residues = [1] + [p for p in range(3, 212) if all(p % bp != 0 for bp in [2, 3, 5, 7])]
    expected_steps = [b-a for a, b in zip(residues, residues[1:])] 
    for expected_step in expected_steps:
        step = next(iterator)
        assert step == expected_step
    try:
        next(iterator)
    except StopIteration:
        assert True, "Итерация завершена."
    else:
        assert False, "Ожидалось исключение StopIteration."

def test_lazy_prime_17():
    checker = LazyStepsPrimalityTest(17)
    for p in primes_500:
        assert checker.is_prime(p) is True, f"Failed for prime {p}"
    n = 482431
    d = checker.find_divisor(n)
    assert d == 613, f"Failed to find divisor for {n} using lazy checker"
    assert checker.is_prime(n) is False, f"Failed primality test for {n}"
    n = 899
    d = checker.find_divisor(n)
    assert d == 29, f"Failed to find divisor for {n} using lazy checker"
    assert checker.is_prime(n) is False, f"Failed primality test for {n}"
    for p1, p2 in zip(primes_500, primes_500[1:]):
        n = p1*p2
        d = checker.find_divisor(n)
        assert d == p1, f"Failed to find divisor for {n} using lazy checker: expected {p1}, got {d}"
