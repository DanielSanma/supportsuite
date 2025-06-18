package com.telco.security.controller.impl;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class TestControllerTest {

    private final TestController controller = new TestController();

    @Test
    void helloPublic_returnsOkMessage() {
        ResponseEntity<String> response = controller.helloPublic();
        assertEquals(200, response);
        assertEquals("Hola! Este es un endpoint público.", response.getBody());
    }

    @Test
    void helloSupervisor_returnsOkMessage() {
        ResponseEntity<String> response = controller.helloSupervisor();
        assertEquals(200, response);
        assertEquals("Hola, Supervisor! Has accedido a un recurso protegido.", response.getBody());
    }
}

