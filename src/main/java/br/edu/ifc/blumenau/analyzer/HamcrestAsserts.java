package br.edu.ifc.blumenau.analyzer;

public enum HamcrestAsserts {
    assert_That("assertThat");

    private final String methodName;
    private final String assertOrigin;

    HamcrestAsserts(String methodName) {
        this.methodName = methodName;
        this.assertOrigin = "hamcrest";
    }

    public String getAssertOrigin() {
        return assertOrigin;
    }

    public String getMethodName() {
        return methodName;
    }
}
