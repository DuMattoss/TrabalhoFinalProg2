package controladores;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class ControladorTelaPrincipalUsuario {

    @FXML private Label lblMenuRetirada;
    @FXML private Label lblMenuCadMotoristas;
    @FXML private Label lblMenuCadVeiculos;

    @FXML private AnchorPane conteudoCentro;

    @FXML
    public void initialize() {
        if (lblMenuRetirada != null) {
            lblMenuRetirada.setOnMouseClicked(e ->
                    abrirNaAreaCentral("/org/example/carrosuenp/TelaRetiradas.fxml"));
        }
        if (lblMenuCadMotoristas != null) {
            lblMenuCadMotoristas.setOnMouseClicked(e ->
                    abrirNaAreaCentral("/org/example/carrosuenp/TelaCadastroMotorista.fxml"));
        }
        if (lblMenuCadVeiculos != null) {
            lblMenuCadVeiculos.setOnMouseClicked(e ->
                    abrirNaAreaCentral("/org/example/carrosuenp/TelaCadastroVeiculos.fxml"));
        }


        Platform.runLater(() ->
                abrirNaAreaCentral("/org/example/carrosuenp/TelaRetiradas.fxml"));
    }

    private void abrirNaAreaCentral(String caminhoFXML) {
        try {
            Parent raiz = FXMLLoader.load(getClass().getResource(caminhoFXML));
            conteudoCentro.getChildren().setAll(raiz);
            AnchorPane.setTopAnchor(raiz, 0.0);
            AnchorPane.setRightAnchor(raiz, 0.0);
            AnchorPane.setBottomAnchor(raiz, 0.0);
            AnchorPane.setLeftAnchor(raiz, 0.0);
            System.out.println("[TelaPrincipalUsuario] Carregado: " + caminhoFXML);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("[ERRO] Falha ao carregar " + caminhoFXML);
        }
    }
}
