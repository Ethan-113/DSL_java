import java.util.Scanner;

public class InputThread extends Thread{

    public void run(){
        Scanner scanner = new Scanner(System.in);
        String in = scanner.nextLine();
        Parser.set_user_in(in);
    }
}
