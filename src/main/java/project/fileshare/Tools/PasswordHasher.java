package project.fileshare.Tools;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Random;

public class PasswordHasher {

    /// This method hashes the password and is used to compare what we have on file with what the user has entered.
    /// The main difference from the private hashPassword method is that this is the public wrapper that
    /// converts the hashed bytes to a hex string for storage.

    public static String HashPassword(String password, int iterations, byte[] salt) throws Exception {
        final int keyLength = 256;
        byte[] hashedPassword = hashPassword(password, salt, iterations, keyLength);
        return bytesToHex(hashedPassword);
    }

    // Randomly generates an int between 0 and 10000 to use for hashing iterations.
    // More iterations = more secure but slower hashing.

    public static int getIterations(){
        Random random = new Random();
        return random.nextInt(10000);
    }

    // Hashes a password string using PBKDF2 with HMAC SHA256.
    // This is the actual hashing algorithm that makes passwords secure.

    private static byte[] hashPassword(String password, byte[] salt, int iterations, int keyLength) throws Exception{
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, keyLength);
        SecretKeyFactory factory =  SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        return factory.generateSecret(spec).getEncoded();
    }

    // Generates a random 16-byte array to use as salt.
    // Salt ensures that identical passwords produce different hashes.

    public static byte[] generateSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    // Takes the byte array we generated and converts it into a hexadecimal string.
    // This makes the hash readable and storable as text in the database.

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