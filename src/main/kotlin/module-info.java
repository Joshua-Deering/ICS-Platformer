module josh.icsplatformer {
    requires javafx.controls;
    requires javafx.fxml;
    requires kotlin.stdlib;

    requires org.controlsfx.controls;
    requires com.almasb.fxgl.all;
    requires java.desktop;

    opens josh.icsplatformer to javafx.fxml;
    exports josh.icsplatformer;
}