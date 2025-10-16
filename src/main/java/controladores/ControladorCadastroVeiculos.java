package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.carrosuenp.Veiculo;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class ControladorCadastroVeiculos {

    @FXML private TextField campomarca;
    @FXML private TextField campomodelo;
    @FXML private TextField campoplaca;


    @FXML
    private void salvarCadastroVeiculo() {
        try {
            String marca  = campomarca.getText().trim();
            String modelo    = campomodelo.getText().trim();
            String placa = campoplaca.getText().trim();



            if (marca.isEmpty() || modelo.isEmpty() || placa.isEmpty()) {
                alertaErro("Preencha todos os campos.");
                return;
            }

            Veiculo v = new Veiculo();
            v.setMarca(marca);
            v.setModelo(modelo);
            v.setPlaca(placa);


            CodecRegistry pojoRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            try (var client = MongoClients.create()) {
                MongoDatabase db = client.getDatabase("carrosuenp").withCodecRegistry(pojoRegistry);
                MongoCollection<Veiculo> col = db.getCollection("Veiculos", Veiculo.class);
                col.insertOne(v);
            }

            alertaInfo("Veículo salvo com sucesso!");
            campomarca.clear();
            campomodelo.clear();
            campoplaca.clear();

        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao salvar: " + e.getMessage());
        }
    }

    private void alertaInfo(String msg) {
        var a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
    private void alertaErro(String msg) {
        var a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText("Problema"); a.setContentText(msg); a.showAndWait();
    }
}
