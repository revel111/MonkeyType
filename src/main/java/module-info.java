module group.monkeytype {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens group.monkeytype to javafx.fxml;
    exports group.monkeytype;
}