package controladores;

import dao.MotoristaDao;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.carrosuenp.Motorista;

import java.util.List;

public class ControladorGerenciarMotoristas {

    @FXML private TableView<Motorista> tabela;
    @FXML private TableColumn<Motorista, String> colNome;
    @FXML private TableColumn<Motorista, String> colCodigo;
    @FXML private TableColumn<Motorista, String> colCnh;
    @FXML private TableColumn<Motorista, String> colSetor;

    @FXML private TextField tfNome;
    @FXML private TextField tfCodigo;
    @FXML private TextField tfCnh;
    @FXML private TextField tfSetor;

    private final MotoristaDao motoristaDao = new MotoristaDao();

    @FXML
    public void initialize() {
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colCnh.setCellValueFactory(new PropertyValueFactory<>("cnh"));
        colSetor.setCellValueFactory(new PropertyValueFactory<>("setor"));

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                tfNome.setText(sel.getNome());
                tfCodigo.setText(sel.getCodigo());
                tfCnh.setText(sel.getCnh());
                tfSetor.setText(sel.getSetor());
            }
        });

        atualizarTabela();
    }


    @FXML
    private void atualizarTabela(ActionEvent e) { atualizarTabela(); }


    @FXML
    public void atualizarTabela() {
        List<Motorista> lista = motoristaDao.listarTodos();
        tabela.setItems(FXCollections.observableArrayList(lista));
        tabela.getSelectionModel().clearSelection();
        tfNome.clear(); tfCodigo.clear(); tfCnh.clear(); tfSetor.clear();
    }


    @FXML
    private void salvarAlteracoes(ActionEvent e) { salvarAlteracoes(); }

    @FXML
    public void salvarAlteracoes() {
        Motorista sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Selecione um motorista na tabela."); return; }

        String codigoAntigo = sel.getCodigo();

        sel.setNome(v(tfNome.getText()));
        sel.setCodigo(v(tfCodigo.getText()));
        sel.setCnh(v(tfCnh.getText()));
        sel.setSetor(v(tfSetor.getText()));

        if (!codigoAntigo.equals(sel.getCodigo())) {

            motoristaDao.remover(codigoAntigo);
            motoristaDao.salvar(sel);
        } else {
            motoristaDao.atualizar(sel);
        }
        alerta("Motorista atualizado.");
        atualizarTabela();
    }


    @FXML
    private void removerSelecionado(ActionEvent e) { removerSelecionado(); }

    @FXML
    public void removerSelecionado() {
        Motorista sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Selecione um motorista na tabela."); return; }
        motoristaDao.remover(sel.getCodigo());
        alerta("Motorista removido.");
        atualizarTabela();
    }


    private String v(String s) { return s == null ? "" : s.trim(); }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
