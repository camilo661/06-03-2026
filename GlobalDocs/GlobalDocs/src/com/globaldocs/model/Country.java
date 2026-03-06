package com.globaldocs.model;

public enum Country {
    COLOMBIA("Colombia", "CO", "🇨🇴"),
    MEXICO("México", "MX", "🇲🇽"),
    ARGENTINA("Argentina", "AR", "🇦🇷"),
    CHILE("Chile", "CL", "🇨🇱");

    private final String nombre;
    private final String codigo;
    private final String bandera;

    Country(String nombre, String codigo, String bandera) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.bandera = bandera;
    }

    public String getNombre() { return nombre; }
    public String getCodigo() { return codigo; }
    public String getBandera() { return bandera; }

    @Override
    public String toString() { return bandera + " " + nombre; }
}
