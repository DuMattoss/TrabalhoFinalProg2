package controladores;

import dao.AgendamentoRetiradaDao;
import dao.VeiculoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;
import org.example.carrosuenp.AgendamentoRetirada;
import org.example.carrosuenp.Veiculo;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ControladorGerenciarRetiradas {

    @FXML private TextField filtroPlaca;
    @FXML private DatePicker filtroInicio;
    @FXML private DatePicker filtroFim;
    @FXML private TableView<AgendamentoRetirada> tabela;
    @FXML private TableColumn<AgendamentoRetirada, String> colPlaca;
    @FXML private TableColumn<AgendamentoRetirada, String> colMotorista;
    @FXML private TableColumn<AgendamentoRetirada, String> colRetirada;
    @FXML private TableColumn<AgendamentoRetirada, String> colDevolucao;
    @FXML private TableColumn<AgendamentoRetirada, Boolean> colProcessado;

    @FXML private DatePicker novaDevolucao;
    @FXML private ComboBox<Veiculo> comboNovoVeiculo;

    private final AgendamentoRetiradaDao agDao = new AgendamentoRetiradaDao();
    private final VeiculoDao veiculoDao = new VeiculoDao();
    private final ObservableList<AgendamentoRetirada> itens = FXCollections.observableArrayList();
    private final ObservableList<Veiculo> veiculos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colPlaca.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getVeiculo() != null ? nz(c.getValue().getVeiculo().getPlaca()) : ""
        ));
        colMotorista.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                c.getValue().getMotorista() != null ? nz(c.getValue().getMotorista().getNome()) : ""
        ));
        colRetirada.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                nz(c.getValue().getDataRetirada() != null ? c.getValue().getDataRetirada() : "")
        ));
        colDevolucao.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                nz(c.getValue().getDataDevolucao() != null ? c.getValue().getDataDevolucao() : "")
        ));
        colProcessado.setCellValueFactory(c -> new javafx.beans.property.SimpleBooleanProperty(c.getValue().isProcessado()));
        colProcessado.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(Boolean b, boolean empty) {
                super.updateItem(b, empty);
                setText(empty ? null : (Boolean.TRUE.equals(b) ? "Sim" : "Não"));
            }
        });

        tabela.setItems(itens);

        if (filtroInicio.getValue() == null) filtroInicio.setValue(LocalDate.now().minusDays(7));
        if (filtroFim.getValue() == null) filtroFim.setValue(LocalDate.now().plusDays(30));

        comboNovoVeiculo.setConverter(new StringConverter<>() {
            @Override public String toString(Veiculo v) {
                if (v == null) return "";
                String placa = nz(v.getPlaca());
                String marca = nz(v.getMarca());
                String modelo = nz(v.getModelo());
                return (placa.isBlank() ? "" : placa + " - ") + (marca + " " + modelo).trim();
            }
            @Override public Veiculo fromString(String s) { return null; }
        });

        carregarVeiculos();
        onRecarregar();
    }

    @FXML
    private void onRecarregar() {
        String placa = nz(filtroPlaca.getText()).toUpperCase();
        LocalDate ini = filtroInicio.getValue() == null ? LocalDate.now().minusDays(7) : filtroInicio.getValue();
        LocalDate fim = filtroFim.getValue() == null ? LocalDate.now().plusDays(30) : filtroFim.getValue();
        if (fim.isBefore(ini)) fim = ini;
        List<AgendamentoRetirada> base = agDao.listarPeriodo(ini.toString(), fim.toString());
        if (!placa.isBlank()) {
            base = base.stream()
                    .filter(a -> a.getVeiculo() != null &&
                            placa.equalsIgnoreCase(nz(a.getVeiculo().getPlaca())))
                    .collect(Collectors.toList());
        }
        itens.setAll(base);
        tabela.getSelectionModel().clearSelection();
        novaDevolucao.setValue(null);
        comboNovoVeiculo.getSelectionModel().clearSelection();
    }

    @FXML
    private void onAlterarDevolucao() {
        AgendamentoRetirada sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Selecione uma retirada.");
            return;
        }
        if (novaDevolucao.getValue() == null) {
            info("Informe a nova data de devolução.");
            return;
        }
        String retOld = nz(sel.getDataRetirada());
        String devOld = nz(sel.getDataDevolucao());
        String devNova = novaDevolucao.getValue().toString();
        if (devNova.compareTo(retOld) < 0) {
            info("A devolução não pode ser anterior à retirada.");
            return;
        }
        if (agDao.existeConflitoReserva(sel.getVeiculo().getPlaca(), retOld, devNova)) {
            info("Conflito de reserva para o período.");
            return;
        }
        long m = agDao.atualizarPeriodo(sel.getVeiculo().getPlaca(), retOld, devOld, retOld, devNova);
        if (m == 0) {
            erro("Nenhum registro alterado.");
            return;
        }
        onRecarregar();
        info("Devolução atualizada.");
    }

    @FXML
    private void onTrocarVeiculo() {
        AgendamentoRetirada sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Selecione uma retirada.");
            return;
        }
        Veiculo novo = comboNovoVeiculo.getValue();
        if (novo == null) {
            info("Selecione o novo veículo.");
            return;
        }
        String ret = nz(sel.getDataRetirada());
        String dev = nz(sel.getDataDevolucao());
        if (agDao.existeConflitoReserva(novo.getPlaca(), ret, dev)) {
            info("O veículo selecionado está indisponível para o período.");
            return;
        }
        long m = agDao.atualizarPlaca(sel.getVeiculo().getPlaca(), ret, dev, novo.getPlaca());
        if (m == 0) {
            erro("Nenhum registro alterado.");
            return;
        }
        onRecarregar();
        info("Veículo trocado.");
    }

    @FXML
    private void onCancelar() {
        AgendamentoRetirada sel = tabela.getSelectionModel().getSelectedItem();
        if (sel == null) {
            info("Selecione uma retirada.");
            return;
        }
        String ret = nz(sel.getDataRetirada());
        String dev = nz(sel.getDataDevolucao());
        long m = agDao.remover(sel.getVeiculo().getPlaca(), ret, dev);
        if (m == 0) {
            erro("Nenhum registro removido.");
            return;
        }
        onRecarregar();
        info("Retirada cancelada.");
    }

    private void carregarVeiculos() {
        try {
            veiculos.setAll(veiculoDao.listarTodos());
            comboNovoVeiculo.setItems(veiculos);
        } catch (Exception e) {
            erro("Erro ao carregar veículos.");
        }
    }

    private String nz(String v) { return v == null ? "" : v.trim(); }

    private void info(String m) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }

    private void erro(String m) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setHeaderText(null);
        a.setContentText(m);
        a.showAndWait();
    }
}
