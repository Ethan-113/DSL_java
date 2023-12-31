import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Test {
    //语法分析部分测试
    //单独测试toTranslate部分，确保语法翻译和存储正确
    public static void test_Parser(String file_path)
    {
        System.out.println("\n对Parser进行单独测试:");
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(file_path),"UTF-8"));
            String line;
            while((line=br.readLine())!=null)
            {
                //对读取的每一行进行语法分析
                Parser.toTranslate(line,br);
                Parser.line_num++;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        System.out.println("脚本读取测试成功\n");
    }

    //解释器方面测试
    public static void test_action(){
        Stub stub = new Stub();
        //对output方法进行测试
        System.out.println("\n对output方法进行单独测试:");
        stub.action_test.output("这是一则测试，不含变量");
        //stub.action_test.output("这是一则测试，含假变量:#假变量#");
        stub.action_test.output("这是一则测试，含真变量:#value1#");

        //对user_input方法进行测试
        System.out.println("\n对user_input方法进行单独测试:");
        stub.action_test.user_input(false, "", "未超时存入action的内置input未出错");
        System.out.println(stub.action_test.input);
        stub.action_test.user_input(true, "value1", "超时修改出错");
        System.out.println(Parser.value_table.get("value1"));
        stub.action_test.user_input(false, "value1", "未超时修改成功,未出错");
        System.out.println(Parser.value_table.get("value1"));

        //对jump方法进行测试，分三种情况
        System.out.println("\n对jump方法进行单独测试:");
        //生成跳转表
        ArrayList<String> conditions = new ArrayList<>(Arrays.asList("jump","timeout","0","action2","input","0","action3","else","2","action2"));

        stub.action_test.setInput("others");
        String[] next_action_1 = stub.action_test.jump(conditions);
        System.out.println("跳转测试成功:"+next_action_1[0]+'第'+next_action_1[1]+'步');

        stub.action_test.setInput("input");
        String[] next_action_2 = stub.action_test.jump(conditions);
        System.out.println("跳转测试成功:"+next_action_2[0]+'第'+next_action_2[1]+'步');

        stub.action_test.setInput("timeout");
        String[] next_action_3 = stub.action_test.jump(conditions);
        System.out.println("跳转测试成功:"+next_action_3[0]+'第'+next_action_3[1]+'步');
    }

    public static void test_interpreter(){
        System.out.println("\n对interpreter进行单独测试:");
        Stub stub = new Stub();
        stub.interpreter_test.go();
    }

    public static void main(String[] args) {
        test_Parser("test.txt");
        test_action();
        test_interpreter();
        System.out.println("\n全部测试完成，功能正常");
    }
}