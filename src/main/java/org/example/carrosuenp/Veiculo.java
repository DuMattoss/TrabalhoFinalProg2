package org.example.carrosuenp;

public class Veiculo {
    String marca;
    String modelo;
    String placa;
    boolean disponivel = true;


    public Veiculo(String marca, String modelo, String placa){
        this.marca=marca;
        this.modelo=modelo;
        this.placa=placa;
        this.disponivel = true;
    }

    public Veiculo(){}

    public void setMarca(String marca){
        this.marca=marca;
    }

    public String getMarca(){
        return marca;
    }

    public void setModelo(String modelo){
        this.modelo=modelo;
    }

    public String getModelo(){
        return modelo;
    }

    public void setPlaca(String placa){
        this.placa=placa;
    }

    public String getPlaca(){
        return placa;
    }
    public boolean getDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }
}
