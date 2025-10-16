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
        String codigo = operadorMode ? value(campocodigo.getText()) : "";

        if (login.isEmpty() || senha.isEmpty() || (operadorMode && codigo.isEmpty())) {
            alerta(Alert.AlertType.WARNING,
                    "Preencha todos os campos " + (operadorMode ? "(incluindo o código)" : "") + ".");
            return;
        }

        try {
            if (operadorMode) {

                var operador = serviçodeLogin.autenticarOperador(login, senha, codigo);
                if (operador == null) {
                    alerta(Alert.AlertType.ERROR, "Credenciais de operador inválidas.");
                    return;
                }

                abrirCenaNaMesmaJanela("/org/example/carrosuenp/TelaPrincipalOperador.fxml", "Operador");
            } else {

                boolean ok = serviçodeLogin.autenticarUsuario(login, senha);
                if (!ok) {
                    alerta(Alert.AlertType.ERROR, "Login ou senha incorretos!");
                    return;
                }
                abrirCenaNaMesmaJanela("/org/example/carrosuenp/TelaPrincipalUsuario.fxml", "Retiradas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            alerta(Alert.AlertType.ERROR, "Erro ao autenticar: " + e.getMessage());
        }
    }

    private void abrirCenaNaMesmaJanela(String recurso, String titulo) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(recurso));
        Parent root = loader.load();
        Stage stage = (Stage) entrar.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.setTitle(titulo);
        stage.show();
        System.out.println("[Login] Abriu: " + recurso);
    }


    private String value(String s) { return s == null ? "" : s.trim(); }

    private void alerta(Alert.AlertType tipo, String msg) {
        Alert a = new Alert(tipo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}