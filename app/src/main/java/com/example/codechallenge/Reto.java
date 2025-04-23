package com.example.codechallenge;

public class Reto {
    private String titulo;
    private String descripcion;
    private int color;

    public Reto(String titulo, String descripcion, int color) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.color = color;
    }

    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public int getColor() { return color; }
}
