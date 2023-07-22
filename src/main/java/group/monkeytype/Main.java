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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;

public class Main extends Application {
    private static String language = null;
    private static int time = 0;
    private static boolean isInTest = false;
    private static int current = 0;
    private static final TextFlow textFlow = new TextFlow();
    private static final Label label = new Label("0");
    private static final ChoiceBox choiceTime = new ChoiceBox(FXCollections.observableArrayList("15", "20", "45", "60", "90", "120", "300"));
    private static final ChoiceBox choiceLanguage = new ChoiceBox(FXCollections.observableArrayList(Operations.getLanguages()));

    public static String getLanguage() {
        return language;
    }

    public static int getTime() {
        return time;
    }

    public static Label getLabel() {
        return label;
    }

    public static TextFlow getTextFlow() {
        return textFlow;
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

    public static void setCurrent(int current) {
        Main.current = current;
    }

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        choiceTime.setFocusTraversable(false);
        choiceLanguage.setFocusTraversable(false);
        HBox boxes = new HBox();
        boxes.getChildren().addAll(choiceTime, choiceLanguage);
        boxes.setSpacing(25);
        boxes.setPadding(new Insets(15, 0, 35, 520));

        HBox instructions = new HBox();
        ImageView footer = new ImageView(new Image(new FileInputStream("src/main/resources/monkeytype/footer.png")));
        instructions.getChildren().add(footer);
        instructions.setPadding(new Insets(0, 0, -100, 370));

        VBox clock = new VBox();
        ImageView sandClock = new ImageView(new Image(new FileInputStream("src/main/resources/monkeytype/clock.png")));
        label.setTextFill(Color.rgb(209, 208, 197));
        label.setFont(Font.font(25));
        label.setPadding(new Insets(25, 0, 0, 10));
        label.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));
        clock.getChildren().addAll(sandClock, label);

        textFlow.setPadding(new Insets(100, 0, 0, 50));

        choiceTime.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            String timeS = (String) choiceTime.getItems().get(newValue.intValue());
            label.setText(timeS);
            time = Integer.parseInt(timeS);
        });

        choiceLanguage.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            language = (String) choiceLanguage.getItems().get(newValue.intValue());
            try {
                Operations.fillTextFlow();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1200, 760);
        root.setLeft(clock);
        root.setCenter(textFlow);
        root.setBottom(instructions);
        root.setTop(boxes);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/monkeytype/background.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
        stage.setTitle("MonkeyType");
        stage.setResizable(true);
        stage.getIcons().add(new Image(new FileInputStream("src/main/resources/monkeytype/icon.png")));

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
                if ((event.getCode().isLetterKey())) {
                    if (!isInTest) {
                        isInTest = true;
                        choiceTime.setDisable(true);
                        choiceLanguage.setDisable(true);
                        Operations.timer();
                    }
                    Text currentText = (Text) textFlow.getChildren().get(current);
                    if (currentText.getText().equals(event.getText()))
                        currentText.setFill(Color.rgb(55, 255, 55));
                    else if (currentText.getText().equals(" "))
                        current--;
                    else
                        currentText.setFill(Color.rgb(255, 55, 55));
                    current++;
                } else if (event.getCode().equals(KeyCode.BACK_SPACE) && current != 0) {
                    Text previousText = (Text) textFlow.getChildren().get(current - 1);
                    if (current > 0 && !previousText.getText().equals(" ")) {
                        previousText.setFill(Color.rgb(209, 208, 197));
                        current--;
                    }
                } else if (event.getCode().equals(KeyCode.SPACE)) {
                    if (current == textFlow.getChildren().size() - 1) {
                        try {
                            Operations.fillTextFlow();
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        current = 0;
                    } else {
                        Text currentText = (Text) textFlow.getChildren().get(current);
                        if (currentText.getText().equals(" "))
                            current++;
                        else {
                            do {
                                Text futureText = (Text) textFlow.getChildren().get(current);
                                if (!Character.isLetter(futureText.getText().charAt(0))) {
                                    current++;
                                    break;
                                } else
                                    current++;
                            } while (true);
                        }
                    }
                } else if (event.getCode().equals(KeyCode.ESCAPE)) {

                }
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}