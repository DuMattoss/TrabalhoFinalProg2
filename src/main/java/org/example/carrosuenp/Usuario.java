package org.example.carrosuenp;

public class Usuario {
    public String nome;
    public String senha;
    public String login;

    public Usuario(String nome, String senha, String login){
        this.nome=nome;
        this.senha=senha;
        this.login=login;
    }
    public Usuario(){
        this.nome = "";
        this.login = "";
        this.senha = "";
    }

    public String getNome() {
        return nome;
    }

    public String getSenha(){
        return senha;
    }

    public String getLogin(){
        return login;
    }

    public void setSenha(String senha){
        this.senha = senha;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public void setLogin(String login){
        this.login = login;
    }

}

