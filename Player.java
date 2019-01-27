/**Things to consider
 * Base weighting on booleans such as:
 * prefers corners, prefers edges, prefers center
 * counters on corners, edges, center
 * 
 * Maybe make matrix the moves they make in order (if not taken)
 */
import java.util.ArrayList;
import java.util.Scanner;

public class Player implements Saveable, Loadable{
    public static Scanner scanner;
    private static int playerNum = 0;
    private double weights[][] = new double[Board.boardSize*Board.boardSize + 1][Board.boardSize*Board.boardSize]; // 10th is for empty board
    private int moves[] = new int[Board.boardSize*Board.boardSize];
    private double friendlyWeights[][] = new double[Board.boardSize*Board.boardSize+1][Board.boardSize*Board.boardSize];  // weighting for spots taken by friendly piece
    private int winCount = 0;
    private int tieCount = 0;
    private int lossCount = 0;
    private int score = 0;
    private int id;

    private boolean isUser = false;
    private boolean useWeighting = true;
    private static boolean startEmpty = true;
    private static boolean useFriendly = true;
    private static double mutationRate = 0.001;

    /**
     * Create a new player and assign its weighting matrices
     */
    public Player(){
        if (Player.scanner == null){
            Player.scanner = new Scanner(System.in);
        }
        this.id = Player.playerNum++;
        for (int i = 0; i < weights.length; i++){
            for (int j = 0; j<weights[0].length; j++){
                if (Player.startEmpty){
                    this.weights[i][j] = 0;
                    this.friendlyWeights[i][j] = 0;
                } else {
                    this.weights[i][j] = getRandomGene(); // get a random number between -1 and 1
                    this.friendlyWeights[i][j] = getRandomGene();
                }
            }
        }

        this.setupMoves();
    }

    /**
     * Create new player that may be a user
     * @param isUser - true if user
     */
    public Player(boolean isUser){
        if (Player.scanner == null){
            Player.scanner = new Scanner(System.in);
        }
        this.id = Player.playerNum++;
        this.isUser = isUser;
    }

    public Player(double weights[][], double friendlyWeights[][], int moves[]){
        if (Player.scanner == null){
            Player.scanner = new Scanner(System.in);
        }
        this.id = Player.playerNum++;
        this.weights = weights;
        this.friendlyWeights = friendlyWeights;

        this.moves = moves;
    }

    public String toString(){
        return "Player_"+id+"{ wins: " + winCount +"," + " losses:" + this.lossCount + " ties: " + this.tieCount +  "}";
    }

    private void setupMoves(){
        // set up moves 0-max
        for (int i = 0; i<moves.length; i++){
            moves[i] = i;
        }

        // switch moves around randomly
        for (int i = 0; i<moves.length; i++){
            int n = moves[i];
            int index = (int) (Math.random() * moves.length);
            moves[i] = moves[index];
            moves[index] = n;
        }
    }


    /**
     * Apply weighting to a board and pick a spot
     * @param spots - spots on the board
     * @param playerNum - this players piece number
     * @return
     */
    public int pick(int spots[], int playerNum){
        ArrayList<Integer> taken = new ArrayList<>();
        final double weighting[] = new double[Board.boardSize*Board.boardSize];
        int chosen = -1;
        
        // record all taken spots
        for (int i = 0; i<spots.length; i++){
            if (spots[i] != -1){ // if the spot is taken record it
                taken.add(i);
                weighting[i] = -1.0;
            } else {
                weighting[i] = 0.0;
            }
        }

        if (useWeighting){
            // if no spots are taken use default weighting
            if (taken.size() == 0){ // empty board
                for (int i = 0; i<spots.length; i++){
                    weighting[i] = this.weights[this.weights.length-1][i]; // last set is for empty board
                }
            }
            
            // calculate weighting for remaining spots
            taken.forEach((index)->{ // iterate through each taken index
                for (int i = 0; i<weighting.length; i++){
                    if (weighting[i] < -0.001){ // spot is taken
    
                    } else { // spot to weight is open
                        if (spots[index] == playerNum && Player.useFriendly){ // fiendly spot taken
                            weighting[i] += this.friendlyWeights[index][i];
                        } else { // non-friendly spot taken
                            weighting[i] += this.weights[index][i];
                        }
                    }
                }
            });
    
            // check to see which weighting is the highest
            for (int i = 0; i<weighting.length; i++){
                double weight = weighting[i];
                if (chosen == -1){
                    chosen = i;
                } else{
                    chosen = weight > weighting[chosen] ? i : chosen; // if the new weight is greater pick it
                }   
            }

        } else {  
            for (int i = 0; i<moves.length; i++){
                if (!taken.contains(moves[i])){
                    chosen = moves[i];
                    break;
                }
            }
            
        }

        return chosen;
    }

    /**
     * Pick a random spot on the board
     * @param spots - board to pick from
     * @return - position on board chosen
     */
    public static int pickRandom(int spots[]){
        ArrayList<Integer> avail = new ArrayList<>();

        // record all taken spots
        for (int i = 0; i<spots.length; i++){
            if (spots[i] == -1){ // if the spot is taken record it
                avail.add(i);
            } else {
            }
        }

        return avail.get((int) (Math.random() * avail.size()));
    }

