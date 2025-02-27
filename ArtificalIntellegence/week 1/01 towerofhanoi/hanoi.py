import math


class HanoiState:
    def __init__(self, pegs=3, disks=5, state=None, parent=None, action=None):
        self.pegs = pegs
        self.disks = disks
        if state is None:
            array = [tuple([i for i in range(disks, 0, -1)])]
            array.extend([tuple() for _ in range(pegs-1)])
            self.state = tuple(array)
        else:
            self.state = state
        self.parent = parent
        self.action = action

    def top_disk(self, peg):
        if len(self.state[peg]) == 0:
            return math.inf
        else:
            return self.state[peg][-1]

    def next_state(self, from_peg, to_peg):
        if len(self.state[from_peg]) == 0:
            raise ValueError(f"No disks on peg {from_peg}")
        elif self.top_disk(from_peg) > self.top_disk(to_peg):
            raise ValueError(f"Top disk on peg {from_peg} is size {self.state[from_peg][0]} "
                             f"which is larger than peg {to_peg}'s top disk of size {self.state[to_peg][0]}")

        if from_peg < to_peg:
            newstate = self.state[0:from_peg] + \
                       (self.state[from_peg][:-1],) + \
                       self.state[from_peg+1:to_peg] + \
                       (self.state[to_peg] + (self.state[from_peg][-1],),) + \
                       self.state[to_peg+1:]
        else:
            newstate = self.state[0:to_peg] + \
                       (self.state[to_peg] + (self.state[from_peg][-1],),) + \
                       self.state[to_peg+1:from_peg] + \
                       (self.state[from_peg][:-1],) + \
                       self.state[from_peg+1:]

        return HanoiState(self.pegs, self.disks, newstate, self, (from_peg, to_peg))

    def possible_actions(self):
        actions = []
        for from_peg in range(0, self.pegs):
            for to_peg in range(0, self.pegs):
                if self.top_disk(from_peg) < self.top_disk(to_peg):
                    actions.append((from_peg, to_peg))
        return actions

    def is_goal_state(self):
        return len(self.state[-1]) == self.disks

    def __str__(self):
        height = self.disks
        width = math.floor(math.log10(self.disks)) + 2
        out = ""
        for row in range(height-1, -1, -1):
            for peg in range(0, self.pegs):
                if len(self.state[peg]) > row:
                    out += f"{self.state[peg][row]:{width}}"
                else:
                    out += " " * (width-1) + "|"
            out += "\n"
        return out

    def __repr__(self):
        return str(self.state)

    def __eq__(self, other):
        return self.state == other.state


def main():
    state = HanoiState(disks=3)
    print(state)
    state = state.next_state(0, 2)
    print(state)
    state = state.next_state(0, 1)
    print(state)
    state = state.next_state(2, 1)
    print(state)
    state = state.next_state(0, 2)
    print(state)
    state = state.next_state(1, 0)
    print(state)
    state = state.next_state(1, 2)
    print(state)
    state = state.next_state(0, 2)
    print(state)
    print(state.is_goal_state())


if __name__ == "__main__":
    main()
