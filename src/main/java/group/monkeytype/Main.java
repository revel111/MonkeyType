package group.monkeytype;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

public class Main extends Application {
    private static String language = null;
    private static int time = 0;
    private static int genTime = 0;
    private static boolean inTest = false;
    private static boolean paused = false;
    private static boolean inStat = false;
    private static int current = 0;
    private static final BorderPane root = new BorderPane();
    private static final BorderPane chart = new BorderPane();
    private static final TextFlow mainText = new TextFlow();
    private static final Label timeL = new Label(Integer.toString(time));
    private static final ChoiceBox choiceTime = new ChoiceBox(FXCollections.observableArrayList("15", "20", "45", "60", "90", "120", "300"));
    private static final ChoiceBox choiceLanguage = new ChoiceBox(FXCollections.observableArrayList(Operations.getLanguages()));
    private static final XYChart.Series series = new XYChart.Series();
    private static final Label wpmP = Operations.labelCreate("wpm%", 2);
    private static final Label accP = Operations.labelCreate("acc%", 2);
    private static final Label charactersP = Operations.labelCreate("characters%", 2);
    private static final Label languageP = Operations.labelCreate("language", 2);
    private static final Label timeP = Operations.labelCreate("time", 2);

    public static String getLanguage() {
        return language;
    }

    public static int getTime() {
        return time;
    }

    public static int getGenTime() {
        return genTime;
    }

    public static boolean isPaused() {
        return paused;
    }

    public static BorderPane getRoot() {
        return root;
    }

    public static BorderPane getChart() {
        return chart;
    }

    public static TextFlow getMainText() {
        return mainText;
    }

    public static Label getTimeL() {
        return timeL;
    }

    public static ChoiceBox getChoiceTime() {
        return choiceTime;
    }

    public static ChoiceBox getChoiceLanguage() {
        return choiceLanguage;
    }

    public static XYChart.Series getSeries() {
        return series;
    }

    public static Label getWpmP() {
        return wpmP;
    }

    public static Label getAccP() {
        return accP;
    }

    public static Label getCharactersP() {
        return charactersP;
    }

    public static Label getLanguageP() {
        return languageP;
    }

    public static Label getTimeP() {
        return timeP;
    }

    public static void setTime(int time) {
        Main.time = time;
    }

    public static void setInTest(boolean inTest) {
        Main.inTest = inTest;
    }

    public static void setPaused(boolean paused) {
        Main.paused = paused;
    }

    public static void setInStat(boolean inStat) {
        Main.inStat = inStat;
    }

    public static int getCurrent() {
        return current;
    }

    public static void setCurrent(int current) {
        Main.current = current;
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });

        choiceTime.setFocusTraversable(false);
        choiceLanguage.setFocusTraversable(false);

        final HBox boxes = new HBox();
        boxes.getChildren().addAll(choiceTime, choiceLanguage);
        boxes.setSpacing(25);
        boxes.setPadding(new Insets(15, 0, 35, 520));

        final HBox instructions = new HBox();
        ImageView footer = new ImageView(new Image(new FileInputStream("src/main/resources/monkeytype/images/footer.png")));
        instructions.getChildren().add(footer);
        instructions.setPadding(new Insets(0, 0, -100, 370));

        final VBox clock = new VBox();
        ImageView sandClock = new ImageView(new Image(new FileInputStream("src/main/resources/monkeytype/images/clock.png")));
        timeL.setTextFill(Color.rgb(209, 208, 197));
        timeL.setFont(Font.font(25));
        timeL.setPadding(new Insets(25, 0, 0, 10));
        timeL.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));
        clock.getChildren().addAll(sandClock, timeL);

        mainText.setPadding(new Insets(100, 0, 0, 50));

        choiceTime.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String timeS = (String) choiceTime.getItems().get(newValue.intValue());
            timeL.setText(timeS);
            time = Integer.parseInt(timeS);
            genTime = Integer.parseInt(timeS);
        });

        choiceLanguage.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            language = (String) choiceLanguage.getItems().get(newValue.intValue());
            try {
                Operations.fillTextFlow();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        final LineChart<Number, Number> lineChart = new LineChart<>(new NumberAxis(), new NumberAxis());
        lineChart.setTitle("Statistic");
        lineChart.getData().add(series);
        lineChart.getStyleClass().add("custom-line-chart");
        series.setName("WPM");

        final Label wpm = Operations.labelCreate("wpm", 1);
        final Label acc = Operations.labelCreate("acc", 1);
        final Label testType = Operations.labelCreate("test type:", 1);
        final Label characters = Operations.labelCreate("characters: ", 1);
        final Label timeLab = Operations.labelCreate("time:", 1);
        final VBox chartLeft = new VBox(wpm, wpmP, acc, accP, testType, timeLab, timeP, languageP);
        final HBox chartBottom = new HBox(characters, charactersP);
        chartLeft.setSpacing(10);
        chartBottom.setSpacing(10);
        chartLeft.setPadding(new Insets(200, 0, 0, 0));
        chartBottom.setPadding(new Insets(0, 0, 0, 50));

        final StackPane stackPane = new StackPane(root, chart);
        final Scene scene = new Scene(stackPane, 1200, 760);
        scene.getStylesheets().add(getClass().getResource("/monkeytype/css/chart.css").toExternalForm());

        chart.setCenter(lineChart);
        chart.setLeft(chartLeft);
        chart.setBottom(chartBottom);
        chart.setVisible(false);
        chart.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));

        root.setLeft(clock);
        root.setCenter(mainText);
        root.setBottom(instructions);
        root.setTop(boxes);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));

        stage.setScene(scene);
        stage.show();
        stage.setTitle("MonkeyType");
        stage.setResizable(true);
        stage.getIcons().add(new Image(new FileInputStream("src/main/resources/monkeytype/images/icon.png")));

