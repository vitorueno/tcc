package br.edu.ifc.blumenau.analyzer;

public enum Junit5Asserts {
    ASSERT_ALL("assertAll"),
    ASSERT_ARRAY_EQUALS("assertArrayEquals"),
    ASSERT_EQUALS("assertEquals"),
    ASSERT_DOES_NOT_THROW("assertDoesNotThrow"),
    ASSERT_FALSE("assertFalse"),
    ASSERT_ITERABLE_EQUALS("assertIterableEquals"),
    ASSERT_LINES_MATCH("assertLinesMatch"),
    ASSERT_NOT_EQUALS("assertNotEquals"),
    ASSERT_NOT_NULL("assertNotNull"),
    ASSERT_NOT_SAME("assertNotSame"),
    ASSERT_INSTANCE_OF("assertInstanceOf"),
    ASSERT_NULL("assertNull"),
    ASSERT_SAME("assertSame"),
    ASSERT_THROWS("assertThrows"),
    ASSERT_THROWS_EXACTLY("assertThrowsExactly"),
    ASSERT_TIMEOUT("assertTimeout"),
    ASSERT_TIMEOUT_PREEMPTIVELY("assertTimeoutPreemptively"),
    ASSERT_TRUE("assertTrue"),
    FAIL("fail");

    private final String methodName;

    Junit5Asserts(String methodName) {
        this.methodName = methodName;

    }

    public String getMethodName() {
        return methodName;
    }
}
