public class PvCTicTacToe{
    public static void main(String args[]){
        // load up a player
        Player computer;
        if (args.length > 1){
            computer = (Player) Loadable.load("Player"+args[1]+".svbl");
        } else {
            computer = (Player) Loadable.load("Player0.svbl");
        }
        Player user = new Player(true);
        Board board = new Board(user, computer);
        while(!board.isFinished()){
            board.playUser();
            System.out.println(board.toString());
            //for (int[] a : board.getWinning()){
            //    System.out.print("{");
            //    for (int i:a) System.out.print(""+i+",");
            //    System.out.println("}");
            //}
        }
        
    }
}
