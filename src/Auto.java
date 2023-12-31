import java.util.Random;

public class Auto {
    private static int step = 0;
    private static String[] names = {"李火旺","百灵淼","岁岁","狗蛋","李秀才","李状元"};
    private static String[] ids = {"43525234523", "1234123412", "32453143214", "1423153214", "971234896"};
    private static String[] ages = {"三年级", "二年级", "一年级"};
    private static Random random = new Random();

    public static String get_answer(){
        step++;
        if(step == 1){
            int randomIndex = random.nextInt(names.length);
            return names[randomIndex];
        }
        else if(step == 2){
            int randomIndex = random.nextInt(ids.length);
            return ids[randomIndex];
        }
        else{
            int randomIndex = random.nextInt(ages.length);
            return ages[randomIndex];
        }
    }
}