//        scene.widthProperty().addListener((observable, oldValue, newValue) -> {
//            root.setPrefWidth(newValue.doubleValue());
//        });
//        scene.heightProperty().addListener((observable, oldValue, newValue) -> {
//            root.setPrefHeight(newValue.doubleValue());
//        });

        scene.setOnKeyPressed(event -> {
            if (language == null || time == -1) {
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.WARNING);
                a.setHeaderText("Choose time and language");
                a.show();
            } else {
                if ((event.getCode().isLetterKey()) && !inStat) {
                    if (!inTest) {
                        inTest = true;
                        choiceTime.setDisable(true);
                        choiceLanguage.setDisable(true);
                        Operations.timer();
                    }
                    if (paused)
                        Operations.resume();
                    Text currentText = (Text) mainText.getChildren().get(current);
                    Text previousText = null;
                    if (current != 0)
                        previousText = (Text) mainText.getChildren().get(current - 1);

                    if (currentText.getText().equals(" ") && Objects.requireNonNull(previousText).getText().equals(event.getText())) {
                        Text copy = new Text(previousText.getText());
                        copy.setFont(Font.font(30));
                        copy.setFill(Color.rgb(255, 127, 80));
                        mainText.getChildren().add(current, copy);
                        current++;
                    } else if (!currentText.getText().equals(" ")) {
                        if (currentText.getText().equals(event.getText()))
                            currentText.setFill(Color.rgb(55, 255, 55));
                        else
                            currentText.setFill(Color.rgb(255, 55, 55));
                        current++;
                    }
                } else if (event.getCode().equals(KeyCode.BACK_SPACE) && current != 0) {
                    Text previousText = (Text) mainText.getChildren().get(current - 1);
                    if (previousText.getFill().equals(Color.rgb(255, 127, 80))) {
                        mainText.getChildren().remove(current - 1);
                        current--;
                    } else if (current > 0 && !previousText.getText().equals(" ")) {
                        previousText.setFill(Color.rgb(209, 208, 197));
                        current--;
                    }
                } else if (event.getCode().equals(KeyCode.SPACE)) {
                    if (current == mainText.getChildren().size() || current == mainText.getChildren().size() - 1) {
                        try {
                            Operations.fillTextFlow();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        current = 0;
                    } else {
                        Text currentText = (Text) mainText.getChildren().get(current);
                        if (currentText.getText().equals(" ")) {
                            int counter = current - 1;
                            StringBuilder word = new StringBuilder();
                            while (true) {
                                Text previousText = (Text) mainText.getChildren().get(counter);
                                if (previousText.getText().equals(" ") || counter == 0) {
                                    word.append(" -> ").append((int) ((Operations.getRecords().size() + 1) / (((double) genTime - (double) time) / 60))).append(" wpm");
                                    Operations.getRecords().add(word.toString());
                                    break;
                                }
                                word.append(previousText.getText());
                                counter--;
                            }
                            current++;
                        } else
                            while (true) {
                                Text futureText = (Text) mainText.getChildren().get(current);
                                if (!Character.isLetter(futureText.getText().charAt(0))) {
                                    current++;
                                    break;
                                }
                                current++;
                                futureText.setFill(Color.rgb(130, 130, 130));
                            }
                    }
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                    if (inStat) {
                        root.setVisible(true);
                        chart.setVisible(false);
                        inStat = !inStat;
                    } else if (inTest)
                        time = 0;
                } else if (event.getCode().equals(KeyCode.SHIFT)) {
//                    Operations.restart();
//                    Operations.pause();
                }
            }
        });

/*        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            // Check if the pressed keys are "Enter" + "Tab"
            if (event.getCode() == KeyCode.ENTER && event.isShortcutDown()) {
                System.out.println("shehsauea");// Fire the button event
            }
        });*/
    }

    public static void main(String[] args) {
        launch();
    }
}