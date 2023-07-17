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

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        ChoiceBox choiceTime = new ChoiceBox(FXCollections.observableArrayList("15", "20", "45", "60", "90", "120", "300"));
        ChoiceBox choiceLanguage = new ChoiceBox(FXCollections.observableArrayList(Operations.getLanguages()));
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

        TextArea textGenerated = new TextArea();
        textGenerated.setStyle("-fx-text-fill: rgba(209, 208, 197, 0.7)");
//        textGenerated.setStyle("-fx-background-color: rgba(53,89,119,0.4)");
//        textGenerated.setFill(Color.rgb(209, 208, 197));
        textGenerated.setFont(Font.font(25));
        textGenerated.setEditable(false);

        TextArea textTyped = new TextArea("");
        textTyped.setFont(Font.font(25));
//        textTyped.setStyle("-fx-background-color: transparent; -fx-opacity: 0.5;");

        choiceTime.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            time = (String) choiceTime.getItems().get(newValue.intValue());
        });
        choiceLanguage.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            language = (String) choiceLanguage.getItems().get(newValue.intValue());
            try {
                textGenerated.setText(Operations.readWords());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        UnaryOperator<TextFormatter.Change> textFilter = change -> {
//            String newText = change.getControlNewText();
            if (language == null || time == null) {
                Alert a = new Alert(Alert.AlertType.NONE);
                a.setAlertType(Alert.AlertType.WARNING);
                a.setHeaderText("Choose time and language");
                a.show();
                return null;
            }
            return change;
        };

        textTyped.setTextFormatter(new TextFormatter<>(textFilter));
        StackPane text = new StackPane(textGenerated, textTyped);
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1200, 760);
        root.setRight(text);
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

//        scene.setOnKeyPressed(event -> {
//            if (event.getCode().isLetterKey() && !isInTest) {
//                if (language == null || time == null) {
//                    Alert a = new Alert(Alert.AlertType.NONE);
//                    a.setAlertType(Alert.AlertType.WARNING);
//                    a.setHeaderText("Choose time and language");
//                    a.show();
//                } else {
//                    isInTest = true;
//                }
////                String key = event.getText();
////                System.out.println("sheesh" + key);
//            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
//                System.out.println("bbbbb");
//            }
//        });
    }

    public static void main(String[] args) {
        launch();
    }
}