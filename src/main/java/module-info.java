module org.example.carrosuenp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.mongodb.bson;
    requires org.mongodb.driver.core;
    requires org.mongodb.driver.sync.client;

    opens org.example.carrosuenp to javafx.fxml;
    opens controladores to javafx.fxml;
    exports org.example.carrosuenp;
    exports controladores;
}
