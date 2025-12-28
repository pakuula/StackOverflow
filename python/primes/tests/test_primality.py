# pylint: disable=missing-function-docstring, missing-module-docstring
from primes import (
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

from primes.lazy import (
    is_prime_11_lazy,
    is_prime_13_lazy,
    is_prime_17_lazy,
    is_prime_19_lazy,
)

# N_test = 87178291199  # простое число
# print(N_test, find_divisor_2_3(N_test) is None, find_divisor(N_test))
# N_test = 11*13*17  # составное число
# print(N_test, find_divisor_2_3(N_test), find_divisor(N_test))
# exit(0)

_primality_tests = [
    is_prime,
    is_prime_3,
    is_prime_5,
    is_prime_7,
    is_prime_11,
    is_prime_13,
    is_prime_17,
    is_prime_19,
    
    is_prime_11_lazy,
    is_prime_13_lazy,
    is_prime_17_lazy,
    is_prime_19_lazy,
    
    is_prime_miller_rabin,
]


def test_87178291199():
    n_test = 87178291199  # простое число
    for tester in _primality_tests:
        assert tester(n_test) is True, f"Failed for {tester.__name__}"


def test_composite():
    n_test = 2683 * 2687  # составное число
    for tester in _primality_tests:
        assert tester(n_test) is False, f"Failed for {tester.__name__}"


def test_small_primes():
    small_primes = [2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47]
    for n_test in small_primes:
        for tester in _primality_tests:
            assert tester(n_test) is True, f"Failed for {tester.__name__} with {n_test}"
