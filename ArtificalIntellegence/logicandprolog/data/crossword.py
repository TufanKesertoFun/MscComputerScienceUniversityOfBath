import random
from IPython.display import clear_output


class WordBlock:
    def __init__(self, start, end):
        self.start = start
        self.end = end
        self.vertical = start[0] == end[0]
        self.length = end[1] - start[1] if self.vertical else end[0] - start[0]

    def get_spaces(self):
        if self.vertical:
            return [(self.start[0], self.start[1] + i) for i in range(self.length)]
        else:
            return [(self.start[0] + i, self.start[1]) for i in range(self.length)]

    def __repr__(self):
        return f"{self.start} ~ {self.end}"

    def __str__(self):
        return self.__repr__()

    def copy(self):
        return WordBlock(self.start, self.end)

    def shift_back(self, positions):
        if self.vertical:
            start = (self.start[0], self.start[1] - positions)
            end = (self.end[0], self.end[1] - positions)
        else:
            start = (self.start[0] - positions, self.start[1])
            end = (self.end[0] - positions, self.end[1])
        return WordBlock(start, end)


class CrosswordShape:
    def __init__(self, lengths):
        self.lengths = list(lengths)
        random.shuffle(self.lengths)
        self.blocks = []
        self.sites = set()
        self.all_spaces = set()

        self.place_blocks()

    def add_block(self, block):
        for space in block.get_spaces():
            if (space, block.vertical) in self.all_spaces:
                self.sites.add(space)
            else:
                self.all_spaces.add((space, block.vertical))
        self.blocks.append(block)

    # def get_all_spaces(self):
    #     all_spaces = set()
    #     for block in self.blocks:
    #         all_spaces.update(block.get_spaces())
    #     return all_spaces

    def place_blocks(self):
        for block_len in self.lengths:
            if len(self.blocks) == 0:
                start = (0, 0)
                if random.randint(0, 1):
                    end = (0, block_len)
                else:
                    end = (block_len, 0)
                self.add_block(WordBlock(start, end))
            else:
                positions = self.get_candidate_start_positions(block_len)
                if len(positions) == 0:
                    return

                new_block = random.choice(positions)

                self.add_block(new_block)
            # self.print()

    def print(self):
        minx = min([block.start[0] for block in self.blocks] + [block.end[0] for block in self.blocks])
        miny = min([block.start[1] for block in self.blocks] + [block.end[1] for block in self.blocks])
        maxx = max([block.start[0] for block in self.blocks] + [block.end[0] for block in self.blocks])
        maxy = max([block.start[1] for block in self.blocks] + [block.end[1] for block in self.blocks])

        all_spaces = {space[0] for space in self.all_spaces}

        for y in range(miny, maxy+1):
            for x in range(minx, maxx+1):
                if (x, y) in all_spaces:
                    print("X", end="")
                else:
                    print(" ", end="")
            print("")

    def get_neighbours(self, position):
        return {(position[0] + i, position[1]) for i in (-1, 1)}.union({(position[0], position[1] + i) for i in (-1, 1)})

    def get_candidate_start_positions(self, block_len):
        candidates = []
        for block in self.blocks:
            vertical = not block.vertical

            positions = [position for position in block.get_spaces() if position not in self.sites
                                                                    and (position, vertical) not in self.all_spaces]

            if vertical:
                end_positions = [(position[0], position[1] + block_len) for position in positions]
            else:
                end_positions = [(position[0] + block_len, position[1]) for position in positions]

            candidates.extend([WordBlock(start, end) for start, end in zip(positions, end_positions) if end not in self.sites])

        directionless_spaces = {space[0] for space in self.all_spaces}
        filtered_candidates = []
        for cand_block in candidates:
            score = 0
            crossovers = []
            for ix, space in enumerate(cand_block.get_spaces()):
                if (space, cand_block.vertical) in self.all_spaces:
                    score = -1
                    break

                elif ix > 0:
                    if (space, not cand_block.vertical) in self.all_spaces:
                        score += 1
                        crossovers.append(space)

                    if cand_block.vertical:
                        neighbours = {(space[0] + i, space[1]) for i in (-1, 1)}
                    else:
                        neighbours = {(space[0], space[1] + i) for i in (-1, 1)}

                    if len(neighbours.intersection(directionless_spaces)) != 0:
                        score = -1
                        break
            if score >= 0 and len(self.get_neighbours(cand_block.end).intersection(directionless_spaces)) == 0:
                filtered_candidates.append((cand_block, score, crossovers))

        if len(filtered_candidates) == 0:
            return []

        best_score = max([x[1] for x in filtered_candidates])
        best_candidates = [cand for cand in filtered_candidates if cand[1] == best_score]

        # now see if we can shuffle them and keep same number of intersections
        final_candidates = []
        for cand_block, score, crossovers in best_candidates:
            final_candidates.append(cand_block)

            if score == 0:
                shuffle_space = cand_block.length - 1
            else:
                last_crossover = crossovers[-1]
                if cand_block.vertical:
                    shuffle_space = cand_block.end[1] - last_crossover[1]
                else:
                    shuffle_space = cand_block.end[0] - last_crossover[0]

            for shuffle in range(1, shuffle_space+1):
                if cand_block.vertical:
                    space = cand_block.start[0], cand_block.start[1] - shuffle
                    neighbours = {(space[0] + i, space[1]) for i in (-1, 1)}
                    neighbours.add((space[0], space[1] - 1))
                else:
                    space = cand_block.start[0] - shuffle, cand_block.start[1]
                    neighbours = {(space[0], space[1] + i) for i in (-1, 1)}
                    neighbours.add((space[0] - 1, space[1]))

                if len(neighbours.intersection(directionless_spaces)) != 0:
                    break

                final_candidates.append(cand_block.shift_back(shuffle))

        return final_candidates


def generate_good_cross(gen_num, word_lens):
    crosswords = []
    for i in range(0, gen_num):
        crossword = CrosswordShape(word_lens)
        if len(crossword.blocks) == len(word_lens):
            crosswords.append(crossword)
    return max(crosswords, key=lambda x: len(x.sites))


def main():
    print("What is your username?")
    username = input("> ")

    random.seed(username)

    gen_num = 50

    print("Here is your first layout:")
    result = generate_good_cross(gen_num, (6, 5, 5, 4, 4))
    result.print()

    print("Here is your second layout:")
    result = generate_good_cross(gen_num, (7, 6, 6, 6, 5, 5))
    result.print()


if __name__ == "__main__":
    main()
