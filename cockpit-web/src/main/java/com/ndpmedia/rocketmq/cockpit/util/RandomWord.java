package com.ndpmedia.rocketmq.cockpit.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RandomWord {

    private static final Map<Integer, String> DICTIONARY = new HashMap<Integer, String>(6700);

    private static Random RANDOM = new Random();

    static {
        init();
    }

    private static void init() {
        BufferedReader bufferedReader = null;
        try {
            ClassLoader classLoader = RandomWord.class.getClassLoader();
            InputStream  inputStream = classLoader.getResourceAsStream("dictionary.txt");
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            int i = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                DICTIONARY.put(++i, line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    //Ignore.
                }
            }
        }
    }


    public static String randomWord() {
        return DICTIONARY.get(RANDOM.nextInt(DICTIONARY.size()));
    }
}
