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
import org.example.carrosuenp.Veiculo;

import java.util.List;

public class ControladorRetirada {

    @FXML private ChoiceBox<Usuario> choiceUsuarios;
    @FXML private ChoiceBox<Veiculo> choiceVeiculos;
    @FXML private Button voltar;

    // ⚠️ Use exatamente os nomes das coleções como estão no Compass
    private final Dao<Usuario> usuarioDao = new Dao<>(Usuario.class, "Usuarios");
    private final Dao<Veiculo> veiculoDao = new Dao<>(Veiculo.class, "Veiculos");

    @FXML
    public void initialize() {
        System.out.println("[ControladorRetiradas] initialize() chamado");

        carregarUsuarios();
        carregarVeiculos();
    }

    // ==== Carregamento de Usuários ====
    private void carregarUsuarios() {
        try {
            List<Usuario> usuarios = usuarioDao.listarTodos();
            System.out.println("[ControladorRetiradas] qtd usuarios = " + usuarios.size());
            if (!usuarios.isEmpty()) {
                usuarios.stream().limit(5).forEach(u ->
                        System.out.println(" - Usuario: nome=" + safe(u.getNome()) + ", login=" + safe(u.getLogin()))
                );
            }

            choiceUsuarios.setConverter(new StringConverter<>() {
                @Override public String toString(Usuario u) {
                    if (u == null) return "";
                    String nome  = safe(u.getNome());
                    String login = safe(u.getLogin());
                    if (!nome.isBlank())  return nome + (login.isBlank() ? "" : " (" + login + ")");
                    if (!login.isBlank()) return login;
                    return "(usuário sem dados)";
                }
                @Override public Usuario fromString(String s) { return null; }
            });

            choiceUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            if (!choiceUsuarios.getItems().isEmpty()) {
                choiceUsuarios.getSelectionModel().selectFirst();
            } else {
                alertaInfo("Nenhum usuário encontrado na coleção 'Usuarios'.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao carregar usuários: " + e.getMessage());
        }
    }

    // ==== Carregamento de Veículos ====
    private void carregarVeiculos() {
        try {
            List<Veiculo> veiculos = veiculoDao.listarTodos();
            System.out.println("[ControladorRetiradas] qtd veiculos = " + veiculos.size());
            if (!veiculos.isEmpty()) {
                veiculos.stream().limit(5).forEach(v ->
                        System.out.println(" - Veiculo: placa=" + vSafe(v.getPlaca())
                                + ", modelo=" + vSafe(v.getModelo())
                                + ", marca=" + vSafe(v.getMarca()))
                );
            }

            choiceVeiculos.setConverter(new StringConverter<>() {
                @Override public String toString(Veiculo v) {
                    if (v == null) return "";
                    String placa  = vSafe(v.getPlaca());
                    String modelo = vSafe(v.getModelo());
                    String marca  = vSafe(v.getMarca());
                    // ajuste como quiser: placa primeiro é comum
                    if (!placa.isBlank() && (!modelo.isBlank() || !marca.isBlank())) {
                        return placa + " - " + (marca.isBlank() ? "" : marca + " ")
                                + (modelo.isBlank() ? "" : modelo);
                    }
                    if (!placa.isBlank()) return placa;
                    if (!modelo.isBlank() || !marca.isBlank()) return (marca + " " + modelo).trim();
                    return "(veículo sem dados)";
                }
                @Override public Veiculo fromString(String s) { return null; }
            });

            choiceVeiculos.setItems(FXCollections.observableArrayList(veiculos));
            if (!choiceVeiculos.getItems().isEmpty()) {
                choiceVeiculos.getSelectionModel().selectFirst();
            } else {
                alertaInfo("Nenhum veículo encontrado na coleção 'Veiculos'.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao carregar veículos: " + e.getMessage());
        }
    }

    // ==== Navegação ====
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
            alertaErro("Falha ao voltar para tela inicial: " + e.getMessage());
        }
    }

    // ==== Utils ====
    private String safe(String s) { return s == null ? "" : s; }
    private String vSafe(String s) { return s == null ? "" : s.trim(); }

    private void alertaInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void alertaErro(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
