import 'dart:math';

import 'package:flutter/material.dart';
import 'package:neural_network/neural_network.dart';

class Board {
  static Random r = Random();
  List<int> positions = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  List<int> available = [0, 1, 2, 3, 4, 5, 6, 7, 8];
  String piece1 = "X";
  String piece2 = "O";
  Color piece1Color = Colors.blue;
  Color piece2Color = Colors.blue;
  int currentTurn = 1;
  int _winner;


  // List<int> get row1 => [positions[0], positions[1], positions[2]];
  // List<int> get row2 => [positions[3], positions[4], positions[5]];
  // List<int> get row3 => [positions[6], positions[7], positions[8]];

  // List<int> get col1 => [positions[0], positions[3], positions[6]];
  // List<int> get col2 => [positions[1], positions[4], positions[7]];
  // List<int> get col3 => [positions[2], positions[5], positions[8]];

  List<int> get diag1 => [positions[0], positions[4], positions[8]];
  List<int> get diag2 => [positions[2], positions[4], positions[6]];
  String get currentPiece => currentTurn == 1 ? piece1 : piece2;

  List<int> getRow(int index) => [positions[index * 3], positions[1 + index * 3], positions[2 + index * 3]];
  List<int> getCol(int index) => [positions[index], positions[3 + index], positions[6 + index]];
  List<int> getDiagonal(int index) {
    int index1 = index * 2;
    int index2 = index * 4 + (1 - index) * 4;
    int index3 = index * 6 + (1 - index) * 8;
    return index < 2 ? [positions[index1], positions[index2], positions[index3]] : [0];
  }

  String getPiece(int index) => (positions[index] == 1) ? piece1 : (positions[index] == -1 ? piece2 : null);

  bool isFinished() {
    // If someone has won the game is done
    int winner = getWinner();
    if (winner != 0) {
      return true;
    }
    // If all the spots are filled the game is done
    bool allAreFilled = this.positions.every((element) => element != 0);
    return allAreFilled;
  }

  int getWinningSpot() {
    Board copy = Board()..positions = this.positions.toList();
    for (int i = 0; i < 9; i++) {
      if(copy.pickSpot(i)){
        if (copy.getWinner() == this.currentTurn){
          return i;
        } else {
          copy = Board()..positions = this.positions.toList();
        }
      }
    }
    return -1;
  }

  int getFirstDifferent(List<int> items) {
    int diffCount = 0;
    int index;
    for (int i = 0; i < items.length; i++) {
      if (items[i] != items[0]) {
        diffCount++;
        index ??= i;
      }
    }
    if (diffCount == items.length - 1) return 0;
    return index ?? -1;
  }

  bool onlyOneIsDifferent(List<int> items) {
    int diffCount = 0;
    for (int i = 0; i < items.length; i++) {
      if (items[i] != items[0]) diffCount++;
    }
    if (diffCount == items.length - 1) return true;
    return diffCount == 1;
  }

  /// returns 1 if player1 wins, -1 if player2 wins, 0 otherwise
  int getWinner() {
    // if (_winner != null) return _winner;
    for (int i = 0; i < 3; i++) {
      List<int> row = getRow(i);

      if (row[0] != 0 && row.every((element) => element == row[0])) {
        _winner = row[0];
        return row[0];
      }

      List<int> col = getCol(i);
      if (col[0] != 0 && col.every((element) => element == col[0])) {
        _winner = col[0];
        return col[0];
      }

      List<int> diag = getDiagonal(i);
      if (diag[0] != 0 && diag.every((element) => element == diag[0])) {
        _winner = diag[0];
        return diag[0];
      }
    }

    // _winner = 0;
    return 0;
  }

  String getWinnerPiece() {
    switch (this.getWinner()) {
      case 1:
        return this.piece1;
      case -1:
        return this.piece2;
      default:
        return "";
    }
  }

  int spotFromXY(int x, int y) => x + y * 3;

  bool pickSpot(int index) {
    if (positions[index] != 0 || isFinished()) return false;
    this.positions[index] = this.currentTurn;
    available.remove(index);
    this.currentTurn *= -1;
    return true;
  }

  /// Selects a spot based on the output of a Neural Network
  ///
  /// returns the index of the position selected
  int networkSelect(Network n) {
    List<double> input = [currentTurn * 1.0].followedBy(positions.map<double>((p) => p * 1.0)).toList();
    List<double> output = n.forwardPropagation(input);
    List<int> indices = [0, 1, 2, 3, 4, 5, 6, 7, 8];
    // Order the indices highest to lowest based on the output weights
    indices.sort((a, b) => output[b].compareTo(output[a]));

    // Pick the spot the network deemed optimal
    for (int i in indices) if (pickSpot(i)) return i;
    return -1;
  }

  void randomSelect({bool tryToWin = false}) {
    // List<int> indices = [0, 1, 2, 3, 4, 5, 6, 7, 8];
    if (tryToWin){
      int winningSpot = getWinningSpot();
      if (winningSpot != -1){
        if(pickSpot(winningSpot)){
          return;
        }
      }
    }
    if (available.length > 0){
      int index = r.nextInt(available.length);
      pickSpot(available[index]);
    }
  }

  void reset() {
    this.positions = [0, 0, 0, 0, 0, 0, 0, 0, 0];
    available = [0, 1, 2, 3, 4, 5, 6, 7, 8];
    this.currentTurn = 1;
    _winner = null;
  }
}
