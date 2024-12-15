К вопросу [Как исправить и доработать алгоритм для работы с массивами](https://ru.stackoverflow.com/questions/1602165)

Задача: найти в массиве целых чисел подмассив максимальной длины, в котором число уникальных элементов не превосходит заданное.

Реализованы два алгоритма:
 - `Solver1` пробегает по всем индексам, для каждого индекса строит максимальный подходящий подмассив, начинающийся в заданном индексе.
 - `Solver2` находит заданный подмассив за один проход по массиву, для чего хранится список диапазонов с одинаковыми значениями.