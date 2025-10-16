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
    @FXML private ChoiceBox<Motorista> choiceUsuarios;
    @FXML private ComboBox<Veiculo>  comboVeiculos;
    @FXML private Button voltar;



    private final MotoristaDao motoristaDao = new MotoristaDao();
    private final VeiculoDao veiculoDao     = new VeiculoDao();
    private final AgendamentoRetiradaDao agDao = new AgendamentoRetiradaDao();

    private final ObservableList<Veiculo> todosVeiculos = FXCollections.observableArrayList();
    private Set<String> placasIndisponiveis = Set.of();

    @FXML
    public void initialize() {
        System.out.println("[ControladorRetirada] initialize()");

        if (dataRetirada.getValue() == null) dataRetirada.setValue(LocalDate.now());

        carregarMotoristas();
        carregarVeiculos();

        choiceUsuarios.setConverter(new StringConverter<>() {
            @Override public String toString(Motorista m) {
                if (m == null) return "";
                String nome = nz(m.getNome());
                String cod  = nz(m.getCodigo());
                if (!nome.isBlank() && !cod.isBlank()) return nome + " (" + cod + ")";
                if (!nome.isBlank()) return nome;
                if (!cod.isBlank())  return cod;
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
                alertaAtencao("Veículo indisponível",
                        "Este veículo já está reservado para a data selecionada.");
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
        LocalDate d = dataRetirada.getValue() == null ? LocalDate.now() : dataRetirada.getValue();
        String dataStr = d.toString();

        try {
            List<AgendamentoRetirada> agendaDoDia = agDao.listarPorData(dataStr);
            placasIndisponiveis = agendaDoDia.stream()
                    .map(AgendamentoRetirada::getPlaca)
                    .map(this::nz)
                    .collect(Collectors.toSet());


            comboVeiculos.setItems(null);
            comboVeiculos.setItems(todosVeiculos);

        } catch (Exception e) {
            e.printStackTrace();
            alertaErro("Erro ao consultar disponibilidade: " + e.getMessage());
        }
    }

    private String formatVeiculo(Veiculo v, boolean indisponivel) {
        String placa  = nz(v.getPlaca());
        String marca  = nz(v.getMarca());
        String modelo = nz(v.getModelo());
        String base = (placa.isBlank() ? "" : placa + " - ")
                + (marca + " " + modelo).trim();
        return indisponivel ? base + " (indisponível)" : base;
    }


    @FXML
    private void onDataChange() {
        refreshDisponibilidade();
    }

    @FXML
    private void onConfirmarRetirada() {
        LocalDate data = dataRetirada.getValue();
        Motorista m = choiceUsuarios.getValue();
        Veiculo v = comboVeiculos.getValue();

        if (data == null) { alertaAtencao("Dados incompletos", "Selecione a data."); return; }
        if (m == null)    { alertaAtencao("Dados incompletos", "Selecione o motorista."); return; }
        if (v == null)    { alertaAtencao("Dados incompletos", "Selecione o veículo."); return; }

        String dataStr = data.toString();
        String placa = nz(v.getPlaca());

        if (agDao.existeReserva(placa, dataStr)) {
            alertaAtencao("Veículo indisponível", "Este veículo já está reservado em " + dataStr + ".");
            return;
        }

        try {
            AgendamentoRetirada novo = new AgendamentoRetirada(placa, dataStr, false);
            agDao.inserir(novo);

            alertaInfo("Retirada confirmada para " + dataStr + " (" + formatVeiculo(v, false) + ").");
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

    // Utils
    private String nz(String v) { return v == null ? "" : v.trim(); }

    private void alertaInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
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
}
