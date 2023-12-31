package group.monkeytype;

import javafx.animation.Animation;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
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

    /**
     * Function for reading all available for test language files.
     *
     * @return: String[]
     */
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

    /**
     * Function for reading random words from a chosen language files.
     * Auxiliary function for fillTextFlow().
     *
     * @return: void
     */
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

    /**
     * Function for creating file with results of a test.
     * File can be found in resources/monkeytype/results directory.
     *
     * @return: void
     */
    public static void createFile() {
        String name = "src/main/resources/monkeytype/results/" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss")) + ".txt";

        try (FileWriter bw = new FileWriter(name, true)) {
            for (String record : records) bw.write(record + "\n");
            bw.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function for filling textFlow with text to type after the end of a test.
     *
     * @return: void
     */
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

    /**
     * Function creates thread responsible for timer for main game process.
     *
     * @return: void
     */
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
                    Main.getSeries().getData().add(new XYChart.Data<>(finalI, (records.size()) / (((double) Main.getGenTime() - (double) Main.getTime()) / 60)));
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
                Main.getChartPane().setVisible(true);
                Main.getTextPane().setVisible(false);
            });

            while (Main.isInStat())
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            Platform.runLater(() -> {
                records.clear();
                Main.getSeries().getData().clear();
            });
        }).start();
    }

    /**
     * Monitor used for pausing test if ctrl+shift+p combination was pressed by user.
     *
     * @return: void
     */
    public static void pause() {
        synchronized (monitor) {
            Main.setPaused(true);
        }
    }

    /**
     * Monitor used for resuming test if it was paused by user by pressing ctrl+shift+p combination.
     *
     * @return: void
     */
    public static void resume() {
        synchronized (monitor) {
            Main.setPaused(false);
            monitor.notify();
        }
    }

    /**
     * Function used for restarting test if user pressed tab+enter combination.
     *
     * @return: void
     */
    public static void restart() {
        Platform.runLater(() -> {
            for (int i = 0; i < Main.getMainText().getChildren().size(); i++) {
                Text text = (Text) Main.getMainText().getChildren().get(i);
                text.setFill(Color.rgb(209, 208, 197));
            }
            Main.getSeries().getData().clear();
        });

        records.clear();
        Main.setCurrent(0);
        Main.setTime(Main.getGenTime());
    }

    /**
     * Function for calculating a result of a test.
     *
     * @return: void
     */
    public static void calcResult() {
        Platform.runLater(() -> {
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
            if (correct + incorrect + extra + missed == 0)
                Main.getAccP().setText("0");
            else
                Main.getAccP().setText(Integer.toString(correct * 100 / (correct + incorrect + extra + missed)));

            Main.getWpmP().setText(Integer.toString((int) (records.size() / ((double) Main.getGenTime() / 60))));
            Main.getCharactersP().setText(correct + "/" + incorrect + "/" + extra + "/" + missed);
            Main.getLanguageP().setText(Main.getLanguage());
            Main.getTimeP().setText(Integer.toString(Main.getGenTime()));
        });
    }

    /**
     * Auxiliary function used for simplification of Label creation (code cleaning).
     *
     * @param: String text, int choose
     * @return: Label
     */
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

    /**
     * Function is used for creating animation when letter was typed.
     *
     * @param: Text text
     * @return: void
     */
    public static void playWaveAnim(Text text) {
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.1), text);
        translateTransition.setCycleCount(5 * 2);
        translateTransition.setFromX(0);
        translateTransition.setToX(2);
        translateTransition.setAutoReverse(true);

        translateTransition.setOnFinished(event -> text.setTranslateX(0));

        translateTransition.play();
    }

    /**
     * Function is used for refresh button if user points on this button or vice versa.
     *
     * @param: Button button
     * @return: void
     */
    public static void playButAnim(Button button) {
        RotateTransition rotateTransition = new RotateTransition(Duration.millis(200), button);
        rotateTransition.setToAngle(0);

        button.setOnMouseEntered(event -> {
            if (rotateTransition.getStatus() == Animation.Status.RUNNING)
                rotateTransition.stop();
            rotateTransition.setToAngle(90);
            rotateTransition.play();
        });

        button.setOnMouseExited(event -> {
            if (rotateTransition.getStatus() == Animation.Status.RUNNING)
                rotateTransition.stop();
            rotateTransition.setToAngle(0);
            rotateTransition.play();
        });
    }
}