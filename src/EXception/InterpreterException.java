package EXception;

public class InterpreterException extends RuntimeException{
    public InterpreterException(String error){
        System.out.println(error);
    }
}
