package EXception;

public class ParserException extends RuntimeException{
    //行号+错误信息
    public ParserException(int line, String error)
    {
        System.out.println("第"+line+"行: "+error);
    }
    //行号+变量名+错误信息
    public ParserException(int line, String name, String error)
    {
        System.out.println("第"+line+"行: "+name+","+error);
    }
    //动作名称+错误信息
    public ParserException(String action, String error)
    {
        System.out.println("action "+action+":"+error);
    }
    //动作名称+变量名+错误信息
    public ParserException(String action, String name, String error)
    {
        System.out.println("action "+action+":"+name+","+error);
    }
}
