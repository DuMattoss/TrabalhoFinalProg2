package controladores;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.example.carrosuenp.Usuario;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;



import static org.bson.codecs.configuration.CodecRegistries.*;

public class ControladorCadastroUsuario {

    @FXML
    private TextField camponome;
    @FXML
    private TextField campologin;
    @FXML
    private TextField camposenha;
    @FXML private Button voltar;


    @FXML
    private void salvarCadastroVeiculo() {
        try {
            String nome  = camponome.getText().trim();
            String login    = campologin.getText().trim();
            String senha = camposenha.getText().trim();



            if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
                alertaErro("Preencha todos os campos.");
                return;
            }

            Usuario u = new Usuario();
            u.setNome(nome);
            u.setLogin(login);
            u.setSenha(senha);


            CodecRegistry pojoRegistry = fromRegistries(
                    MongoClientSettings.getDefaultCodecRegistry(),
                    fromProviders(PojoCodecProvider.builder().automatic(true).build())
            );

            try (var client = MongoClients.create()) {
                MongoDatabase db = client.getDatabase("carrosuenp").withCodecRegistry(pojoRegistry);
                MongoCollection<Usuario> col = db.getCollection("Usuarios", Usuario.class);
                col.insertOne(u);
            }

            alertaInfo("Usuario salvo com sucesso!");
            camponome.clear();
            campologin.clear();
            camposenha.clear();

        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao salvar: " + e.getMessage());
        }
    }
    @FXML
    private void voltar() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/carrosuenp/TelaInicial.fxml"));
            Stage stage = (Stage) voltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Tela Inicial");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erro");
            alert.setHeaderText("Falha ao voltar para tela inicial");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
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