    public int userPick(int spots[]){
        int chosen = -1;

        while(true){
            try{
                String nextInput = Player.scanner.next(); 
                int spot = Integer.parseInt(nextInput);
                if (spots[spot] == -1){
                    chosen = spot;
                    break;
                }
            } catch (Exception e){
                continue;
            }
        }
        // s.close();
        return chosen;
    }

    /**
     * Increase the win count
     */
    public void hasWon(){
        this.winCount++;
        this.score+=2;
    }

    public void hasLost(){
        this.lossCount++;
        this.score-=2;
    }

    public void hasTied(){
        this.tieCount++;
        this.score+=2;
    }

    public int getWins(){
        return this.winCount;
    }

    public int getLosses(){
        return this.lossCount;
    }

    public int getScore(){
        return this.score;
    }

    public void resetCounts(){
        this.winCount = 0;
        this.lossCount = 0;
        this.tieCount = 0;
        this.score = 0;
    }

    public double[][] getWeights(){
        return this.weights;
    }

    public double[][] getFriendlyWeights(){
        return this.friendlyWeights;
    }

    public double getRandomGene(){
        return 2*(0.5 - Math.random());
    }

    public Player breed(Player otherPlayer){
        Player offspring;
        double newWeights[][] = new double[this.weights.length][this.weights[0].length];
        double newFriendlyWeights[][] = new double[this.friendlyWeights.length][this.friendlyWeights[0].length];
        int newMoves[] = new int[this.moves.length];

        for (int i = 0; i<newMoves.length;i++) newMoves[i] = i;

        if (this.score < otherPlayer.getScore()){
            // double geneMixRatio = this.score/otherPlayer.getScore(); // ratio of this players wins to others
            for (int i = 0; i<this.moves.length; i++){
                if (Math.random() > 0.75){ // less likely to pick owns
                    if (indexOf(newMoves, this.moves[i]) == -1){
                        newMoves[i] = this.moves[i];
                    } else { // move already present
                        int holder = newMoves[i];
                        int ind = indexOf(newMoves, this.moves[i]);
                        newMoves[i] = newMoves[ind];
                        newMoves[ind] = holder;
                    }
                } else {
                    if (indexOf(newMoves, otherPlayer.moves[i]) == -1){
                        newMoves[i] = otherPlayer.moves[i];
                    } else { // move already present
                        int holder = newMoves[i];
                        int ind = indexOf(newMoves, otherPlayer.moves[i]);
                        newMoves[i] = newMoves[ind];
                        newMoves[ind] = holder;
                    }
                }
            }
            for (int i = 0; i<this.weights.length; i++){
                for (int j = 0; j<this.weights[0].length; j++){
                    if (Math.random() > 0.75){ // less likely to pick own
                        newWeights[i][j] = this.weights[i][j];
                        newFriendlyWeights[i][j] = this.friendlyWeights[i][j];
                    } else {
                        newWeights[i][j] = otherPlayer.getWeights()[i][j];
                        newFriendlyWeights[i][j] = otherPlayer.getFriendlyWeights()[i][j];
                    }
                    if (Math.random() < Player.mutationRate){
                        newWeights[i][j] = getRandomGene();
                    }
                    if (Math.random() < Player.mutationRate){
                        newFriendlyWeights[i][j] = getRandomGene();
                    }
                }
            }
    
            offspring = new Player(newWeights, newFriendlyWeights, newMoves);
        } else {
            // double geneMixRatio = otherPlayer.getScore()/this.score; // ratio of this players wins to others
            for (int i = 0; i<this.moves.length; i++){

                if (Math.random() < 0.75){ // more likely to pick own
                    if (indexOf(newMoves, this.moves[i]) == -1){
                        newMoves[i] = this.moves[i];
                    } else { // move already present
                        int holder = newMoves[i];
                        int ind = indexOf(newMoves, this.moves[i]);
                        newMoves[i] = newMoves[ind];
                        newMoves[ind] = holder;
                    }
                } else {
                    if (indexOf(newMoves, otherPlayer.moves[i]) == -1){
                        newMoves[i] = otherPlayer.moves[i];
                    } else { // move already present
                        int holder = newMoves[i];
                        int ind = indexOf(newMoves, otherPlayer.moves[i]);
                        newMoves[i] = newMoves[ind];
                        newMoves[ind] = holder;
                    }
                }
            }
            for (int i = 0; i<this.weights.length; i++){
                for (int j = 0; j<this.weights[0].length; j++){
                    if (Math.random() < 0.75){ // more likely to pick own
                        newWeights[i][j] = this.weights[i][j];
                        newFriendlyWeights[i][j] = this.friendlyWeights[i][j];
                    } else {
                        newWeights[i][j] = otherPlayer.getWeights()[i][j];
                        newFriendlyWeights[i][j] = otherPlayer.getFriendlyWeights()[i][j];
                    }
                    if (Math.random() < Player.mutationRate){
                        newWeights[i][j] = getRandomGene();
                    }
                    if (Math.random() < Player.mutationRate){
                        newFriendlyWeights[i][j] = getRandomGene();
                    }
                }
            }
            
            offspring = new Player(newWeights, newFriendlyWeights, newMoves);
        }
        return offspring;
    }

    /**
     * Returns if this player is a user
     * @return - true if user false if not
     */
    public boolean isUser(){
        return this.isUser;
    }

    public int indexOf(int array[], int value){
        for (int i = 0; i<array.length; i++){
            if (array[i] == value){
                return i;
            }
        }
        return -1;
    }

}