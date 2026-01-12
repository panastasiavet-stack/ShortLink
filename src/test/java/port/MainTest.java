package port;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void messageShouldReturnHelloWorld() {
        assertEquals("Hello!", Main.message());
    }
}
