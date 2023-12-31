public class Main {
    public static void main(String[] args){
        Parser.readScript("test2.txt");
        Interpreter.go();
        //Auto_interpreter.go();
    }
}
