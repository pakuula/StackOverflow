"""Поиск лучшей последовательности ходов для белых в игре с нулевой суммой."""

from collections import defaultdict
from typing import Generator, List, Self, Tuple

WHITE = "W"
BLACK = "B"


class Move:
    """Ход в игре: позиция и сторона"""

    def __init__(self, position: int, side: str):
        self.position = position
        self.side = side

    def __hash__(self) -> int:
        return hash((self.position, self.side))

    def __eq__(self, other) -> bool:
        if isinstance(other, Move):
            return self.position == other.position and self.side == other.side
        return False

    def __repr__(self) -> str:
        return f"Move({self.position}, {self.side})"

    def __str__(self) -> str:
        return f"{self.side}:{self.position}"

    @property
    def opponent(self) -> str:
        """Цвет противника"""
        return WHITE if self.side == BLACK else BLACK


class Game:
    """Игра: последовательность ходов, сторона, делающая первый ход, и итоговый счёт"""

    def __init__(self, moves: Tuple[int], side: str, score: Tuple[int]):
        self.moves = moves
        self.side = side
        self.score = score

    def __len__(self) -> int:
        return len(self.moves)

    @property
    def head(self) -> Move:
        """Первый ход"""
        return Move(self.moves[0], self.side)

    @property
    def tail(self) -> Self:
        """Оставшиеся ходы"""
        return Game(self.moves[1:], self.opponent, self.score)

    @property
    def opponent(self) -> str:
        """Цвет противника"""
        return self.head.opponent


class Tree:
    """Дерево игры: ход и оценка хода. Дочерние узлы - возможные продолжения игры.
    Оценка хода - разница в счёте в наихудшем продолжении после данного хода."""

    def __init__(self, move: Move, score: Tuple[int] = None):
        self.move = move
        self.score = score
        self._minimax: int = None
        if score:
            # Если последний ход в игре, то оценка хода - итоговый счёт
            self._minimax = score[0] - score[1]
        self.children: List[Self] = []

    @property
    def minimax(self) -> int:
        """Оценка хода: разница в счёте в наихудшем продолжении после данного хода."""
        if self._minimax is not None:
            return self._minimax
        if not self.children:
            raise ValueError("Листовой узел, но итоговый счёт не задан.")
        if self.move.side == WHITE:
            # Чёрные выберут продолжение с минимальным счётом.
            return min(c.minimax for c in self.children)
        # Белые выберут продолжение с максимальным счётом
        return max(c.minimax for c in self.children)

    def add_child(self, child) -> Self:
        """Добавить дочернее дерево продолжения игры"""
        self.children.append(child)
        return self

    def get_by_minimax(self) -> Self:
        """Возвращает дочернее дерево с наилучшим продолжением игры."""
        minimax = self.minimax
        for child in self.children:
            if child.minimax == minimax:
                return child

    def best_continuations(self) -> Generator[Self, None, None]:
        """Возвращает деревья лучших продолжений."""
        if not self.children:
            return
        minimax = self.minimax
        for child in self.children:
            if child.minimax == minimax:
                yield child

    def best_games(self) -> Generator[Game, None, None]:
        """Возвращает лучшие игры, начинающиеся с данного хода."""
        if not self.children:
            yield Game([self.move.position], self.move.side, self.score)
        else:
            for best in self.best_continuations():
                for bg in best.best_games():
                    yield Game(
                        [self.move.position] + bg.moves,
                        self.move.side,
                        bg.score,
                    )

    def best_game(self) -> Game:
        """Возвращает лучшую игру, начинающуюся с этого хода."""
        if not self.children:
            return Game([self.move.position], self.move.side, self.score)
        child = self.get_by_minimax()
        best_game = child.best_game()
        return Game(
            [self.move.position] + best_game.moves, self.move.side, best_game.score
        )

    def print_tree(self, level=0):
        """Печатает дерево игры"""
        print("| " * level, self.move, self.minimax)
        for child in self.children:
            child.print_tree(level + 1)


class TreeBuilder:
    """Построитель дерева игры из списка игр."""

    def __init__(self, move, games):
        self.games = games
        self.tree = Tree(move)
        self.games_by_move = defaultdict(list)
        self._build_tree()

    def _build_tree(self):
        for game in self.games:
            if len(game) == 1:
                self.tree.add_child(Tree(game.head, game.score))
            else:
                self.games_by_move[game.head].append(game.tail)
        for move, tails in self.games_by_move.items():
            self.tree.add_child(TreeBuilder(move, tails).tree)
        self.tree.children.sort(key=lambda c: c.move.position)


