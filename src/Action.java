import EXception.ParserException;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//DSL的动作，所有运行都像自动机，每一状态都是一个动作，然后跳转至下一个动作
public class Action {
    //当前步骤动作的名称
    public String name;

    //当前动作读取到的输入进行临时存储，只有部分动作能用到，只需要存储一个步骤的临时输入
    public String input = "";

    //当前动作的所有步骤，每个动作可能分为许多步骤进行
    public ArrayList<ArrayList<String>> step = new ArrayList<>();

    public void setInput(String input){
        this.input = input;
    }

    //动作的方法，分为多种，动作中的每个步骤可以调用对应的方法

    //speak方法
    //传入为需要打印的内容
    public void output(String s) throws ParserException {
        //提取需要替换的变量
        Matcher matcher = Pattern.compile("#.+?#").matcher(s);

        //多次循环，将需要打印的内容中的引用变量替换为变量储存的值
        while (matcher.find()){
            String value_name = matcher.group().replaceAll("#", "");
            String value = Parser.value_table.get(value_name);
            if(value == null){
                throw new ParserException(this.name, value_name, "变量不存在或未声明");
            }
            else if(value.equals("")){
                throw new ParserException(this.name, value_name, "变量声明但未赋值");
            }
            else{
                //替换变量为其实际储存的值
                s = matcher.replaceFirst(value);
                matcher = Pattern.compile("#.+?#").matcher(s);
            }
        }

        System.out.println(s);
    }

    //user_input方法
    //if_timeout为是否超时，为ture则超时，否则未超时
    //value_name表示用户的输入内容保存在哪个变量中
    //in表示用户的输入
    public void user_input(boolean if_timeout, String value_name, String in) throws ParserException {
        //如果超时
        if(if_timeout){
            input="timeout";
        }
        //没有超时
        else {
            //如果不指定传入的变量，则保存在该动作的input中
            if(value_name.equals("")){
                input = in;
            }
            else {
                //判断需要传入的变量是否已经声明
                String value = Parser.value_table.get(value_name);
                if(value == null){
                    throw new ParserException(this.name, value_name, "变量不存在或未声明");
                }
                else {
                    Parser.value_table.put(value_name, in);
                }
                input = in;
            }
        }
    }

    //jump方法
    //传入为所有包含其中的if to else语句，表示验证不同的input时对应的action跳转
    //输出为一个字符串数组，长度为2，第一个是要跳转的action的name，第二个是跳转后的action的步骤step编号
    public String[] jump(ArrayList<String> condition) throws ParserException {
        String[] result = {};
        boolean if_match = false;
        for (int i = 1; i < condition.size(); i = i + 3){
            //如果跳转条件和输入相同
            if(input.contains(condition.get(i))){
                if_match = true;
                result = new String[]{condition.get(i+2),condition.get(i+1)};
            }
        }

        //else情况
        if(!if_match){
            result=new String[]{condition.get(condition.size()-1),condition.get(condition.size()-2)};
        }

        if(Parser.action_table.get(result[0]) == null){
            throw new ParserException(this.name, "jump模块中"+result[0]+"不存在");
        }

        return result;
    }

    //exit方法
    //输入为所有动作的栈，如果栈为空，程序会停止运行
    public void exit(Stack<String> action_stack){
        //所有动作出栈
        while (action_stack.size() != 0){
            action_stack.pop();
        }
    }
}
