# sudoku
An Android app for playing Sudoku.

## How to Play
Sudoku is played on a square grid, with each cell in the grid containing a number. The game starts  
with some of the cells filled in with numbers. The objective is to fill in the rest of the cells 
without creating duplicates in any row, column, or quadrant. The game is won when all cells have a 
number.

![sample Sudoku game board](https://wpc.puzzles.com/sudoku/2009samples/is-6.gif "Sudoku board")

## Terminology
The *game board* must be a square, and the *size* of the sides must be a perfect square. The most 
common board configuration is 9x9. The square root of the size is called the *rank*. Thus, the 
most common configuration has rank 3.

The game board is divided into smaller square grids called *blocks*. The number of blocks on a 
board will be the size of the board.

No move by the player is allowed to result in a number appearing twice in a given row, column, or 
block. This is called the One Rule.
