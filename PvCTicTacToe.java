import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.stream.Stream;

public class PvCTicTacToe{

    public static ArrayList<File> getFileWith(String subString){
        ArrayList<File> files = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Paths.get(System.getProperty("user.dir") + "/"))) {
            paths
                .filter(Files::isRegularFile)
                .forEach((p_)->{
                    Path p = (Path) p_;
                    File f = p.toFile();
                    if (f.getName().contains(subString)){
                        files.add(f);
                    }
                });
        } catch (Exception e){

        }
        return files;
    }

    public static void main(String args[]){
        // load up a player
        Player computer;
        if (args.length > 0){
            ArrayList<File> files = PvCTicTacToe.getFileWith("Player_" + args[0]);
            if (files.size()>0){
                computer = (Player) Loadable.load(files.get(0));
            } else {
                computer = (Player) Loadable.load("Player0.svbl");
            }
        } else {
            computer = (Player) Loadable.load("Player0.svbl");
        }
        System.out.println(computer.toString());
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
