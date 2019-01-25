import java.util.ArrayList;
import java.util.Scanner;

public class Player implements Saveable, Loadable{
    private static int playerNum = 0;
    public static Scanner scanner;
    private double weights[][] = new double[10][9]; // 10th is for empty board
    private double friendlyWeights[][] = new double[10][9];  // weighting for spots taken by friendly piece
    private int winCount = 0;
    private int score = 0;
    private int id;
    private boolean isUser = false;

    /**
     * Create a new player and assign its weighting matrices
     */
    public Player(){
        if (Player.scanner == null){
            Player.scanner = new Scanner(System.in);
        }
        this.id = Player.playerNum++;
        for (int i = 0; i < 10; i++){
            for (int j = 0; j<9; j++){
                this.weights[i][j] = 1.0 - Math.random(); // get a random number greater than 0 up to and including 1
                this.friendlyWeights[i][j] = 1.0 - Math.random();
            }
        }
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

    public Player(double weights[][], double friendlyWeights[][]){
        if (Player.scanner == null){
            Player.scanner = new Scanner(System.in);
        }
        this.id = Player.playerNum++;
        this.weights = weights;
        this.friendlyWeights = friendlyWeights;
    }

    public String toString(){
        return "Player_"+id+"{ wins: " + winCount +"," +  "}";
    }


    /**
     * Apply weighting to a board and pick a spot
     * @param spots - spots on the board
     * @param playerNum - this players piece number
     * @return
     */
    public int pick(int spots[], int playerNum){
        ArrayList<Integer> taken = new ArrayList<>();
        final double weighting[] = new double[9];
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

        // if no spots are taken use default weighting
        if (taken.size() == 0){ // empty board
            for (int i = 0; i<9; i++){
                weighting[i] = this.weights[9][i];
            }
        }

        // calculate weighting for non-taken spots
        taken.forEach((index)->{ // iterate through each taken index
            for (int i = 0; i<weighting.length; i++){
                if (weighting[i] < -0.001){ // spot is taken

                } else {
                    if (spots[index] == playerNum){ // fiendly spot taken
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
            if (chosen == -1 && weight > 0.0){
                chosen = i;
            } else if (weight > 0.0) {
                chosen = weight > weighting[chosen] ? i : chosen; // if the new weight is greater pick it
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
        this.score-=2;
    }

    public void hasTied(){
        this.score+=1;
    }

    public int getWins(){
        return this.winCount;
    }

    public int getScore(){
        return this.score;
    }

    public void resetWins(){
        this.winCount = 0;
    }

    public Player breed(Player otherPlayer){
        Player offspring;
        double newWeights[][] = new double[10][9];
        double newFriendlyWeights[][] = new double[10][9];
        
        if (this.score < otherPlayer.getScore()){
            double geneMixRatio = this.score/otherPlayer.getScore(); // ratio of this players wins to others
    
            for (int i = 0; i<this.weights.length; i++){
                for (int j = 0; j<this.weights[0].length; j++){
                    newWeights[i][j] = this.weights[i][j]*geneMixRatio + otherPlayer.weights[i][j]*(1-geneMixRatio);
                    newFriendlyWeights[i][j] = this.friendlyWeights[i][j]*geneMixRatio + otherPlayer.friendlyWeights[i][j]*(1-geneMixRatio);
                }
            }
    
            offspring = new Player(newWeights, newFriendlyWeights);
        } else {
            double geneMixRatio = otherPlayer.getScore()/this.score; // ratio of this players wins to others
    
            for (int i = 0; i<this.weights.length; i++){
                for (int j = 0; j<this.weights[0].length; j++){
                    newWeights[i][j] = this.weights[i][j]*(1-geneMixRatio) + otherPlayer.weights[i][j]*geneMixRatio;
                    newFriendlyWeights[i][j] = this.friendlyWeights[i][j]*(1-geneMixRatio) + otherPlayer.friendlyWeights[i][j]*geneMixRatio;
                }
            }
    
            offspring = new Player(newWeights, newFriendlyWeights);
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

}