public class PvCTicTacToe{
    public static void main(String args[]){
        // load up a player
        Player computer = (Player) Loadable.load("Player1.svbl");
        Player user = new Player(true);
        Board board = new Board(user, computer);
        while(!board.isFinished()){
            board.playUser();
            System.out.println(board.toString());
        }
        
    }
}