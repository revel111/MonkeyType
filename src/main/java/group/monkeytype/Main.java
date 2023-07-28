package group.monkeytype;

import javafx.application.Application;
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
    private static boolean isInTest = false;
    private static boolean isPaused = false;
    private static int current = 0;
    private static final TextFlow mainText = new TextFlow();
    private static final Label label = new Label("0");
    private static final ChoiceBox choiceTime = new ChoiceBox(FXCollections.observableArrayList("15", "20", "45", "60", "90", "120", "300"));
    private static final ChoiceBox choiceLanguage = new ChoiceBox(FXCollections.observableArrayList(Operations.getLanguages()));

    public static String getLanguage() {
        return language;
    }

    public static int getTime() {
        return time;
    }

    public static int getGenTime() {
        return genTime;
    }

    public static boolean isIsPaused() {
        return isPaused;
    }

    public static TextFlow getMainText() {
        return mainText;
    }

    public static Label getLabel() {
        return label;
    }

    public static ChoiceBox getChoiceTime() {
        return choiceTime;
    }

    public static ChoiceBox getChoiceLanguage() {
        return choiceLanguage;
    }

    public static void setTime(int time) {
        Main.time = time;
    }

    public static void setIsInTest(boolean isInTest) {
        Main.isInTest = isInTest;
    }

    public static void setIsPaused(boolean isPaused) {
        Main.isPaused = isPaused;
    }

    public static void setCurrent(int current) {
        Main.current = current;
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
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
        label.setTextFill(Color.rgb(209, 208, 197));
        label.setFont(Font.font(25));
        label.setPadding(new Insets(25, 0, 0, 10));
        label.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));
        clock.getChildren().addAll(sandClock, label);

        mainText.setPadding(new Insets(100, 0, 0, 50));

        choiceTime.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String timeS = (String) choiceTime.getItems().get(newValue.intValue());
            label.setText(timeS);
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

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Statistic");
        XYChart.Series series = new XYChart.Series();
        series.setName("WPM");
        series.getData().add(new XYChart.Data<>(1, 23));
        lineChart.getData().add(series);

        final Label wpm = new Label("WPM");
        final VBox chartLeft = new VBox(wpm);
        wpm.setTextFill(Color.rgb(226, 183, 20));
        wpm.setFont(Font.font(25));

        final BorderPane root = new BorderPane();
        final BorderPane chart = new BorderPane();
        final StackPane stackPane = new StackPane(root, chart);
        final Scene scene = new Scene(stackPane, 1200, 760);

        chart.setCenter(lineChart);
        chart.setLeft(chartLeft);
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

//        new Thread(() -> {
//            while (time > 0 && !isInTest) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            Platform.runLater(() -> root.setVisible(false));
//        }).start();

        scene.setOnKeyPressed(event -> {
            if (language == null || time == -1) {
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.WARNING);
                a.setHeaderText("Choose time and language");
                a.show();
            } else {
                if ((event.getCode().isLetterKey())) {
                    if (!isInTest) {
                        isInTest = true;
                        choiceTime.setDisable(true);
                        choiceLanguage.setDisable(true);
                        Operations.timer();
                    }
                    if (isPaused)
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
                    }
                    if (!currentText.getText().equals(" ")) {
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
                    if (current == mainText.getChildren().size() - 1) {
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
                                    word.append(" -> ").append((int) ((Operations.getRecords().size() + 1) / (((double) genTime - (double) time) / 60))); //or gentime
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
                } else if (event.getCode().equals(KeyCode.ESCAPE))
                    time = 0;
                else if (event.getCode().equals(KeyCode.SHIFT)) {
//                    Operations.restart();
//                    Operations.pause();
                    root.setVisible(!root.isVisible());
                    chart.setVisible(!chart.isVisible());
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