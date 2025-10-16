package org.example.carrosuenp;

public class Motorista {
        private String codigo;
        private String nome;
        private String cnh;
        private String setor;

        public Motorista(String codigo, String nome, String cnh, String setor) {
            this.codigo = codigo;
            this.nome = nome;
            this.cnh = cnh;
            this.setor = setor;
        }
        public Motorista(){}


        public String getCodigo() {
            return codigo;
        }

        public void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public String getCnh() {
            return cnh;
        }

        public void setCnh(String cnh) {
            this.cnh = cnh;
        }

        public String getSetor() {
            return setor;
        }

        public void setSetor(String setor) {
            this.setor = setor;
        }
    }


