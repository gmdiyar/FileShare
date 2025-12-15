// All buisness rules should be held in this class.
// This inlucdes dissallowed username and password characters.
// Methods to verify username and password strings don't contain
// dissalowed characters are also found here. 

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
}
