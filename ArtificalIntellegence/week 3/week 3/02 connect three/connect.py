import numpy as np
import utils


class Connect:
    def __init__(self, starting_player="x", num_cols=5, num_rows=3, num_connect=3, verbose=True):
        """
        Define the agent's environment -- a new Connect object.

        All variables/properties that start with an underscore are for internal use only.
        You will probably only need to access the .grid, .available_actions, and .active_player
        variables.
        """
        # Game/board/grid parameters.
        self._num_cols = num_cols
        self._num_rows = num_rows
        self._num_connect = num_connect
        self._verbose = verbose
        self._players = ['o', 'x']
        self._other_player = {'o': 'x', 'x': 'o'}  # This is for changing between players, e.g.,  self._other_player['o'] yields 'x'.
        
        # Define two-dimensional array of strings to represent the grid.
        self.grid = np.full(fill_value=" ", shape=(self._num_rows, self._num_cols), dtype=str)

        # Define which columns are NOT full yet. Each column index is one action.
        self.available_actions = np.arange(self._num_cols)

        # Define who plays first.
        self._starting_player = starting_player
        if self._starting_player not in self._players:
            raise ValueError("The argument starting_player has to be either 'x', or 'o'.")
        else:
            self.active_player = self._starting_player

        self.reset()

    def reset(self):
        """
        Clears the board (and resets associated internal variables)
        and chooses the starting player as given by the
        self._starting_player variable.
        """

        # Change to first player.
        self.active_player = self._starting_player

        # Make grid empty.
        self.grid = np.full(fill_value=" ", shape=(self._num_rows, self._num_cols), dtype=str)

        # Reset available actions to all columns
        self.available_actions = np.arange(self._num_cols)

        # Keep track of the lowest free row position per column (where a disk would land if dropped in that column)
        # You can ignore this; for internal use only.
        self._lowest_free_row_per_column = np.zeros(self._num_cols, dtype=int)

        # Keep track of the last action played (simplifies checking for terminal states).
        # You can ignore this; for internal use only.
        self._last_action = None

        if self._verbose:
            print("Game has been reset.")
            print(self.grid[::-1, ])

    def change_turn(self):
        self.active_player = self._other_player[self.active_player]

    def act(self, action):
        """
        Given an action (a column index; known to be a valid action!), generate the new board

        :param action: an integer referring to the column index where a new token/disk should be dropped
        :return (reward, game_over):
            reward is an integer that indicates the reward for player `o`.
            game_over is a boolean indicating well... whether the game is over or not.
        """

        self.grid[self._lowest_free_row_per_column[action], action] = self.active_player
        self._lowest_free_row_per_column[action] += 1  # You can ignore this; internal use only.
        if self._lowest_free_row_per_column[action] == self._num_rows:
            self.available_actions = np.setdiff1d(self.available_actions, action)
        self._last_action = action  # You can ignore this; internal use only.

        if self._verbose:
            print(self.grid[::-1, ])

        # if self.was_winning_move():
        #     game_over = True
        #     winner = self.active_player
        # elif self.grid_is_full():
        #     game_over = True
        #     winner = "draw"
        # else:
        #     game_over = False
        #     winner = "nobody"
        # # Check if step led to win of current player or whether the board is full (= draw!).
        if self.was_winning_move():
            game_over = True
            if self.active_player == "o":
                reward = 1
            else:
                reward = -1
        elif self.grid_is_full():
            game_over = True
            reward = 0
        else:
            game_over = False
            reward = 0

        self.change_turn()

        return reward, game_over

    def grid_is_full(self):
        return np.all(self._lowest_free_row_per_column == self._num_rows)

    def was_winning_move(self):
        """
        Check if the move that has just been made wins the game.

        Determine in which row the disk (token) landed using self._last_action and look at that row,
        column and both diagonals including this token. Check whether there is any sequence of
        length 'num_connect' of the same token type.

        For example, if num_connect == 3

        ' 'd' ' ' 'c' ' ' 'u' ' '
        ' ' ' 'd' 'c' 'u' ' ' ' '
        ' 'r' 'r' 'x' 'r' 'r' ' '
        ' ' ' 'u' 'c' 'd' ' ' ' '
        ' 'u' ' ' 'c' ' ' 'd' ' '
        ' ' ' ' ' ' ' ' ' ' ' ' '
        ' ' ' ' ' ' ' ' ' ' ' ' '

        and "x" is the position the token has dropped, check whether there is a sequence of 'x' of length 3
        in the corresponding row (r), column (c), upward-diagonal (u), or downward diagonal (d).

        [This function could be made MUCH more efficient by excluding some of the checks beforehand, for
         example, based on the row height of the last_action.]

        :return: a boolean, True if the last move was a winning move
        """
        game_is_won = False

        action_row = self._lowest_free_row_per_column[self._last_action] - 1
        action_col = self._last_action
        winning_sequence = np.full(shape=self._num_connect, fill_value=self.active_player)

        # Calculate candidate vectors
        row_candidates = self.grid[action_row, max(0, action_col - self._num_connect + 1) : min(self._num_cols, action_col + self._num_connect)]
        if utils.search_sequence_numpy(row_candidates, winning_sequence):
            game_is_won = True
        else:
            col_candidates = self.grid[max(0, action_row - self._num_connect + 1): min(self._num_rows, action_row + self._num_connect), action_col]
            if utils.search_sequence_numpy(col_candidates, winning_sequence):
                game_is_won = True
            else:
                diag_index_up = action_col - action_row
                diag_up_candidates = np.diagonal(self.grid, diag_index_up)
                if utils.search_sequence_numpy(diag_up_candidates, winning_sequence):
                    game_is_won = True
                else:
                    diag_index_down = action_row + action_col - (self._num_rows - 1)
                    diag_down_candidates = np.diagonal(self.grid[::-1], diag_index_down)
                    if utils.search_sequence_numpy(diag_down_candidates, winning_sequence):
                        game_is_won = True

        if self._verbose and game_is_won:
            print("Player '", self.active_player, "' has won the game!")
        return game_is_won

