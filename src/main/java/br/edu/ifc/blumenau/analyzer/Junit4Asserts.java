package br.edu.ifc.blumenau.analyzer;

public enum Junit4Asserts {
    ASSERT_ARRAY_EQUALS("assertArrayEquals"),
    ASSERT_EQUALS("assertEquals"),
    ASSERT_FALSE("assertFalse"),
    ASSERT_NOT_EQUALS("assertNotEquals"),
    ASSERT_NOT_NULL("assertNotNull"),
    ASSERT_NOT_SAME("assertNotSame"),
    ASSERT_NULL("assertNull"),
    ASSERT_SAME("assertSame"),
    ASSERT_THAT("assertThat"),
    ASSERT_THROWS("assertThrows"),
    ASSERT_TRUE("assertTrue"),
    FAIL("fail");

    private final String methodName;
    Junit4Asserts(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}
