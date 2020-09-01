package cn.jwjg.jwpd.Utils;

public class StringUtils {


    //判断是否有条码输入
    public static boolean checkString(String s) {
        return s.matches("^\\S*\\^\\S*\\^\\S*\\^\\S*$");
    }
}
