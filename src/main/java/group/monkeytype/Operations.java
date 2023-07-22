package group.monkeytype;

import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.temporal.Temporal;
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
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary/" + Main.getLanguage() + ".txt"), StandardCharsets.UTF_8))) {
                long count = Files.lines(Paths.get("dictionary/" + Main.getLanguage() + ".txt")).count();
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

    public static void fillTextFlow() throws FileNotFoundException {
        Main.getTextFlow().getChildren().clear();
        String string = Operations.readWords();

        for (int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);
            Text text = new Text(Character.toString(letter));
            text.setFill(Color.rgb(209, 208, 197));
            text.setFont(Font.font(30));
            Main.getTextFlow().getChildren().add(text);
        }
    }

    public static synchronized void timer() {
        new Thread(() -> {
            int curTime = Main.getTime();
            while (Main.getTime() > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Main.setTime(Main.getTime() - 1);
                Platform.runLater(() -> Main.getLabel().setText(Integer.toString(Main.getTime())));
            }

            Main.setIsInTest(false);
            Main.setCurrent(0);
            Main.setTime(curTime);
            Platform.runLater(() -> {
                try {
                    Operations.fillTextFlow();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Main.getChoiceTime().setDisable(false);
                Main.getChoiceLanguage().setDisable(false);
            });
        }).start();
    }
}