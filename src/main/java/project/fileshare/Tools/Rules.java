package project.fileshare.Tools;

public class Rules {
    static String disallowedChars = "!@#$%^&*()~|}{\":?><";

    public static boolean ensureUsername(String username){
        return !username.contains(disallowedChars);
    }
//    public static boolean ensureEmail(String email){
//        tbd
//    }

    //password one too
}
