package controladores;

import dao.AgendamentoRetiradaDao;
import dao.MotoristaDao;
import dao.VeiculoDao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.carrosuenp.AgendamentoRetirada;
import org.example.carrosuenp.Motorista;
import org.example.carrosuenp.Veiculo;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ControladorRetirada {

    @FXML private DatePicker dataRetirada;
    @FXML private DatePicker dataDevolucao;
    @FXML private ChoiceBox<Motorista> choiceUsuarios;
    @FXML private ComboBox<Veiculo> comboVeiculos;
    @FXML private Button voltar;

    private final MotoristaDao motoristaDao = new MotoristaDao();
    private final VeiculoDao veiculoDao = new VeiculoDao();
    private final AgendamentoRetiradaDao agDao = new AgendamentoRetiradaDao();

    private final ObservableList<Veiculo> todosVeiculos = FXCollections.observableArrayList();
    private Set<String> placasIndisponiveis = Set.of();

    @FXML
    public void initialize() {
        if (dataRetirada.getValue() == null) dataRetirada.setValue(LocalDate.now());
        if (dataDevolucao.getValue() == null) dataDevolucao.setValue(dataRetirada.getValue());

        dataRetirada.valueProperty().addListener((obs, o, n) -> {
            if (n != null && dataDevolucao.getValue() != null && dataDevolucao.getValue().isBefore(n)) {
                dataDevolucao.setValue(n);
            }
            refreshDisponibilidade();
        });
        dataDevolucao.valueProperty().addListener((obs, o, n) -> refreshDisponibilidade());

        carregarMotoristas();
        carregarVeiculos();

        choiceUsuarios.setConverter(new StringConverter<>() {
            @Override public String toString(Motorista m) {
                if (m == null) return "";
                String nome = nz(m.getNome());
                String cod = nz(m.getCodigo());
                if (!nome.isBlank() && !cod.isBlank()) return nome + " (" + cod + ")";
                if (!nome.isBlank()) return nome;
                if (!cod.isBlank()) return cod;
                return "(motorista)";
            }
            @Override public Motorista fromString(String s) { return null; }
        });

        comboVeiculos.setCellFactory(list -> new ListCell<>() {
            @Override protected void updateItem(Veiculo v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) {
                    setText(null);
                    setOpacity(1.0);
                } else {
                    boolean ind = placasIndisponiveis.contains(nz(v.getPlaca()));
                    setText(formatVeiculo(v, ind));
                    setOpacity(ind ? 0.5 : 1.0);
                }
            }
        });
        comboVeiculos.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Veiculo v, boolean empty) {
                super.updateItem(v, empty);
                if (empty || v == null) {
                    setText(null);
                } else {
                    boolean ind = placasIndisponiveis.contains(nz(v.getPlaca()));
                    setText(formatVeiculo(v, ind));
                }
            }
        });

        comboVeiculos.setOnAction(e -> {
            Veiculo v = comboVeiculos.getValue();
            if (v != null && placasIndisponiveis.contains(nz(v.getPlaca()))) {
                alertaAtencao("Veículo indisponível", "Este veículo já está reservado para o período selecionado.");
                comboVeiculos.getSelectionModel().clearSelection();
            }
        });

        refreshDisponibilidade();
    }

    private void carregarMotoristas() {
        try {
            List<Motorista> motoristas = motoristaDao.listarTodos();
            choiceUsuarios.setItems(FXCollections.observableArrayList(motoristas));
            if (!motoristas.isEmpty()) choiceUsuarios.getSelectionModel().selectFirst();
            else alertaInfo("Nenhum motorista encontrado.");
        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao carregar motoristas: " + e.getMessage());
        }
    }

    private void carregarVeiculos() {
        try {
            List<Veiculo> veiculos = veiculoDao.listarTodos();
            todosVeiculos.setAll(veiculos);
            comboVeiculos.setItems(todosVeiculos);
            comboVeiculos.getSelectionModel().clearSelection();
            if (veiculos.isEmpty()) alertaInfo("Nenhum veículo encontrado.");
        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao carregar veículos: " + e.getMessage());
        }
    }

    private void refreshDisponibilidade() {
        LocalDate ini = (dataRetirada.getValue() == null) ? LocalDate.now() : dataRetirada.getValue();
        LocalDate fim = (dataDevolucao.getValue() == null) ? ini : dataDevolucao.getValue();
        if (fim.isBefore(ini)) fim = ini;
        String sIni = ini.toString();
        String sFim = fim.toString();
        try {
            List<AgendamentoRetirada> conflitos = agDao.listarConflitantes(sIni, sFim);
            placasIndisponiveis = conflitos.stream()
                    .map(AgendamentoRetirada::getPlaca)
                    .map(this::nz)
                    .collect(Collectors.toSet());
            comboVeiculos.setItems(null);
            comboVeiculos.setItems(todosVeiculos);
            comboVeiculos.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao consultar disponibilidade: " + e.getMessage());
        }
    }

    private String formatVeiculo(Veiculo v, boolean indisponivel) {
        String placa = nz(v.getPlaca());
        String marca = nz(v.getMarca());
        String modelo = nz(v.getModelo());
        String base = (placa.isBlank() ? "" : placa + " - ") + (marca + " " + modelo).trim();
        return indisponivel ? base + " (indisponível)" : base;
    }

    @FXML
    private void onDataChange() {
        refreshDisponibilidade();
    }

    @FXML
    private void onConfirmarRetirada() {
        try {
            Motorista m = choiceUsuarios.getValue();
            Veiculo v = comboVeiculos.getValue();
            if (m == null) {
                alertaAtencao("Atenção", "Selecione um motorista.");
                return;
            }
            if (v == null) {
                alertaAtencao("Atenção", "Selecione um veículo disponível.");
                return;
            }
            if (dataRetirada.getValue() == null) {
                alertaAtencao("Atenção", "Escolha a data de retirada.");
                return;
            }
            if (dataDevolucao.getValue() == null) {
                alertaAtencao("Atenção", "Escolha a data de devolução.");
                return;
            }
            String ini = dataRetirada.getValue().toString();
            String fim = dataDevolucao.getValue().toString();
            if (fim.compareTo(ini) < 0) {
                alertaAtencao("Atenção", "A data de devolução não pode ser anterior à retirada.");
                return;
            }
            String placa = v.getPlaca();
            if (agDao.existeConflitoReserva(placa, ini, fim)) {
                alertaAtencao("Conflito", "Já existe reserva para este veículo que conflita com o período selecionado.");
                return;
            }
            AgendamentoRetirada novo = new AgendamentoRetirada(placa, ini, fim, false);
            agDao.inserir(novo);
            alertaInfo("Sucesso", "Reserva confirmada de " + ini + " até " + fim + " (" + formatVeiculo(v, false) + ").");
            refreshDisponibilidade();
            comboVeiculos.getSelectionModel().clearSelection();
        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao salvar agendamento: " + e.getMessage());
        }
    }

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

    private String nz(String v) { return v == null ? "" : v.trim(); }

    private void alertaInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void alertaInfo(String titulo, String mensagem) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }

    private void alertaAtencao(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setTitle(titulo);
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

    private void alertaErro(String titulo, String mensagem) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensagem);
        a.showAndWait();
    }
}
