package br.edu.ifc.blumenau.analyzer;

public enum AssertJAsserts {
    ASSERT_THAT("assertThat"),
    ASSERT_THAT_PREDICATE("assertThatPredicate"),
    ASSERT_THAT_THROWN_BY("assertThatThrownBy"),
    ASSERT_THAT_CODE("assertThatCode"),
    ASSERT_THAT_OBJECT("assertThatObject"),
    ASSERT_WITH("assertWith"),
    ASSERT_THAT_EXCEPTION_OF_TYPE("assertThatExceptionOfType"),
    ASSERT_THAT_NO_EXCEPTION("assertThatNoException"),
    ASSERT_THAT_NULL_POINTER_EXCEPTION("assertThatNullPointerException"),
    ASSERT_THAT_ILLEGAL_ARGUMENT_EXCEPTION("assertThatIllegalArgumentException"),
    ASSERT_THAT_IO_EXCEPTION("assertThatIOException"),
    ASSERT_THAT_ILLEGAL_STATE_EXCEPTION("assertThatIllegalStateException"),
    ASSERT_THAT_EXCEPTION("assertThatException"),
    ASSERT_THAT_RUNTIME_EXCEPTION("assertThatRuntimeException"),
    ASSERT_THAT_REFLECTIVE_OPERATION_EXCEPTION("assertThatReflectiveOperationException"),
    ASSERT_THAT_INDEX_OUT_OF_BOUNDS_EXCEPTION("assertThatIndexOutOfBoundsException"),
    FAIL("fail"),
    FAIL_BECAUSE_EXCEPTION_WAS_NOT_THROWN("failBecauseExceptionWasNotThrown"),
    ASSERT_THAT_CHAR_SEQUENCE("assertThatCharSequence"),
    ASSERT_THAT_ITERABLE("assertThatIterable"),
    ASSERT_THAT_ITERATOR("assertThatIterator"),
    ASSERT_THAT_COLLECTION("assertThatCollection"),
    ASSERT_THAT_LIST("assertThatList"),
    ASSERT_THAT_STREAM("assertThatStream"),
    ASSERT_THAT_PATH("assertThatPath"),
    ASSERT_THAT_COMPARABLE("assertThatComparable");

    private final String methodName;

    AssertJAsserts(String methodName) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }
}


