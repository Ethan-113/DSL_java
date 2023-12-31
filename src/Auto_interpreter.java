import EXception.InterpreterException;

import java.util.ArrayList;

public class Auto_interpreter implements Structure{
    //当前步骤的id，即内置编号
    public static int step_id = 0;

    public static int time = 0;

    public static boolean if_timeout = false;

    //对存储好的翻译过的脚本语言进行执行
    public static void go(){
        while (action_stack.size() != 0){
            //取出下一次进行的动作
            String name = action_stack.pop();
            Action acting = action_table.get(name);
            //获取该动作的步骤表
            ArrayList<ArrayList<String>> step_table = acting.step;

            while (step_id < step_table.size()){
                boolean if_jump = false;

                //按序从步骤表中取出步骤
                ArrayList<String> step_now = step_table.get(step_id);

                //逐个比对步骤的第一个关键字，并按关键字调用
                //当前步骤为output
                if(step_now.get(0).equals("output"))
                {
                    acting.output(step_now.get(1));
                    step_id ++;
                }
                //当前步骤为user_input
                else if(step_now.get(0).equals("user_input")){
                    if_timeout = false;
                    //将秒转换为毫秒
                    time = Integer.parseInt(step_now.get(1)) * 1000;

                    String in = Auto.get_answer();
                    System.out.println(in);

                    acting.user_input(if_timeout, step_now.get(2), in);
                    Parser.clear_user_in();
                    step_id ++;
                }
                //当前步骤为jump
                else if(step_now.get(0).equals("jump")){
                    //获取新动作的名称和动作的开始步骤
                    String[] next_action = acting.jump(step_now);
                    //新动作入栈，并更新步骤的编号
                    action_stack.push(next_action[0]);
                    step_id = Integer.parseInt(next_action[1]);

                    if_jump = true;
                }
                //当前步骤为exit
                else if(step_now.get(0).equals("exit")){
                    acting.exit(action_stack);
                    step_id ++;
                }

                //进行跳转，则需要取新的动作的步骤表
                if(if_jump)
                    break;
            }

            if(step_id >= step_table.size()){
                if(action_stack.size() != 0)
                    throw new InterpreterException("脚本缺少exit，未正常退出");
                break;
            }
        }
    }

    public static void main(String[] args){
        Parser.readScript("test2.txt");
        Auto_interpreter.go();
    }
}
