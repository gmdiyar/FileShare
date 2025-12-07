package project.fileshare.Tools;

public class Rules {
    static String disallowedChars = " !@#$%^&*()~|}{\":?><";
    static String disallowedPasswordChars = " ";

    public static boolean ensureUsername(String username){
        return !username.contains(disallowedChars);
    }
    public static boolean ensurePassword(String password){
        return !password.contains(disallowedPasswordChars);
    }

    //email one too
}
