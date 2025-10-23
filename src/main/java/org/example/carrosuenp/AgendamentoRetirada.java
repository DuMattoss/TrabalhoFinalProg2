package org.example.carrosuenp;

public class AgendamentoRetirada {

    private Veiculo veiculo;
    private Motorista motorista;

    private String dataRetirada;
    private String dataDevolucao;
    private boolean processado;

    public AgendamentoRetirada() {}

    public AgendamentoRetirada(Veiculo veiculo, Motorista motorista, String dataRetirada, String dataDevolucao, boolean processado) {
        this.veiculo = veiculo;
        this.motorista = motorista;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.processado = processado;
    }

    public Veiculo getVeiculo() { return veiculo; }
    public void setVeiculo(Veiculo veiculo) { this.veiculo = veiculo; }

    public Motorista getMotorista() { return motorista; }
    public void setMotorista(Motorista motorista) { this.motorista = motorista; }

    public String getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(String dataRetirada) { this.dataRetirada = dataRetirada; }

    public String getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(String dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    public boolean isProcessado() { return processado; }
    public void setProcessado(boolean processado) { this.processado = processado; }

    @Override
    public String toString() {
        String placa = (veiculo != null) ? veiculo.getPlaca() : "(sem veículo)";
        String nome = (motorista != null) ? motorista.getNome() : "(sem motorista)";
        return "AgendamentoRetirada{" +
                "veiculo=" + placa +
                ", motorista=" + nome +
                ", dataRetirada='" + dataRetirada + '\'' +
                ", dataDevolucao='" + dataDevolucao + '\'' +
                ", processado=" + processado +
                '}';
    }
}
