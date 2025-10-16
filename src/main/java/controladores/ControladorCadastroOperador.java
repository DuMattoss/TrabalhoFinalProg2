package controladores;

import dao.OperadorDao;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.example.carrosuenp.Operador;

public class ControladorCadastroOperador {

    @FXML private TextField campoNome;
    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private TextField campoCodigo;

    private final OperadorDao operadorDao = new OperadorDao();

    @FXML
    public void salvarOperador() {
        String nome = campoNome.getText().trim();
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText().trim();
        String codigo = campoCodigo.getText().trim();

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty() || codigo.isEmpty()) {
            alerta("Preencha todos os campos!");
            return;
        }

        try {
            Operador op = new Operador(nome, senha, login, codigo);
            operadorDao.salvar(op);
            alerta("Operador cadastrado com sucesso!");
            campoNome.clear(); campoLogin.clear(); campoSenha.clear(); campoCodigo.clear();
        } catch (Exception e) {
            e.printStackTrace();
            alerta("Erro ao salvar operador: " + e.getMessage());
        }
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
