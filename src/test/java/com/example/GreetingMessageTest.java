package com.example;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class GreetingMessageTest {

    @Test
    public void testGreetingMessage() {
        GreetingMessage message = GreetingMessage.of("Say Hello to JakartaEE");
        assertTrue("message should contains `Say Hello to JakartaEE`",
                "Say Hello to JakartaEE".equals(message.getMessage()
                ));
    }
}
