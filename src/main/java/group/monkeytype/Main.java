package group.monkeytype;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main extends Application {
    public static String var;
    public static String time;
    public static boolean isInTest = false;

    @Override
    public void start(Stage stage) throws FileNotFoundException {
        ChoiceBox choiceTime = new ChoiceBox(FXCollections.observableArrayList("15", "20", "45", "60", "90", "120", "300"));
        ChoiceBox choiceLanguage = new ChoiceBox(FXCollections.observableArrayList(Operations.getLanguages()));
        HBox boxes = new HBox();
        boxes.getChildren().addAll(choiceTime, choiceLanguage);
        boxes.setSpacing(25);
        boxes.setPadding(new Insets(15, 0, 35, 520));

        choiceTime.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> time = (String) choiceTime.getItems().get(newValue.intValue()));
        choiceLanguage.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> var = (String) choiceLanguage.getItems().get(newValue.intValue()));

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
        TextArea textGenerated = new TextArea("fuck");
//        textGenerated.setFill(Color.rgb(209, 208, 197));
        textGenerated.setFont(Font.font(20));
//        textGenerated.setOpacity(0.5);


//        Text textTyped = new TextField();
//        textTyped.setStyle("-fx-border-color: transparent; -fx-border-width: 0;");
//        textTyped.setFont(Font.font(20));

        BorderPane root = new BorderPane();
//        root.setCenter(textTyped);
        root.setCenter(textGenerated);
        root.setBottom(instructions);
        root.setTop(boxes);
        root.setBackground(new Background(new BackgroundFill(Color.rgb(32, 34, 37), new CornerRadii(0), Insets.EMPTY)));

        Scene scene = new Scene(root, 1200, 760);
        stage.setScene(scene);
        stage.show();
        stage.setTitle("MonkeyType");
        stage.getIcons().add(new Image(new FileInputStream("src/main/resources/monkeytype/icon.png")));

//        scene.widthProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
//
//            }
//        });
//        scene.heightProperty().addListener(new ChangeListener<Number>() {
//            @Override
//            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
//
//            }
//        });

        scene.setOnKeyPressed(event -> {
            if (event.getCode().isLetterKey() && !isInTest) {
                if (var == null || time == null) {
                    Alert a = new Alert(Alert.AlertType.NONE);
                    a.setAlertType(Alert.AlertType.WARNING);
                    a.setHeaderText("Choose time and language");
                    a.show();
                } else {
                    isInTest = true;
                }
//                String key = event.getText();
//                System.out.println("sheesh" + key);
            } else if (event.getCode().equals(KeyCode.ESCAPE)) {
                System.out.println("bbbbb");
            }
        });
    }

    public static void main(String[] args) throws FileNotFoundException {
        launch();

//        ArrayList<String> arrayList = Operations.readWords();
//        for (String s : arrayList) System.out.print(s + " ");
    }
}