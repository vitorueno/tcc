package br.edu.ifc.blumenau.tests;

import com.github.javafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;


public class DummyTest {
    private Faker faker;

    @BeforeEach
    void setUp() {
        this.faker = new Faker();
    }

    @Test
    void testAlwaysTrue() {
        assertTrue(true);
    }

    @Test
    void testAssertInline() {
        org.junit.jupiter.api.Assertions.assertEquals("bla", "bla");
    }

}
