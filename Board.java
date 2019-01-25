import java.util.ArrayList;

public class Board {
    public static final int boardSize = 3; // width&height of the board
    private Player players[] = { null, null };
    private int turn;
    private int spots[] = new int[Board.boardSize*Board.boardSize]; // -1 means open 0 is p1, 1 is p2
    private int[][] winning; // winning combo for 3x3

    /**
     * Create a board with two players the starting turn is decided randomly
     * <p>
     * Connects player1 and player2 to this board
     * 
     * @param player1 - first player
     * @param player2 - second player
     */
    public Board(Player player1, Player player2) {
        for (int i = 0; i<spots.length; i++){
            spots[i] = -1;
        }
        this.calculateWinning();
        this.players[0] = player1;
        this.players[1] = player2;

        this.turn = Math.random() > 0.5 ? 1 : 0;
        if (player1.isUser() && this.turn == 0){
            System.out.println("Player 1 is x, User moves first");
        } else if (player1.isUser() && this.turn == 1){
            System.out.println("Player 1 is x, Computer moves first");
        }

        if (player2.isUser() && this.turn == 0){
            System.out.println("Player 2 is o, User moves first");
        } else if (player2.isUser() && this.turn == 1){
            System.out.println("Player 2 is o, User moves first");
        }
    }

     /**
     * Create a board with two players the starting turn is decided randomly
     * <p>
     * Connects player1 and player2 to this board
     * 
     * @param player1 - first player
     */
    public Board(Player player1) {
        for (int i = 0; i<spots.length; i++){
            spots[i] = -1;
        }
        this.calculateWinning();
        this.players[0] = player1;
        this.players[1] = null;

        this.turn = Math.random() > 0.5 ? 1 : 0;

    }

    public String toString(){
        StringBuilder bSB = new StringBuilder();
        for (int i = 0; i<this.spots.length; i++){
            int spot = this.spots[i];
            if (spot == -1){
                bSB.append("_");
            } else if (spot == 0){
                bSB.append(("x"));
            } else if (spot == 1){
                bSB.append("o");
            }
            if (i%Board.boardSize < Board.boardSize-1 || i == 0){
                bSB.append("|");
            } else {
                bSB.append("\n");
            }
        }
        return bSB.toString();
    }

    /**
     * Returns the board array
     * 
     * @return
     */
    public int[] getSpots() {
        return this.spots;
    }

    /**
     * Tell the player whose turn it is to make a move
     */
    public void play() {
        if (!this.isFinished()){
            Player p = this.players[this.turn];
            if (p!=null){
                this.spots[p.pick(this.spots, this.turn)] = this.turn;
            } else {
                this.spots[Player.pickRandom(this.spots)] = this.turn;
            }
            this.turn = (this.turn + 1) % 2;
        }
    }

    public void playUser(){
        if (!this.isFinished()){
            Player p = this.players[this.turn];
            if (p.isUser()){
                this.spots[p.userPick(this.spots)] = this.turn;
            } else {
                this.spots[p.pick(spots, this.turn)] = this.turn;
            }
            
            this.turn = (this.turn + 1) % 2;
        }
    }

    /**
     * Returns if the board is finished or not
     * @return true if this board is done
     */
    public boolean isFinished() {
        if (this.winning == null){
            this.calculateWinning();
        }

        boolean isFinished = false;
        for (int[] checks: winning){
            if (this.spots[checks[0]]!=-1){
                boolean wasAllSame = true;
                for (int i = 0; i<checks.length-1; i++){
                    if (this.spots[checks[i]] != this.spots[checks[i+1]]){
                        wasAllSame = false;
                    }
                }
                isFinished = wasAllSame;
                if (isFinished) break;
            }
            // if (this.spots[checks[0]] != -1 && this.spots[checks[0]] == this.spots[checks[1]] && this.spots[checks[1]] == this.spots[checks[2]]){
            //     isFinished = true;
            // }
        }
        
        boolean foundNegOne = false;
        for (int spot: this.spots){
            if (spot == -1){
                foundNegOne = true;
            }
        }
        if (!foundNegOne){ // board was full
            isFinished = true;
        }

        return isFinished;
    }

    /**
     * Returns the winning player or null
     * @return winning player
     */
    public Player getWinner() {
        for (int[] checks: winning){
            if (this.spots[checks[0]] != -1 && this.spots[checks[0]] == this.spots[checks[1]] && this.spots[checks[1]] == this.spots[checks[2]]){
                return this.players[this.spots[checks[1]]];
            }
        }
        return null;
    }

    public Player getLoser(){
        for (int[] checks: winning){
            if (this.spots[checks[0]] != -1 && this.spots[checks[0]] == this.spots[checks[1]] && this.spots[checks[1]] == this.spots[checks[2]]){
                if (this.spots[checks[1]] == 0){
                    return this.players[1];
                } else if (this.spots[checks[1]] == 1) {
                    return this.players[0];
                }
            }
        }
        return null;
    }

    public void setTies(){
        if (this.players[0] != null){
            this.players[0].hasTied();
        }
        if (this.players[1] != null){
            this.players[1].hasTied();
        }
    }

    /**
     * Get whose turn it is
     * @return 0 if player 1's turn 1 if player 2's turn
     */
    public int getTurn(){
        return this.turn;
    }

    public int[][] getWinning(){
        return this.winning;
    }

    private void calculateWinning(){
        // int[][] winning3x3 = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 0, 4, 8 }, { 1, 4, 7 },
        // { 2, 5, 8 }, { 2, 4, 6 } };
        // this.winning = winning3x3;

        ArrayList<ArrayList<Integer>> wins = new ArrayList<>();
        ArrayList<Integer> win = new ArrayList<>();

        // check top row win positions
        for (int i = 0; i<Board.boardSize; i++){
            // all the way down
            win = new ArrayList<>();
            for (int n = i; n<Board.boardSize*Board.boardSize; n+=Board.boardSize){
                win.add(n);
            }
            wins.add(win);
        }

        // check left column win positions
        for (int i = 0; i<Board.boardSize*Board.boardSize-Board.boardSize+1; i+=Board.boardSize){
            // all the way down
            win = new ArrayList<>();
            for (int n = i; n<i+Board.boardSize; n++){
                win.add(n);
            }
            wins.add(win);
        }

        // add diagonals
        win = new ArrayList<>();
        for (int i = 0; i<Board.boardSize*Board.boardSize; i+=(Board.boardSize+1) ){
            win.add(i);
        }
        wins.add(win);

        win = new ArrayList<>();
        for (int i = Board.boardSize-1; i<Board.boardSize*Board.boardSize-Board.boardSize+1; i+=(Board.boardSize-1) ){
            win.add(i);
        }
        wins.add(win);

        int newWinning[][] = new int[wins.size()][Board.boardSize];
        for (int i = 0; i<wins.size(); i++){
            for (int j = 0; j<wins.get(i).size(); j++){
                newWinning[i][j] = wins.get(i).get(j);
            }
        }
        this.winning = newWinning;
    }

}