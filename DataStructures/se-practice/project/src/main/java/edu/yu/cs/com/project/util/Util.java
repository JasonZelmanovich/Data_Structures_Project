package edu.yu.cs.com.project.util;
import java.util.Random;


public class Util {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String NUMBERS = "12134567890";
    private static final int ID_LENGTH = 8;
    public static String generateReservationId() {
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public static int generateID(){
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < ID_LENGTH; i++) {
            int index = random.nextInt(NUMBERS.length());
            char randomChar = NUMBERS.charAt(index);
            sb.append(randomChar);
        }
        int results = Integer.parseInt(sb.toString());
        return results;
    }


}
