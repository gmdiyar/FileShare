package project.fileshare.Tools;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

public class PasswordHasher {

    public static String HashPassword(String password, int iterations, byte[] salt) throws Exception {
        final int keyLength = 256;

        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);
        return bytesToHex(hashedPassword);
    }

    public static int getIterations(){
        Random random = new Random();
        return random.nextInt(10000);
    }

//    public static String generateHashedPassword(String password) throws Exception {
//
//        Random random = new Random();
//
//        final int keyLength = 256;
//        int iterations = random.nextInt(10000);
//        byte[] salt = generateSalt();
//        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);
//        System.out.println(bytesToHex(hashedPassword));
//        return bytesToHex(hashedPassword);
//    }

    private static byte[] hashPassword(String password, byte[] salt, int iterations, int keyLength) throws Exception{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory =  SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    public static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
    return hexString.toString();
    }
}
