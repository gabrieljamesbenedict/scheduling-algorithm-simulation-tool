module org.gjbmloslos.schedulingalgo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens org.gjbmloslos.schedulingalgo to javafx.fxml;
    exports org.gjbmloslos.schedulingalgo;
}