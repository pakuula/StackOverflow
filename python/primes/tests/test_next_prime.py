# pylint: disable=missing-function-docstring, missing-module-docstring
from primes.primes import next_prime_forward, next_prime_backward

def test_next_prime_forward():
    assert next_prime_forward(2) == 3
    assert next_prime_forward(3) == 5
    assert next_prime_forward(10) == 11
    assert next_prime_forward(11) == 13
    assert next_prime_forward(14) == 17
    assert next_prime_forward(20) == 23
    assert next_prime_forward(29) == 31
    assert next_prime_forward(100) == 101
    assert next_prime_forward(200) == 211
    assert next_prime_forward(1_000_000_000) == 1000000007
    assert next_prime_forward(87_178_291_183) == 87_178_291_199
    assert next_prime_forward(87_178_291_197) == 87_178_291_199

def test_next_prime_backward():

    assert next_prime_backward(3) == 2
    assert next_prime_backward(5) == 3
    assert next_prime_backward(11) == 7
    assert next_prime_backward(13) == 11
    assert next_prime_backward(17) == 13
    assert next_prime_backward(23) == 19
    assert next_prime_backward(31) == 29
    assert next_prime_backward(101) == 97
    assert next_prime_backward(211) == 199
    assert next_prime_backward(1_000_000_007) == 999999937
    assert next_prime_backward(87_178_291_199) == 87_178_291_183

    try:
        next_prime_backward(2)
    except ValueError:
        assert True, "Нет простых чисел меньше 2."
    else:
        assert False, "Expected ValueError for input 2"