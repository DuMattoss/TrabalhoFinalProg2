package controladores;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ControladorTelaPrincipalOperador {

    @FXML private Label lblMenuRetirada;
    @FXML private Label lblMenuCadMotoristas;
    @FXML private Label lblMenuCadVeiculos;
    @FXML private Label lblMenuCadastroVeiculos;
    @FXML private Label lblMenuUsuarios;
    @FXML private Label lblMenuCadUsuarios;
    @FXML private Label lblMenuOperadores;
    @FXML private Label lblMenuGerenciarRetiradas;
    @FXML private AnchorPane conteudoCentro;

    @FXML
    public void initialize() {
        lblMenuRetirada.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaRetiradas.fxml"));
        lblMenuCadMotoristas.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaGerenciarMotoristas.fxml"));
        lblMenuCadVeiculos.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaGerenciarVeiculos.fxml"));
        lblMenuCadastroVeiculos.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaCadastroVeiculos.fxml"));
        lblMenuUsuarios.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaGerenciarUsuarios.fxml"));
        lblMenuOperadores.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaCadastroOperador.fxml"));
        if (lblMenuCadUsuarios != null) {
            lblMenuCadUsuarios.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaCadastroUsuario.fxml"));
        }
        if (lblMenuGerenciarRetiradas != null) {
            lblMenuGerenciarRetiradas.setOnMouseClicked(e -> abrirTela("/org/example/carrosuenp/TelaGerenciarRetiradas.fxml"));
        }
        Platform.runLater(() -> abrirTela("/org/example/carrosuenp/TelaRetiradas.fxml"));
    }

    private void abrirTela(String caminhoFXML) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(caminhoFXML));
            conteudoCentro.getChildren().setAll(root);
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            System.out.println("[Operador] Carregado: " + caminhoFXML);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERRO] Falha ao carregar " + caminhoFXML);
        }
    }
}
