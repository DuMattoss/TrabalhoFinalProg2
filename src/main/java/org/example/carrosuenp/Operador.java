package org.example.carrosuenp;

public class Operador extends Usuario {
    private String codigo;

    public Operador() {
        super();
    }

    public Operador(String nome, String senha, String login, String codigo) {
        super(nome, senha, login);
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
}
