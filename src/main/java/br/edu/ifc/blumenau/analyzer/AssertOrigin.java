package br.edu.ifc.blumenau.analyzer;

public enum AssertOrigin {
    JUNIT_4("junit4"),
    JUNIT_5("junit5"),
    ASSERTJ("assertj"),
    HAMCREST("hamcrest"),
    DESCONHECIDO("desconhecido");

    private final String origin;

    AssertOrigin(String origin) {
        this.origin = origin;
    }

    public String getValue() {
        return origin;
    }
}
