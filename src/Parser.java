import EXception.ParserException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser implements Structure{


    //当前是否为程序的第一个动作，即程序的入口
    public static boolean if_first = true;

    //当前正在进行翻译的动作
    public static Action now_action;

    //记录用户的输入
    public static String user_in = "";

    //当前正在翻译的行数，从1开始
    public static int line_num = 1;

    //用于其他模块对用户的输入进行修改
    public static void set_user_in(String in){
        user_in = in;
    }

    public static String get_user_in(){
        return user_in;
    }

    public static void clear_user_in(){
        user_in = "";
    }

    public static boolean is_in_clear(){
        if(user_in.equals(""))
            return true;
        return false;
    }

    //读取脚本文件，对每一行进行翻译
    //输入path为脚本文件地址
    public static void readScript(String path){
        try
        {
            BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
            String line;
            while((line=br.readLine())!=null)
            {
                //对读取的每一行进行语法分析
                toTranslate(line,br);
                line_num++;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    //toTranslate方法，每一句进行挨个正则匹配
    //输入line为当前翻译的行内容
    //br为读的脚本文件保存空间
    public static void toTranslate(String line, BufferedReader br) throws IOException{

        boolean regular_fail = true;
        //关键词逐个匹配
        for(int i = 0; i<regular.length; i++){
            //进行正则匹配
            if(Pattern.matches(regular[i], line)){
                //匹配成功
                regular_fail = false;
                //进行翻译并且进行储存
                loading(i, line, br);
                break;
            }
        }

        //如果不为空行但是没有正则匹配成功
        if(!Pattern.matches("\\s*",line) && regular_fail){
            throw new ParserException(line_num,"此句未成功匹配任何关键字");
        }
    }

    //传入pattern为正则匹配表
    //传入i为匹配到的关键字在正则匹配表中的下标
    //传入line为正在翻译的句子
    //传入br用于读取脚本文件
    private static void loading(int i, String line, BufferedReader br) throws IOException{

        //创建一个matcher，但是只匹配到关键字结束的位置
        Matcher matcher = Pattern.compile(regular[i].substring(0,regular[i].length()-2)).matcher(line);
        //只留下关键字后的名称或变量名
        String content = matcher.replaceAll("").trim();

        //通过匹配到的关键字来决定装载的格式
        switch (i){
            //匹配到的是一个新的action
            case 0:
                //新建一个动作，并且将这个动作加入动作表中
                Action action = new Action();
                action.name = content;
                now_action = action;
                action_table.put(content, action);

                //如果是第一个动作则将动作入栈
                if(if_first){
                    action_stack.add(content);
                    if_first = false;
                }
                break;


            //匹配到的是一个output
            case 1:
                if(Pattern.matches("^\".*\"$",content)){
                    content=content.substring(1, content.length()-1);
                    //将output的步骤装载进当前动作的步骤
                    ArrayList<String> step_output=new ArrayList<>(Arrays.asList("output",content));
                    now_action.step.add(step_output);
                }
                else {
                    throw new ParserException(line_num, "output后具体输入格式错误");
                }
                break;


            //匹配到的是user_input动作
            case 2:
                if(!content.equals("")){
                    if(Pattern.matches("^\\d+(\\s+.+\\s*)?",content))
                    {
                        //分隔关键字后面的内容，分别为等待时间和待传入变量，用空白字符分隔
                        String[] contents = content.split("\\s+");

                        //只有时间参数，未指定输入的传入变量
                        if(contents.length == 1){
                            ArrayList<String> step_input = new ArrayList<>(Arrays.asList("user_input", contents[0], ""));
                            now_action.step.add(step_input);
                        }
                        //有时间参数，指定输入的传入变量
                        else {
                            //判断变量是否存在，存在则加入该动作的步骤表
                            if(value_table.get(contents[1]) != null){
                                ArrayList<String> step_input = new ArrayList<>(Arrays.asList("user_input", contents[0], contents[1]));
                                now_action.step.add(step_input);
                            }else {
                                throw new ParserException(line_num, contents[1], "变量未提前声明");
                            }
                        }
                    }
                    else
                    {
                        throw new ParserException(line_num, "参数格式错误");
                    }
                }
                else
                {
                    throw new ParserException(line_num, "请输入参数");
                }
                break;


            //匹配到的是jump动作
            case 3:
                if(!Pattern.matches("^\\s*jump:\\s*$",line))
                    throw new ParserException(line_num, "jump模块应该自己占用一行");

                ArrayList<String> step_jump=new ArrayList<>(Arrays.asList("jump"));
                while((line=br.readLine()) != null){
                    line_num++;

                    //检测到结束符，结束jump
                    if(line.trim().equals("end"))
                        break;

                    //检测到if to语句
                    if(Pattern.matches("^\\s*if\\s+.+\\sto\\s+\\S+",line)){
                        //以空格为界分为四部分，第二和第四部分分别为跳转的条件和结果
                        String[] contents = line.trim().split(" ");

                        if(contents[1].equals("timeout"))
                            step_jump.add(contents[1]);
                        else if(contents[1].charAt(0) == '"')
                            step_jump.add(contents[1].substring(1,contents[1].length()-1));
                        else
                            throw new ParserException(line_num, "参数格式错误");

                        //获取转移到的动作的步骤，并存储
                        Matcher matcher_step=Pattern.compile("\\(\\d*\\)").matcher(contents[3]);
                        if(matcher_step.find()){
                            String content_step = matcher_step.group();
                            if(content_step.length() != 2)
                                step_jump.add(content_step.substring(1, content_step.length()-1));
                            else
                                step_jump.add("0");
                        }
                        else
                            throw new ParserException(line_num, "to后面的动作步骤错误");

                        //对转移到的动作进行存储
                        String contents_action=matcher_step.replaceAll("").trim();
                        step_jump.add(contents_action);
                    }
                    else if(Pattern.matches("^\\s*else\\s+\\S+",line)){
                        String[] contents = line.trim().split(" ");
                        step_jump.add(contents[0]);

                        //与检测到if to语句相同
                        Matcher matcher_step=Pattern.compile("\\(\\d*\\)").matcher(contents[1]);

                        if(matcher_step.find()){
                            String content_step = matcher_step.group();
                            if(content_step.length() != 2)
                                step_jump.add(content_step.substring(1, content_step.length()-1));
                            else
                                step_jump.add("0");
                        }
                        else
                            throw new ParserException(line_num, "to后面的动作步骤错误");

                        //对转移到的动作进行存储
                        String contents_action=matcher_step.replaceAll("").trim();
                        step_jump.add(contents_action);
                    }
                    else {
                        throw new ParserException(line_num, "jump跳转格式错误");
                    }
                }

                now_action.step.add(step_jump);
                break;


            //匹配到的动作是exit
            case 4:
                ArrayList<String> step_exit = new ArrayList<>(Arrays.asList("exit"));
                now_action.step.add(step_exit);
                break;


            //匹配到的是value申请变量
            case 5:
                if(!content.equals("")){
                    //变量名进行分隔
                    String[] contents = content.split(",");

                    for(int num = 0; num<contents.length; num++){
                        //value_table.put(contents[num], "");
                        String[] value_content = contents[num].split("=");
                        if(value_content.length == 2){
                            value_table.put(value_content[0], value_content[1]);
                        }
                        else {
                            value_table.put(value_content[0], "");
                        }
                    }
                }
                else {
                    throw new ParserException(line_num, "未给出变量名");
                }
                break;

            //发现是注释
            case 6:
                break;
        }
    }

}
