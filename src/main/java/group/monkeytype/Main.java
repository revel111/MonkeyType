package group.monkeytype;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class Main extends Application {
    public static String language = null;
    public static String time = null;
    public static boolean isInTest = false;
    public static int current = 0;
    TextFlow textFlow = new TextFlow();

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        ChoiceBox choiceTime = new ChoiceBox(FXCollections.observableArrayList("15", "20", "45", "60", "90", "120", "300"));
        ChoiceBox choiceLanguage = new ChoiceBox(FXCollections.observableArrayList(Operations.getLanguages()));
        choiceTime.setFocusTraversable(false);
        choiceLanguage.setFocusTraversable(false);
        HBox boxes = new HBox();
        boxes.getChildren().addAll(choiceTime, choiceLanguage);
        boxes.setSpacing(25);
        boxes.setPadding(new Insets(15, 0, 35, 520));

        HBox instructions = new HBox();
        ImageView imageView = new ImageView(new Image(new FileInputStream("src/main/resources/monkeytype/footer.png")));
        instructions.getChildren().add(imageView);
        instructions.setPadding(new Insets(0, 0, -100, 370));
//        ExecutorService executor = Executors.newFixedThreadPool(1);
//        executor.execute(new Thread(() -> {
//
//        }));
//
//        executor.execute(new Thread(() -> {
//
//        }));
//        TextArea textGenerated = new TextArea();
//        textGenerated.setStyle("-fx-text-fill: rgba(209, 208, 197, 0.7)");
//        textGenerated.setFont(Font.font(30));
//        textGenerated.setEditable(false);
//        textGenerated.setWrapText(true);
//        TextFlow textFlow = new TextFlow();
//        textFlow.getChildren().add(textGenerated);


//        TextArea textTyped = new TextArea();
//        textTyped.setFont(Font.font(25));
//        textTyped.setStyle("-fx-background-color: transparent; -fx-opacity: 0.5;");

        choiceTime.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            time = (String) choiceTime.getItems().get(newValue.intValue());
        });

        choiceLanguage.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            language = (String) choiceLanguage.getItems().get(newValue.intValue());
            try {
                textFlow.getChildren().clear();
                String string = Operations.readWords();

                for (int i = 0; i < string.length(); i++) {
                    char letter = string.charAt(i);
                    Text text = new Text(Character.toString(letter));
                    text.setFill(Color.rgb(209, 208, 197));
                    text.setFont(Font.font(30));
                    textFlow.getChildren().add(text);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

//        UnaryOperator<TextFormatter.Change> textFilter = change -> {
////            String newText = change.getControlNewText();
//            if (language == null || time == null) {
//                Alert a = new Alert(Alert.AlertType.NONE);
//                a.setAlertType(Alert.AlertType.WARNING);
//                a.setHeaderText("Choose time and language");
//                a.show();
//                return null;
//            }
//            return change;
//        };
//
//        textTyped.setTextFormatter(new TextFormatter<>(textFilter));
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1200, 760);
//        root.setRight(text);
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
            if (language == null || time == null) {
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.WARNING);
                a.setHeaderText("Choose time and language");
                a.show();
            } else {
                if ((event.getCode().isLetterKey() /*&& !isInTest*/)) {
                    /*if (!isInTest)
                        isInTest = true;*/
                    Text currentText = (Text) textFlow.getChildren().get(current);
                    if (currentText.getText().equals(event.getText()))
                        currentText.setFill(Color.rgb(55, 255, 55));
                    else if (currentText.getText().equals(" "))
                        current--;
                    else
                        currentText.setFill(Color.rgb(255, 55, 55));
                    current++;
                } else if (event.getCode().equals(KeyCode.BACK_SPACE)) {
                    Text previousText = (Text) textFlow.getChildren().get(current - 1);
                    if (current > 0 && !previousText.getText().equals(" ")) {
                        previousText.setFill(Color.rgb(209, 208, 197));
                        current--;
                    }
                } else if (event.getCode().equals(KeyCode.SPACE)) {
                    Text currentText = (Text) textFlow.getChildren().get(current);
                    if (currentText.getText().equals(" ")) {
                        current++;
                    } else {
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
            }

        });
    }

    public static void main(String[] args) {
        launch();
    }
}