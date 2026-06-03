package com.example.vendaroupas.model;

public enum Categoria {

    CAMISA(1),
    SHORT(2),
    CALCA(3),
    CASACO(4),
    TENIS(5);

    private final int codigo;

    Categoria(int codigo) {
        this.codigo = codigo;
    }

    public int getCodigo() {
        return codigo;
    }

    public static Categoria fromCodigo(int codigo) {
        for (Categoria categoria : values()) {
            if (categoria.codigo == codigo) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Código inválido: " + codigo);
    }
}