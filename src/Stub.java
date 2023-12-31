import java.util.ArrayList;
import java.util.Arrays;

//程序的测试桩，存入既定数据以测试各模块正确性
public class Stub {
    public Action action_test;
    public Parser parser_test;
    public Interpreter interpreter_test;

    public Stub(){
        action_test = new Action();
        Action action2 = new Action();
        Action action3 = new Action();

        action_test.name = "action1";
        action2.name = "action2";
        action3.name = "action3";

        parser_test.value_table.put("value1", "114");
        parser_test.value_table.put("value2", "514");

        ArrayList<String> step1 = new ArrayList<>(Arrays.asList("output", "action2 的 step0"));
        ArrayList<String> step2 = new ArrayList<>(Arrays.asList("output", "action2 的 step1"));
        ArrayList<String> step3 = new ArrayList<>(Arrays.asList("output", "action2 的 step2"));
        ArrayList<String> step4 = new ArrayList<>(Arrays.asList("output", "action3 的 step0"));
        ArrayList<String> step5 = new ArrayList<>(Arrays.asList("exit"));

        action2.step.add(step1);
        action2.step.add(step2);
        action2.step.add(step3);
        action2.step.add(step5);
        action3.step.add(step4);

        parser_test.action_table.put("action1", action_test);
        parser_test.action_table.put("action2", action2);
        parser_test.action_table.put("action3", action3);

        interpreter_test = new Interpreter();
        interpreter_test.action_stack.push("action2");
    }
}
