import math
from .primality import _get_primes, is_prime_general, is_prime


class _LazyStepsGeneratorInternal:
    """
    Ленивый инициализатор шагов для FilteringPrimeChecker.

    Параметры:
        - base_primes: Список базовых простых чисел для фильтрации кандидатов.
    """

    def __init__(self, max_prime: int):
        if not is_prime(max_prime):
            raise ValueError(f"max_prime должно быть простым числом: {max_prime}")
        if max_prime < 3:
            raise ValueError("max_prime must be at least 3")
        if max_prime == 3:
            self.base_primes = [2, 3]
            self.size = 2
            self.steps = [4, 2]
            self.next_index = 2
            self.computed = True
            self._last_residue = 7
            self.primes_mul = 6
            self._smaller_steps_generator = None
            return

        if max_prime == 5:
            self.base_primes = [2, 3, 5]
            self.size = 8
            self.steps = [6, 4, 2, 4, 2, 4, 6, 2]
            self.next_index = 8
            self.computed = True
            self._last_residue = 31
            self.primes_mul = 30
            self._smaller_steps_generator = None
            return

        self.base_primes = _get_primes(max_prime)
        self.size = math.prod(map(lambda p: p - 1, self.base_primes))
        self.steps = [0] * self.size
        self.next_index = 0
        self.computed = False
        self._last_residue = 1
        self.primes_mul = math.prod(self.base_primes)
        self._smaller_steps_iterator = iter(LazyStepsGenerator(self.base_primes[-2]))

    def steps_so_far(self):
        return self.steps[: self.next_index]

    def get(self, index: int) -> int:
        if index < self.next_index:
            return self.steps[index]
        if index >= len(self.steps):
            raise IndexError("Индекс за пределами размера шагов.")
        while self.next_index <= index:
            self.compute_next_step()
        return self.steps[index]

    def compute_next_step(self) -> int | None:
        if self.computed:
            return None
        candidate = self._last_residue
        while candidate < self.primes_mul:
            try:
                candidate += next(self._smaller_steps_iterator)
            except StopIteration:
                self._smaller_steps_iterator = iter(LazyStepsGenerator(self.base_primes[-2]))
                candidate += next(self._smaller_steps_iterator)
            if all(candidate % p != 0 for p in self.base_primes):
                step = candidate - self._last_residue
                self._last_residue = candidate
                self.steps[self.next_index] = step
                self.next_index += 1
                if self._last_residue > self.primes_mul:
                    self.computed = True
                return step
        self.computed = True
        return None


class LazyStepsGenerator:
    """
    Ленивый генератор шагов для FilteringPrimeChecker.

    Параметры:
        - max_prime: Максимальное простое число для базовых простых чисел.
    """

    steps_cache: dict[int, _LazyStepsGeneratorInternal] = {}

    def __init__(self, max_prime: int):
        if max_prime in LazyStepsGenerator.steps_cache:
            self._internal = LazyStepsGenerator.steps_cache[max_prime]
        else:
            self._internal = _LazyStepsGeneratorInternal(max_prime)
            LazyStepsGenerator.steps_cache[max_prime] = self._internal
        self.base_primes = self._internal.base_primes
        self.steps = self._internal.steps

    def __len__(self):
        return self._internal.size

    def __getitem__(self, index: int) -> int:
        return self._internal.get(index)

    def steps_so_far(self):
        return self._internal.steps_so_far()

    def steps_count_so_far(self):
        return self._internal.next_index

    @property
    def computed(self) -> bool:
        return self._internal.computed

    def compute_next_step(self) -> int | None:
        return self._internal.compute_next_step()

    @property
    def _primes_mul(self) -> int:
        return self._internal.primes_mul

    @property
    def _last_residue(self) -> int:
        return self._internal._last_residue


class LazyStepsIterator:
    """
    Итератор для ленивой генерации шагов.
    """

    def __init__(self, lazy_generator: LazyStepsGenerator):
        self._lazy_generator = lazy_generator
        self.index = 0

    def __next__(self):
        if self.index < len(self._lazy_generator):
            step = self._lazy_generator[self.index]
            self.index += 1
            return step
        raise StopIteration

    def __iter__(self):
        return self


class LazyStepsPrimalityTest:
    """
    Проверка простоты чисел с ленивой инициализацией фильтра по простым числам.

    Параметры:
        - max_prime: Максимальное простое число для базовых простых чисел.
    """

    def __init__(self, max_prime: int):
        self._lazy = LazyStepsGenerator(max_prime)
        self.base_primes = self._lazy.base_primes

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
        candidate = 1  # Первое простое число, не входящее в базовые
        while True:
            for step in self._lazy:
                candidate += step
                if candidate > limit:
                    return None
                if n % candidate == 0:
                    return candidate

    def is_prime(self, n):
        """Проверяет, является ли число n простым."""
        return is_prime_general(n, find_divisor_func=self.find_divisor)


lazy_primality_test_11 = LazyStepsPrimalityTest(11)
is_prime_11_lazy = lazy_primality_test_11.is_prime
find_divisor_11_lazy = lazy_primality_test_11.find_divisor

lazy_primality_test_13 = LazyStepsPrimalityTest(13)
is_prime_13_lazy = lazy_primality_test_13.is_prime
find_divisor_13_lazy = lazy_primality_test_13.find_divisor

lazy_primality_test_17 = LazyStepsPrimalityTest(17)
is_prime_17_lazy = lazy_primality_test_17.is_prime
find_divisor_17_lazy = lazy_primality_test_17.find_divisor

lazy_primality_test_19 = LazyStepsPrimalityTest(19)
is_prime_19_lazy = lazy_primality_test_19.is_prime
find_divisor_19_lazy = lazy_primality_test_19.find_divisor
