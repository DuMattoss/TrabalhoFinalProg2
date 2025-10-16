package controladores;

import dao.OperadorDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.carrosuenp.Operador;

import java.util.List;

public class ControladorGerenciarOperadores {

    @FXML private TableView<Operador> tabela;
    @FXML private TableColumn<Operador, String> colNome;
    @FXML private TableColumn<Operador, String> colLogin;
    @FXML private TableColumn<Operador, String> colSenha;
    @FXML private TableColumn<Operador, String> colCodigo;

    @FXML private TextField tfNome;
    @FXML private TextField tfLogin;
    @FXML private TextField tfSenha;
    @FXML private TextField tfCodigo;

    private final OperadorDao operadorDao = new OperadorDao();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colSenha.setCellValueFactory(new PropertyValueFactory<>("senha"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, a, sel) -> {
            if (sel != null) {
                tfNome.setText(sel.getNome());
                tfLogin.setText(sel.getLogin());
                tfSenha.setText(sel.getSenha());
                tfCodigo.setText(sel.getCodigo());
            }
        });

        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        List<Operador> lista = operadorDao.listarTodos();
        tabela.setItems(FXCollections.observableArrayList(lista));
        tabela.getSelectionModel().clearSelection();
        tfNome.clear(); tfLogin.clear(); tfSenha.clear(); tfCodigo.clear();
    }

    @FXML
    public void salvarAlteracoes() {
        Operador sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerta("Selecione um operador na tabela.");
            return;
        }

        String loginAntigo = sel.getLogin();

        sel.setNome(tfNome.getText().trim());
        sel.setLogin(tfLogin.getText().trim());
        sel.setSenha(tfSenha.getText().trim());
        sel.setCodigo(tfCodigo.getText().trim());

        if (!loginAntigo.equals(sel.getLogin())) {
            operadorDao.remover(loginAntigo);
            operadorDao.salvar(sel);
        } else {
            operadorDao.atualizar(sel);
        }

        alerta("Operador atualizado.");
        atualizarTabela();
    }

    @FXML
    public void removerSelecionado() {
        Operador sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerta("Selecione um operador na tabela.");
            return;
        }

        operadorDao.remover(sel.getLogin());
        alerta("Operador removido.");
        atualizarTabela();
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
