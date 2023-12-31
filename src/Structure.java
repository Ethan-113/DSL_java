import java.util.HashMap;
import java.util.Stack;

public interface Structure {
    String regular_action = "^\\s*action\\s.*";
    String regular_output ="^\\s*output\\s.*";
    String regular_user_input ="^\\s*user_input\\s.*";
    String regular_jump ="^\\s*jump:.*";
    String regular_exit ="^\\s*exit.*";
    String regular_value ="^\\s*value\\s.*";
    String regular_exp = "^\\s*###\\s.*";

    String[] regular = {regular_action, regular_output, regular_user_input, regular_jump
            , regular_exit, regular_value, regular_exp};

    //存储动作的哈希表
    HashMap<String, Action> action_table=new HashMap<>();

    //存储所有变量的哈希表
    HashMap<String, String> value_table=new HashMap<>();

    //动作栈，栈为空时程序结束
    Stack<String> action_stack=new Stack<>();

}