def try_on():
    # список игр из задачи
    game_list = [
        Game((0, 1, 2, 3, 6), WHITE, (30, 34)),
        Game((0, 1, 3, 2, 6), WHITE, (32, 32)),
        Game((0, 1, 6, None, 2, 3), WHITE, (27, 37)),
        Game((0, 1, 6, None, 3, 2), WHITE, (29, 35)),
        Game((1, 2, 0, None, 3, None, 6), WHITE, (36, 28)),
        Game((1, 2, 0, None, 6, None, 3), WHITE, (36, 28)),
        Game((1, 2, 3, 0, 6), WHITE, (27, 37)),
        Game((1, 2, 6, 0, 3), WHITE, (30, 34)),
        Game((1, 3, 0, None, 2, None, 6), WHITE, (35, 29)),
        Game((1, 3, 0, None, 6, None, 2), WHITE, (35, 29)),
        Game((1, 3, 2, 0, 6), WHITE, (25, 39)),
        Game((1, 3, 6, None, 0, None, 2), WHITE, (35, 29)),
        Game((1, 3, 6, None, 2, 0), WHITE, (30, 34)),
        Game((2, 0, 1, 3, 6), WHITE, (22, 42)),
        Game((2, 0, 3, 1, 6), WHITE, (24, 40)),
        Game((2, 0, 6, 3, 1), WHITE, (27, 37)),
        Game((2, 1, 0, 3, 6), WHITE, (30, 34)),
        Game((2, 1, 3, None, 0, None, 6), WHITE, (33, 31)),
        Game((2, 1, 3, None, 6, None, 0), WHITE, (33, 31)),
        Game((2, 1, 6, 3, 0), WHITE, (30, 34)),
        Game((2, 3, 0, 1, 6), WHITE, (28, 36)),
        Game((2, 3, 1, 0, 6), WHITE, (25, 39)),
        Game((2, 3, 6, 0, 1), WHITE, (30, 34)),
        Game((2, 3, 6, 1, 0), WHITE, (30, 34)),
        Game((3, 2, 0, 1, 6), WHITE, (32, 32)),
        Game((3, 2, 1, 0, 6), WHITE, (25, 39)),
        Game((3, 2, 6, None, 0, 1), WHITE, (27, 37)),
        Game((3, 2, 6, None, 1, None, 0), WHITE, (34, 30)),
        Game((6, None, 0, 1, 2, 3), WHITE, (27, 37)),
        Game((6, None, 0, 1, 3, 2), WHITE, (29, 35)),
        Game((6, None, 1, 2, 0, None, 3), WHITE, (36, 28)),
        Game((6, None, 1, 2, 3, None, 0), WHITE, (36, 28)),
        Game((6, None, 1, 3, 0, None, 2), WHITE, (35, 29)),
        Game((6, None, 1, 3, 2, 0), WHITE, (30, 34)),
        Game((6, None, 2, 0, 1, 3), WHITE, (22, 42)),
        Game((6, None, 2, 0, 3, None, 1), WHITE, (33, 31)),
        Game((6, None, 2, 1, 0, 3), WHITE, (27, 37)),
        Game((6, None, 2, 1, 3, None, 0), WHITE, (33, 31)),
        Game((6, None, 2, 3, 0, 1), WHITE, (23, 41)),
        Game((6, None, 2, 3, 1, 0), WHITE, (25, 39)),
        Game((6, None, 3, 2, 0, 1), WHITE, (29, 35)),
        Game((6, None, 3, 2, 1, None, 0), WHITE, (33, 31)),
    ]

    # Дерево игры
    tree = TreeBuilder(Move(None, BLACK), game_list).tree
    for kid in tree.children:
        print(
            f"""Если белые сделают ход {
            kid.move.position}, то разница в итоговом счёте будет {
            kid.minimax}"""
        )

    white_best = max(tree.children, key=lambda c: c.minimax)
    game = white_best.best_game()
    print("Лучшая игра для белых: ", game.moves, game.score)

    best_score = max(tree.children, key=lambda x: x.minimax).minimax
    for white_best in tree.children:
        if white_best.minimax == best_score:
            for best_game in white_best.best_games():
                print("Лучшая игра для белых: ", best_game.moves, best_game.score)

    for kid in tree.children:
        game = kid.best_game()
        print(
            f"Если белые сделают ход {kid.move.position}, то лучшая игра будет ",
            game.moves,
            game.score,
        )

    tree.print_tree()


if __name__ == "__main__":
    try_on()
