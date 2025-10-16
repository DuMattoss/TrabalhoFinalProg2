package controladores;

import dao.UsuarioDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.carrosuenp.Usuario;

import java.util.List;

public class ControladorGerenciarUsuarios {

    @FXML private TableView<Usuario> tabelaUsuarios;
    @FXML private TableColumn<Usuario, String> colNome;
    @FXML private TableColumn<Usuario, String> colLogin;
    @FXML private TableColumn<Usuario, String> colSenha;

    private final UsuarioDao usuarioDao = new UsuarioDao();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colSenha.setCellValueFactory(new PropertyValueFactory<>("senha"));
        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        List<Usuario> usuarios = usuarioDao.listarTodos();
        tabelaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
    }

    @FXML
    public void removerSelecionado() {
        Usuario u = tabelaUsuarios.getSelectionModel().getSelectedItem();
        if (u == null) {
            alerta("Selecione um usuário primeiro!");
            return;
        }
        usuarioDao.remover(u.getLogin());
        alerta("Usuário removido!");
        atualizarTabela();
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
