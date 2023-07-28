package group.monkeytype;

import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Operations {
    private static final Object monitor = new Object();
    private static final ArrayList<String> records = new ArrayList<>();

    public static ArrayList<String> getRecords() {
        return records;
    }

    public static String[] getLanguages() {
        File folder = new File("dictionary");
        File[] list = folder.listFiles();
        String[] strings = new String[Objects.requireNonNull(list).length];

        for (int i = 0; i < list.length; i++) {
            strings[i] = list[i].getName();
            strings[i] = strings[i].substring(0, strings[i].lastIndexOf('.'));
        }

        return strings;
    }

    public static String readWords() throws IOException {
        StringBuilder stringB = new StringBuilder();
        Random random = new Random();
        int n = 0;
        long count = Files.lines(Paths.get("dictionary/" + Main.getLanguage() + ".txt")).count();

        while (n < 30) {
            int randomNumber = random.nextInt((int) count) + 1;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("dictionary/" + Main.getLanguage() + ".txt"), StandardCharsets.UTF_8))) {
                String line;
                int counter = 1;
                while ((line = br.readLine()) != null) {
                    if (counter == randomNumber) {
                        stringB.append(line).append(" ");
                        break;
                    }
                    counter++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            n++;
        }

        return Objects.requireNonNull(stringB).toString();
    }

    public static void createFile() {
        String name = "src/main/resources/monkeytype/results/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".txt";

        try (FileWriter bw = new FileWriter(name, true)) {
            for (int i = 0; i < Operations.getRecords().size(); i++)
                bw.write(Operations.getRecords().get(i) + "\n");
            bw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void fillTextFlow() throws FileNotFoundException {
        Main.getMainText().getChildren().clear();
        String string;
        try {
            string = Operations.readWords();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < string.length(); i++) {
            char letter = string.charAt(i);
            Text text = new Text(Character.toString(letter));
            text.setFill(Color.rgb(209, 208, 197));
            text.setFont(Font.font(30));
            Main.getMainText().getChildren().add(text);
        }
    }

    public static synchronized void timer() {
        new Thread(() -> {
            int curTime = Main.getTime();
            int i = 0;
            while (Main.getTime() > 0) {
                synchronized (monitor) {
                    while (Main.isPaused())
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Main.setTime(Main.getTime() - 1);
                int finalI = i;
                Platform.runLater(() -> {
                    Main.getTimeL().setText(Integer.toString(Main.getTime()));
                    Main.getSeries().getData().add(new XYChart.Data<>(finalI, (Operations.getRecords().size()) / (((double) Main.getGenTime() - (double) Main.getTime()) / 60)));
                });
                i++;
            }

            Main.setInStat(true);
            Main.setInTest(false);
            Main.setCurrent(0);
            Main.setTime(curTime);
            Operations.calcResult();
            Operations.createFile();
            Platform.runLater(() -> {
                try {
                    Operations.fillTextFlow();
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                Main.getChoiceTime().setDisable(false);
                Main.getChoiceLanguage().setDisable(false);
                Main.getChart().setVisible(true);
                Main.getRoot().setVisible(false);
            });
            Operations.getRecords().clear();
        }).start();
    }

    public static void pause() {
        synchronized (monitor) {
            Main.setPaused(true);
        }
    }

    public static void resume() {
        synchronized (monitor) {
            Main.setPaused(false);
            monitor.notify();
        }
    }

    public static void restart() {
        Platform.runLater(() -> {
            for (int i = 0; i < Main.getMainText().getChildren().size(); i++) {
                Text text = (Text) Main.getMainText().getChildren().get(i);
                text.setFill(Color.rgb(209, 208, 197));
            }
        });
        Main.setCurrent(0);
        Main.setTime(Main.getGenTime());
        Operations.getRecords().clear();
    }

    public static void calcResult() {
        int correct = 0, incorrect = 0, extra = 0, missed = 0;

        for (int i = 0; i < Main.getMainText().getChildren().size(); i++) {
            Text text = (Text) Main.getMainText().getChildren().get(i);
            if (text.getFill().equals(Color.rgb(55, 255, 55)))
                correct++;
            else if (text.getFill().equals(Color.rgb(255, 55, 55)))
                incorrect++;
            else if (text.getFill().equals(Color.rgb(255, 127, 80)))
                extra++;
            else if (text.getFill().equals(Color.rgb(130, 130, 130)))
                missed++;
        }

        try {
            int finalCorrect = correct, finalIncorrect = incorrect, finalExtra = extra, finalMissed = missed;
            Platform.runLater(() -> {
                Main.getAccP().setText(Integer.toString(finalCorrect * 100 / (finalCorrect + finalIncorrect + finalExtra + finalMissed)));
                Main.getWpmP().setText(Integer.toString((int) ((Operations.getRecords().size() + 1) / ((double) Main.getGenTime() / 60))));
                Main.getCharactersP().setText(finalCorrect + "/" + finalIncorrect + "/" + finalExtra + "/" + finalMissed);
                Main.getLanguageP().setText(Main.getLanguage());
                Main.getTimeP().setText(Integer.toString(Main.getGenTime()));
            });
        } catch (ArithmeticException e) {
            Main.getAccP().setText("0");
        }
    }

    public static Label labelCreate(String text, int choose) {
        Label label = new Label(text);

        if (choose == 1) {
            label.setTextFill(Color.rgb(209, 208, 197, 0.5));
            label.setFont(Font.font(20));
        } else {
            label.setTextFill(Color.rgb(226, 183, 20));
            label.setFont(Font.font(32));
        }

        return label;
    }
}