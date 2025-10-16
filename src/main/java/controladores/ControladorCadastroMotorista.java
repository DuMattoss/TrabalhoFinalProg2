package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import org.example.carrosuenp.Motorista;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import static org.bson.codecs.configuration.CodecRegistries.*;

public class ControladorCadastroMotorista {

    @FXML private TextField camponome;
    @FXML private TextField campocnh;
    @FXML private TextField campocodigo;  // <- STRING
    @FXML private TextField camposetor;

    @FXML
    private void salvarCadastroMotorista() {
        try {
            String nome   = camponome.getText().trim();
            String cnh    = campocnh.getText().trim();
            String codigo = campocodigo.getText().trim(); // <- STRING
            String setor  = camposetor.getText().trim();


            if (nome.isEmpty() || cnh.isEmpty() || codigo.isEmpty() || setor.isEmpty()) {
                alertaErro("Preencha todos os campos.");
                return;
            }

            Motorista m = new Motorista();
            m.setNome(nome);
            m.setCnh(cnh);
            m.setCodigo(codigo); // <- STRING
            m.setSetor(setor);

            CodecRegistry pojoRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            try (var client = MongoClients.create()) {
                MongoDatabase db = client.getDatabase("carrosuenp").withCodecRegistry(pojoRegistry);
                MongoCollection<Motorista> col = db.getCollection("Motoristas", Motorista.class);
                col.insertOne(m);
            }

            alertaInfo("Motorista salvo com sucesso!");
            camponome.clear(); campocnh.clear(); campocodigo.clear(); camposetor.clear();

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
