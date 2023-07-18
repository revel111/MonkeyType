package group.monkeytype;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Operations {
    public static String[] getLanguages() {
        File folder = new File("dictionary");
        File[] list = folder.listFiles();
        String[] strings = new String[list.length];

        for (int i = 0; i < list.length; i++) {
            strings[i] = list[i].getName();
            strings[i] = strings[i].substring(0, strings[i].lastIndexOf('.'));
        }

        return strings;
    }

    public static String readWords() throws FileNotFoundException {
        StringBuilder stringBuil = new StringBuilder();
        Random random = new Random();
        int n = 0;
        while (n < 30) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary/" + Main.language + ".txt"), StandardCharsets.UTF_8))) {
                long count = Files.lines(Paths.get("dictionary/" + Main.language + ".txt")).count();
                int randomNumber = random.nextInt((int) count) + 1;
                String line;
                int counter = 1;
                while ((line = br.readLine()) != null) {
                    if (counter == randomNumber) {
                        stringBuil.append(line).append(" ");
                        break;
                    }
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            n++;
        }
        return Objects.requireNonNull(stringBuil).toString();
    }
}