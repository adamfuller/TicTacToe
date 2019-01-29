import java.util.ArrayList;

public class MLTicTacToe {


    public static void main(String args[]) { // carryOver generations numPlayers randomOpp numRandomGames goTillUndefeated
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
        boolean goTillUndefeated = args.length> 5 ? Boolean.parseBoolean(args[5]):false;

        // Create generation 0
        for (int i = 0; i < numPlayers; i++) {
            players.add(new Player());
        }

        for (int currentGeneration = 0; (goTillUndefeated?true:(currentGeneration < maxGeneration)); currentGeneration++){ // generation loop
            winners.clear(); // clear winners (should be in players)
            nextGen.clear(); // clear the future generation (should be in players)

            for (Player p: players){ // clear all the player wins so old ones don't inherently carry over
                p.resetCounts();
            }

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
                        }
                        if (loser != null){
                            loser.hasLost();
                        }
                        if (loser == null && winner == null){
                            b.setTies();
                        }
                    }
                }
            } else {
                Board b;
                for (int runNum = 0; runNum < randomGames; runNum++){
                    for (Player p : players){
                        b = new Board(p); // new board with random opponent
                        while (!b.isFinished()){
                            b.play();
                        }
                        // System.out.println(b.toString()); // print the board
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
                    }
                }
            }

            
            players.stream() // make a stream
                .sorted((p1, p2) -> p1.compareTo(p2)) // sort greatest score to least
                .forEachOrdered((p)->{ // add carryOver # of players to the winners list
                if (winners.size() < carryOver){
                    winners.add(p);
                }
            });

            // breed the winners to form the next generation
            for (int i = 0; i < winners.size(); i++) { // iterate through all
                nextGen.add(winners.get(i)); // add winner to next gen

                // breeding section
                if (breedWinners){
                    for (int j = i + 1; j < winners.size(); j++) { // iterate through remaining
                        nextGen.add(winners.get(i).breed(winners.get(j))); // add offspring to next gen
                    }
                }
            }

            // if any went undefeated stop
            if (goTillUndefeated && players.stream().anyMatch((p)->p.getLosses() == 0)){
                break;
            }


            // END OF GENERATION
            try{
                Player best = winners.stream().max((p1, p2) -> p1.compareTo(p2)).get();
                // System.out.println("Generation " + currentGeneration + " is complete");
                // System.out.println("New Players Bred: " + (nextGen.size()-winners.size()));
                // System.out.println("Highest Score: " + best.getScore());
                System.out.println("" + best.getWins() + "," + best.getLosses() + "," + best.getTies());
            } catch (Exception e){}

            
            // clear the players
            players.clear();

            // add the next generation to the players
            for (int i = 0; i < numPlayers; i++) {
                if (i < nextGen.size()) {
                    players.add(nextGen.get(i));
                } else {
                    // generate new player with more mutations
                    players.add(new Player(0.5));
                }
            }
            
        }

        // Every generation is done by here
        winners.stream().sorted((p1, p2) -> p1.compareTo(p2)).forEachOrdered((p)->{
            p.save(p.getSaveString());
        });
    }
}
