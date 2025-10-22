package org.example.carrosuenp;

/**
 * POJO para a coleção "AgendamentosRetirada".
 * Compatível com registros antigos (campo "data") e novos (dataRetirada/dataDevolucao).
 */
public class AgendamentoRetirada {
    private String placa;

    // NOVOS CAMPOS
    private String dataRetirada;   // "YYYY-MM-DD"
    private String dataDevolucao;  // "YYYY-MM-DD"

    // LEGADO: ainda existe em docs antigos; manter getter/setter para o codec
    private String data;

    private boolean processado;

    public AgendamentoRetirada() {}

    /** Construtor novo (recomendado) */
    public AgendamentoRetirada(String placa, String dataRetirada, String dataDevolucao, boolean processado) {
        this.placa = placa;
        this.dataRetirada = dataRetirada;
        this.dataDevolucao = dataDevolucao;
        this.processado = processado;

        // Para compatibilidade eventual, podemos setar "data" igual à dataRetirada
        this.data = dataRetirada;
    }

    /** Construtor antigo (compatibilidade) -> devolução = retirada */
    public AgendamentoRetirada(String placa, String data, boolean processado) {
        this.placa = placa;
        this.data = data;
        this.dataRetirada = data;
        this.dataDevolucao = data;
        this.processado = processado;
    }

    // Getters/Setters
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getDataRetirada() { return dataRetirada; }
    public void setDataRetirada(String dataRetirada) { this.dataRetirada = dataRetirada; }

    public String getDataDevolucao() { return dataDevolucao; }
    public void setDataDevolucao(String dataDevolucao) { this.dataDevolucao = dataDevolucao; }

    // LEGADO: manter para docs antigos
    public String getData() { return data; }
    public void setData(String data) {
        this.data = data;
        // se preencher o legado, mantenha os novos coerentes
        if (this.dataRetirada == null) this.dataRetirada = data;
        if (this.dataDevolucao == null) this.dataDevolucao = data;
    }

    public boolean isProcessado() { return processado; }
    public void setProcessado(boolean processado) { this.processado = processado; }

    @Override
    public String toString() {
        return "AgendamentoRetirada{" +
                "placa='" + placa + '\'' +
                ", dataRetirada='" + dataRetirada + '\'' +
                ", dataDevolucao='" + dataDevolucao + '\'' +
                ", processado=" + processado +
                '}';
    }
}
