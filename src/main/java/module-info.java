module com.inphb.icgl.stockmanager_ci {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.poi;
    requires org.apache.poi.ooxml;
    requires mysql.connector.j;
    requires org.kordamp.ikonli.javafx;
    requires javafx.graphics;

    //requires org.apache.poi;

    opens com.inphb.icgl.stockmanager_ci to javafx.fxml;
    opens com.inphb.icgl.stockmanager_ci.model to javafx.base;
    opens com.inphb.icgl.stockmanager_ci.controller to javafx.fxml;
    exports com.inphb.icgl.stockmanager_ci;
    exports com.inphb.icgl.stockmanager_ci.controller;
}