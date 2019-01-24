import java.util.ArrayList;

public class MLTicTacToe {

    public static boolean allBoardsFinished(ArrayList<Board> boards) {
        for (Board board : boards) {
            if (!board.isFinished()) {
                return false;
            }
        }
        return true;
    }

    public static void main(String args[]) {
        // ArrayList<Board> boards = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Player> winners = new ArrayList<>();
        ArrayList<Player> nextGen = new ArrayList<>();
        int generationNum = 0;
        int carryOver = 9; // number of players that carry over
        int numBoards = (int) ((carryOver - 1) * (carryOver / 2.0)); // allow for carryOver to evenly produce new gen
        int numPlayers = numBoards * 2; // double the number of boards

        // Create generation 0
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player());
        }

        for (int n__ = 0; n__ < 200; n__++){ // generation loop

            // boards = new ArrayList<>(); // clear the boards (all should be full and more will be made)
            winners = new ArrayList<>(); // clear winners (should be in players)
            nextGen = new ArrayList<>(); // clear the future generation (should be in players)

            // for (int i = 0; i < numBoards / 2; i++) {
            //     boards.add(new Board(players.get(i), players.get(players.size() - i - 1)));
            // }

            for (Player p: players){ // clear all the player wins so old ones don't inherently carry over
                p.resetWins();
            }

            // have each player play every other player
            for (int i = 0; i<numPlayers-1; i++){
                for (int j = i; j<numPlayers; j++){
                    try{
                        Board b = new Board(players.get(i), players.get(j));
                        // System.out.println("--------- NEW GAME ---------");
                        while (!b.isFinished()){
                            b.play();
                            // System.out.println(b.toString()); // print the board
                        }
                        Player winner = b.getWinner();
                        Player loser = b.getLoser();
                        if (winner != null){
                            winner.hasWon();
                        }
                        if (loser != null){
                            loser.hasLost();
                        }
                        if (loser == null && winner == null){
                            b.setTies();
                        }
                    }catch (Exception e){

                    }
                }
            }

            // int roundNum = 0;
            // // play the boards until they're all done
            // while (!MLTicTacToe.allBoardsFinished(boards)) {
            //     roundNum++;
            //     boards.forEach((board) -> {
            //         board.play();
            //     });
            // }

            // count the winners and add them to the winners array
            // int numWinners = 0;
            // for (int i = 0; i < boards.size(); i++) {
            //     Player p = boards.get(i).getWinner();
            //     if (p != null) {
            //         p.hasWon(); // increase the players win count
            //         numWinners++;
            //         if (winners.size() < carryOver) {
            //             winners.add(p);
            //         } else {
            //             // Kick out players that have fewer wins than this one # TODO
            //             for (int index = 0; index < winners.size(); index++){
            //                 if (winners.get(index).getWins() < p.getWins()){
            //                     System.out.println("Winner replaced");
            //                     winners.remove(index);
            //                     winners.add(p);
            //                     break;
            //                 }
            //             }
            //         }
            //     } else {
            //         // System.out.println("No winner");
            //     }
            // }

            for (Player p: players){
                if (winners.size() < carryOver){
                    winners.add(p);
                } else {
                    int indexToRemove = -1;
                    for (int index = 0; index < winners.size(); index++){
                        if (winners.get(index).getScore() < p.getScore() && (indexToRemove == -1 || winners.get(index).getScore() < winners.get(indexToRemove).getScore()) ){
                            indexToRemove = index;
                            // System.out.println("Winner replaced");
                            break;
                        }
                    }
                    if (indexToRemove >= 0){
                        winners.remove(indexToRemove);
                        winners.add(p);
                    }
                }
            }

            // breed the winners to form the next generation
            for (int i = 0; i < winners.size() - 1; i++) { // iterate through all
                nextGen.add(winners.get(i)); // add winner to next gen
                for (int j = i + 1; j < winners.size(); j++) { // iterate through remaining
                    nextGen.add(winners.get(i).breed(winners.get(j)));
                }
            }

            // clear the players
            players = new ArrayList<>();

            // add the next generation to the players
            for (int i = 0; i < numPlayers; i++) {
                if (i < carryOver && nextGen.size() > i) {
                    players.add(nextGen.get(i));
                } else {
                    players.add(new Player());
                }
            }

            
            System.out.println("Generation " + generationNum + " is complete");
            System.out.println("Carried Over: " + winners.size());
            System.out.println("New Players Bred: " + nextGen.size());
            // System.out.println("Games Won: " + String.valueOf(winners.size()));
            // winners.forEach((p)->{System.out.println(p.toString());});
            
            generationNum++;
        }
        // Every generation is done by here
        for (int i = 0; i<winners.size(); i++){
            winners.get(i).save("Player" + i + ".svbl");
        }
        // Player playerToSave = null;
        // for (Player p: players){
        //     if (playerToSave == null){
        //         playerToSave = p;
        //     } else if (p.getWins() > playerToSave.getWins()){
        //         playerToSave = p;
        //     }
        // }
        // playerToSave.save();

    }
}