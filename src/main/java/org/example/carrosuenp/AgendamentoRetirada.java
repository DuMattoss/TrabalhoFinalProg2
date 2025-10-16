package org.example.carrosuenp;

/**
 * POJO para a coleção "AgendamentosRetirada".
 * Precisa do construtor vazio + getters/setters para o POJO codec do Mongo.
 */
public class AgendamentoRetirada {
    private String placa;
    private String data;
    private boolean processado;

    public AgendamentoRetirada() {}

    public AgendamentoRetirada(String placa, String data, boolean processado) {
        this.placa = placa;
        this.data = data;
        this.processado = processado;
    }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getData() { return data; }
    public void setData(String data) { this.data = data; }

    public boolean isProcessado() { return processado; }
    public void setProcessado(boolean processado) { this.processado = processado; }

    @Override
    public String toString() {
        return "AgendamentoRetirada{placa='" + placa + "', data='" + data + "', processado=" + processado + "}";
    }
}
