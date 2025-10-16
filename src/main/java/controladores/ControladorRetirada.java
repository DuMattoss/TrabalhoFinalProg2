package controladores;

import dao.Dao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.carrosuenp.Usuario;

import java.util.List;


public class ControladorRetirada {

    @FXML private ChoiceBox<Usuario> choiceUsuarios;
    @FXML private Button voltar;

    // ATENÇÃO: veja a nota sobre o nome da coleção mais abaixo
    private final Dao<Usuario> usuarioDao = new Dao<>(Usuario.class, "Usuarios");

    @FXML
    public void initialize() {
        try {
            System.out.println("[ControladorRetiradas] initialize() chamado");

            List<Usuario> usuarios = usuarioDao.listarTodos();
            System.out.println("[ControladorRetiradas] qtd usuarios carregados = " + usuarios.size());
            if (!usuarios.isEmpty()) {
                usuarios.stream().limit(5).forEach(u ->
                        System.out.println(" - Usuario: nome=" + safe(u.getNome()) + ", login=" + safe(u.getLogin()))
                );
            }

            // Mostra algo mesmo se nome/login vierem nulos:
            choiceUsuarios.setConverter(new StringConverter<>() {
                @Override
                public String toString(Usuario u) {
                    if (u == null) return "";
                    String nome  = safe(u.getNome());
                    String login = safe(u.getLogin());
                    if (!nome.isBlank())  return nome;
                    if (!login.isBlank()) return login;
                    // como último recurso, .toString()
                    return u.toString(); // personalize no POJO se quiser algo legível
                }
                @Override public Usuario fromString(String s) { return null; }
            });

            choiceUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            if (!choiceUsuarios.getItems().isEmpty()) {
                choiceUsuarios.getSelectionModel().selectFirst();
            } else {
                alerta("Nenhum usuário encontrado na coleção. Verifique o nome da coleção e o banco.");
            }

            // DEBUG: teste rápido de UI (descomente para verificar se o ChoiceBox aparece algo)
            // choiceUsuarios.getItems().add(new Usuario("testeLogin","Teste Nome"));
            // choiceUsuarios.getSelectionModel().selectLast();

        } catch (Exception e) {
            e.printStackTrace();
            alerta("Erro ao carregar usuários: " + e.getMessage());
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

    private String safe(String s) { return s == null ? "" : s; }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
