import numpy as np

class SudokuState:
    # Dictionary mapping (row, col, val) → list of constraints
    # E.g., get_constraints[(r, c, v)] = [
    #   ("Cell", (r, c)),
    #   ("Row", (r, v)),
    #   ("Col", (c, v)),
    #   ("Block", (b, v))
    # ]
    constraint_map = {}

    # Populate constraint_map with (row, col, val) to constraints
    for r in range(9):
        block_y = r // 3
        for c in range(9):
            block_x = c // 3
            block_id = (block_y * 3) + block_x
            for v in range(1, 10):
                constraint_map[(r, c, v)] = [
                    ("Cell", (r, c)),
                    ("Row", (r, v)),
                    ("Col", (c, v)),
                    ("Block", (block_id, v))
                ]

    def __init__(self, initial_values: np.ndarray):
        """
        Initialize a SudokuState with a 9x9 grid.
        Builds the internal matrix (self.a) that tracks constraint → set of valid (r, c, v).
        Also processes any given non-zero cells to remove conflicts.
        """
        self.solvable = True
        self.solution = {}
        self.values = initial_values

        # Matrix 'a' will map each constraint to the set of all possible (r, c, v) entries that satisfy it
        self.a = {
            constraint: set() for constraint in (
                # Every cell must hold a single value
                [("Cell", (row, col)) for row in range(9) for col in range(9)] +
                # Each row must contain each value
                [("Row", (row, val)) for row in range(9) for val in range(1, 10)] +
                # Each column must contain each value
                [("Col", (col, val)) for col in range(9) for val in range(1, 10)] +
                # Each 3x3 block must contain each value
                [("Block", (blk, val)) for blk in range(9) for val in range(1, 10)]
            )
        }

        # Populate self.a with the (r, c, v) sets using our precomputed constraint_map
        for rcv, constraints in SudokuState.constraint_map.items():
            for constraint in constraints:
                self.a[constraint].add(rcv)

        # Remove conflicting possibilities based on the initial puzzle state
        for (row_idx, col_idx), val in np.ndenumerate(initial_values):
            if val != 0:
                try:
                    self._discard_conflicts((row_idx, col_idx, val))
                except KeyError:
                    self.solvable = False

    def _discard_conflicts(self, rcv: (int, int, int)):
        """
        Eliminate invalid (r, c, v) entries caused by choosing this particular (r, c, v).
        Returns a list of removed sets so it can be restored if needed.
        """
        removed_sets = []
        for constraint in SudokuState.constraint_map[rcv]:
            # For every other (r,c,v) that also satisfies this constraint
            for other_rcv in self.a[constraint]:
                # Remove that (r,c,v) from all other constraints to avoid duplication
                for other_constraint in SudokuState.constraint_map[other_rcv]:
                    if other_constraint != constraint:
                        self.a[other_constraint].remove(other_rcv)
            removed_sets.append(self.a.pop(constraint))
        return removed_sets

    def _reinstate_conflicts(self, rcv: (int, int, int), removed_sets):
        """
        Restore previously removed (r, c, v) entries to the matrix.
        This undoes the effect of _discard_conflicts.
        """
        for constraint in reversed(SudokuState.constraint_map[rcv]):
            self.a[constraint] = removed_sets.pop()
            # Reinstate links for each recovered (r,c,v)
            for recovered_rcv in self.a[constraint]:
                for recovered_constraint in SudokuState.constraint_map[recovered_rcv]:
                    self.a[recovered_constraint].add(recovered_rcv)

    def _insert_choice(self, rcv: (int, int, int)):
        """
        Include a chosen (row, col, val) in the solution, and remove the conflicts it imposes on the puzzle.
        Returns the removed sets for potential rollback.
        """
        r, c, v = rcv
        self.solution[(r, c)] = v
        removed_data = self._discard_conflicts(rcv)
        return removed_data

    def _retract_choice(self, rcv: (int, int, int), removed_sets):
        """
        Remove a (row, col, val) from the current solution and restore all previously discarded possibilities.
        """
        r, c, _ = rcv
        del self.solution[(r, c)]
        self._reinstate_conflicts(rcv, removed_sets)

    def _select_constraint(self) -> ((str, (int, int)), set):
        """
        Choose the next constraint with the smallest number of valid (r,c,v) options remaining.
        Returns None if constraints are empty or if puzzle is nearly complete.
        """
        min_count = float('inf')
        chosen_constraint = None

        for constraint, possible_rcvs in self.a.items():
            count = len(possible_rcvs)
            if count < min_count:
                min_count = count
                chosen_constraint = constraint
                if min_count == 1:  # Early exit if we find a constraint with only one choice
                    break
            # If there's a constraint with no possibilities, we might be stuck
            # but we'll let the backtracking handle that.

        return chosen_constraint

    def _completed(self):
        """
        Check if all constraints are satisfied (i.e., no constraints left to fulfill).
        """
        return all(len(rcv_set) == 0 for rcv_set in self.a.values())

    def finalize_solution(self):
        """
        Apply the discovered solution set directly to the Sudoku grid.
        """
        for (row, col), value in self.solution.items():
            self.values[row, col] = value
        return self.values


def sudoku_solver(state: np.ndarray) -> np.ndarray:
    """
    Keep this function name as requested.
    Solves the given Sudoku puzzle if there are empty cells.
    If there's no empty cell or it's unsolvable, returns a 9x9 array filled with -1.
    """
    fallback = np.full((9, 9), fill_value=-1)

    # If there are no empty cells, we assume it's invalid according to this solver
    if np.count_nonzero(state == 0) == 0:
        return fallback

    sudoku_state = SudokuState(state)
    solved_state = _backtracking_search(sudoku_state) if sudoku_state.solvable else None
    return fallback if solved_state is None else solved_state.finalize_solution()


def _backtracking_search(state: SudokuState) -> SudokuState or None:
    """
    Attempt to solve the Sudoku via depth-first backtracking.
    Returns the solved SudokuState or None if no solution is found.
    """
    constraint = state._select_constraint()
    if constraint is None:
        # Either there's nothing to pick (puzzle could be complete or broken)
        # Check if it's truly done
        if state._completed():
            return state
        return None

    # Convert set to a list for iteration
    possible_rcvs = list(state.a[constraint])

    for rcv in possible_rcvs:
        removed_data = state._insert_choice(rcv)

        if state._completed():
            return state

        # Recursively search deeper
        search_result = _backtracking_search(state)
        if search_result is not None:
            return search_result

        # If not successful, revert the choice
        state._retract_choice(rcv, removed_data)

    return None
