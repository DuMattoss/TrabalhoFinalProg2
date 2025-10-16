package controladores;

import dao.VeiculoDao;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.carrosuenp.Veiculo;

import java.util.List;

public class ControladorGerenciarVeiculos {

    @FXML private TableView<Veiculo> tabela;
    @FXML private TableColumn<Veiculo, String> colPlaca;
    @FXML private TableColumn<Veiculo, String> colModelo;
    @FXML private TableColumn<Veiculo, String> colMarca;

    @FXML private TextField tfPlaca;
    @FXML private TextField tfModelo;
    @FXML private TextField tfMarca;

    private final VeiculoDao veiculoDao = new VeiculoDao();

    @FXML
    public void initialize() {
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colModelo.setCellValueFactory(new PropertyValueFactory<>("modelo"));
        colMarca.setCellValueFactory(new PropertyValueFactory<>("marca"));

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, a, sel) -> {
            if (sel != null) {
                tfPlaca.setText(sel.getPlaca());
                tfModelo.setText(sel.getModelo());
                tfMarca.setText(sel.getMarca());
            }
        });

        atualizarTabela();
    }

    @FXML
    public void atualizarTabela() {
        List<Veiculo> lista = veiculoDao.listarTodos();
        tabela.setItems(FXCollections.observableArrayList(lista));
        tabela.getSelectionModel().clearSelection();
        tfPlaca.clear(); tfModelo.clear(); tfMarca.clear();
    }

    @FXML
    public void salvarAlteracoes() {
        Veiculo sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerta("Selecione um veículo na tabela.");
            return;
        }

        String placaAntiga = sel.getPlaca();

        sel.setPlaca(tfPlaca.getText().trim());
        sel.setModelo(tfModelo.getText().trim());
        sel.setMarca(tfMarca.getText().trim());

        if (!placaAntiga.equals(sel.getPlaca())) {
            veiculoDao.remover(placaAntiga);
            veiculoDao.salvar(sel);
        } else {
            veiculoDao.atualizar(sel);
        }

        alerta("Veículo atualizado com sucesso.");
        atualizarTabela();
    }

    @FXML
    public void removerSelecionado() {
        Veiculo sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alerta("Selecione um veículo na tabela.");
            return;
        }

        veiculoDao.remover(sel.getPlaca());
        alerta("Veículo removido com sucesso.");
        atualizarTabela();
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
