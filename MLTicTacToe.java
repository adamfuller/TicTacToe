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

    public static boolean atLeastOneUndefeated(ArrayList<Player> players){
        for (Player p: players){
            if (p.getScore() > 0 && p.getLosses() == 0){
                System.out.println("Someone was undefeated~~~~~~~~~~~");
                return true;
            }
        }
        return false;
    }

    public static void main(String args[]) { // carryOver generations numPlayers randomOpp numRandomGames
        // ArrayList<Board> boards = new ArrayList<>();
        ArrayList<Player> players = new ArrayList<>();
        ArrayList<Player> winners = new ArrayList<>();
        ArrayList<Player> nextGen = new ArrayList<>();
        // input arguments
        int carryOver = args.length > 0 ? Integer.parseInt(args[0]) : 9; // number of players that carry over
        int maxGeneration = args.length > 1 ? Integer.parseInt(args[1]) : 200;
        // int numBoards = (int) ((carryOver - 1) * (carryOver / 2.0)); // allow for carryOver to evenly produce new gen
        int numPlayers = args.length > 2 ? Integer.parseInt(args[2]): ((carryOver - 1)*carryOver); // double the number of boards
        boolean playRandomOpponent = args.length > 3 ? Boolean.parseBoolean(args[3]): true;
        int randomGames = args.length > 4 ? Integer.parseInt(args[4]): 50;
        // compilation booleans
        boolean breedWinners = true;
        boolean goTillUndefeated = true;
        // counters
        int numWins = 0;
        int numTies = 0;
        int numLosses = 0;

        // Create generation 0
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player());
        }

        for (int currentGeneration = 0; (goTillUndefeated?true:(currentGeneration < maxGeneration)); currentGeneration++){ // generation loop
            winners = new ArrayList<>(); // clear winners (should be in players)
            nextGen = new ArrayList<>(); // clear the future generation (should be in players)

            for (Player p: players){ // clear all the player wins so old ones don't inherently carry over
                p.resetCounts();
            }

            numWins = 0;
            numTies = 0;
            numLosses = 0;
            if (!playRandomOpponent){
                // have each player play every other player
                for (int i = 0; i<numPlayers-1; i++){
                    for (int j = i; j<numPlayers; j++){
                        Board b = new Board(players.get(i), players.get(j));
                        // System.out.println("--------- NEW GAME ---------");
                        while (!b.isFinished()){
                            b.play();
                        }
                        // System.out.println(b.toString()); // print the board
                        Player winner = b.getWinner();
                        Player loser = b.getLoser();
                        if (winner != null){
                            winner.hasWon();
                            numWins++;
                        }
                        if (loser != null){
                            loser.hasLost();
                            numLosses++; // should be same as wins
                        }
                        if (loser == null && winner == null){
                            b.setTies();
                            numTies++;
                        }
                    }
                }
            } else {
                Board b;
                for (int runNum = 0; runNum < randomGames; runNum++){
                    for (int i = 0; i<numPlayers; i++){
                        b = new Board(players.get(i)); // new board with random opponent
                        while (!b.isFinished()){
                            b.play();
                        }
                        // System.out.println(b.toString()); // print the board
                        Player winner = b.getWinner();
                        Player loser = b.getLoser();
                        if (winner != null){
                            winner.hasWon();
                            numWins++;
                        }
                        if (loser != null){
                            loser.hasLost();
                            numLosses++;
                        }
                        if (loser == null && winner == null){
                            b.setTies();
                            numTies++;
                        }
                    }
                }
            }

            for (Player p: players){
                if (winners.size() < carryOver){
                    winners.add(p);
                } else {
                    int indexToRemove = -1;
                    for (int index = 0; index < winners.size(); index++){
                        // if current players score is higher than the current winner being check
                        if (winners.get(index).getScore() < p.getScore()){
                            // if first index checked or the current winner's score is less than the current winner to be replaced
                            if(indexToRemove == -1 || winners.get(index).getScore() < winners.get(indexToRemove).getScore()){
                                indexToRemove = index;
                                // System.out.println("Winner replaced");
                                // break;
                            }
                        } 
                    }
                    if (indexToRemove >= 0){
                        winners.remove(indexToRemove);
                        winners.add(p);
                    }
                }
            }

            int mostWins = 0;
            int leastLosses = Integer.MAX_VALUE;
            double highestScore = 0.0;
            String bestWLRName = "";
            // breed the winners to form the next generation
            for (int i = 0; i < winners.size(); i++) { // iterate through all
                nextGen.add(winners.get(i)); // add winner to next gen
                if (winners.get(i).getWins() > mostWins){
                    mostWins = winners.get(i).getWins();
                }
                if (winners.get(i).getLosses() < leastLosses){
                    leastLosses = winners.get(i).getLosses();
                }
                if (winners.get(i).getScore() > highestScore){
                    highestScore = winners.get(i).getScore();
                    bestWLRName = winners.get(i).toString();
                }

                // breeding section
                // nextGen.add(winners.get(i).breed(winners.get(winners.size()-i-1)));

                if (breedWinners){
                    for (int j = i + 1; j < winners.size(); j++) { // iterate through remaining
                        nextGen.add(winners.get(i).breed(winners.get(j)));
                    }
                }
            }

            if (atLeastOneUndefeated(players)){
                break;
            }


            // clear the players
            players = new ArrayList<>();

            // add the next generation to the players
            for (int i = 0; i < numPlayers; i++) {
                if (i < nextGen.size()) {
                    players.add(nextGen.get(i));
                } else {
                    players.add(new Player());
                }
            }

            
            System.out.println("Generation " + currentGeneration + " is complete");
            // System.out.println("Carried Over: " + winners.size());
            System.out.println("New Players Bred: " + (nextGen.size()-winners.size()) );
            System.out.println("Most Wins: " + mostWins);
            System.out.println("Least Losses: " + leastLosses);
            System.out.println("Best Player: " + bestWLRName);
            // System.out.println("Wins: " + numWins +" Ties: " + numTies + " Losses: " + numLosses);
            // System.gc(); // clear memory for the next round
            
        }
        // Every generation is done by here
        for (int i = 0; i<winners.size(); i++){
            int w = winners.get(i).getWins();
            int l = winners.get(i).getLosses();
            int t = winners.get(i).getTies();
            winners.get(i).save("Player_" + i + "-w"+w+"l"+l+"t"+t+ ".svbl");
        }


    }
}
