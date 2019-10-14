# TicTacToe

## C
### TODO
- [x] Play Tic Tac Toe
- - [x] Have board
- - [x] Place pieces
- - [x] Determine outcome
- [ ] Create Tic Tac Toe CPU
- - [x] Record previous games
- - [ ] Compare current game to previous games
- - - [x] Rotate board
- - - [x] Check if two boards are identical
- - [ ] Avoid past mistakes
- - - [ ] Determine winning routes from current board
- - - - [x] Check if board contains a sub-board
- - - [ ] Determine losing routes from current board
- - [ ] Pick optimal spot
- - - [ ] Remove all definite losing route options
- - - [ ] Priotize route with highest chance of winning
- - - - [x] Assign weights to routes
- - - [x] Pick spot with best weight

### Structs
1. Bot
  * history - Board[] - Holds past boards
  * gamesRecorded - int - holds the number of boards in history
2. Board
  * size - int - width & height of board
  * length - int - total number of elements in the board
  * spots - char[] - char array representing the game board
  * result - Result - outcome of the board
  * currentTurn - Piece - char of the piece to place on next selected spot
### Enums
1. Piece
  * P1 = 'X'
  * P2 = 'O'
  * Empty = '_'
2. Result
  * Win = 0
  * Lose = 1
  * Tie = 2
  * Neutral = 3
