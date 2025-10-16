package controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.carrosuenp.Usuario;
import serviços.ServiçodeLogin;

public class ControladorLogin {

    @FXML private TextField campologin;
    @FXML private PasswordField camposenha;
    @FXML private Label codigoLabel;
    @FXML private PasswordField campocodigo;
    @FXML private Button operador;
    @FXML private Button entrar;

    private boolean operadorMode = false;
    private final ServiçodeLogin serviçodeLogin = new ServiçodeLogin();

    @FXML
    private void onOperadorClick() {
        operadorMode = !operadorMode;

        codigoLabel.setVisible(operadorMode);
        codigoLabel.setManaged(operadorMode);
        campocodigo.setVisible(operadorMode);
        campocodigo.setManaged(operadorMode);

        operador.setText(operadorMode ? "Voltar" : "Operador");

        if (operadorMode) {
            campocodigo.clear();
            campocodigo.requestFocus();
        }
    }

    @FXML
    private void onEntrar() {
        String login  = value(campologin.getText());
        String senha  = value(camposenha.getText());
        String codigo = operadorMode ? value(campocodigo.getText()) : null;

        if (login.isEmpty() || senha.isEmpty() || (operadorMode && codigo.isEmpty())) {
            alerta(Alert.AlertType.WARNING, "Preencha todos os campos " + (operadorMode ? "(incluindo o código)" : "") + ".");
            return;
        }

        try {
            Usuario usuario;
            if (operadorMode) {
                // apenas verifica o código antes
                if (!codigo.equals("1234")) {
                    alerta(Alert.AlertType.ERROR, "Código de operador incorreto!");
                    return;
                }
            }

// chamada normal, com 2 parâmetros
            boolean ok = serviçodeLogin.autenticar(login, senha);

            if (ok) {
                alerta(Alert.AlertType.INFORMATION, "Login realizado com sucesso!");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/carrosuenp/TelaRetiradas.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) entrar.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Retiradas");
                stage.show();
            } else {
                alerta(Alert.AlertType.ERROR, "Login ou senha incorretos!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Erro ao autenticar: " + e.getMessage());
        }
    }

    @FXML
    private void onCadastrar() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/org/example/carrosuenp/TelaCadastroUsuario.fxml"));
            Stage stage = (Stage) campologin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Cadastro de Usuário");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Falha ao abrir tela de cadastro: " + e.getMessage());
        }
    }

    // ===== util =====
    private String value(String s) { return s == null ? "" : s.trim(); }

    private void fecharJanela() {
        Stage stage = (Stage) campologin.getScene().getWindow();
        stage.close();
    }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
