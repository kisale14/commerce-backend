package com.empresa.gestionclientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

@SpringBootApplication
public class GestionClientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(GestionClientesApplication.class, args);
	}

	// Se ejecuta automáticamente cuando el servidor termina de arrancar
	@EventListener(ApplicationReadyEvent.class)
	public void abrirSwaggerEnNavegador() {
		String url = "http://localhost:8080/swagger-ui.html";
		System.setProperty("java.awt.headless", "false"); // Permitir interactuar con la interfaz gráfica del S.O.

		if (Desktop.isDesktopSupported()) {
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException | RuntimeException e) {
				System.out.println("No se pudo abrir el navegador automáticamente. Accede manualmente a: " + url);
			} catch (Exception e) {
				Thread.currentThread().interrupt();
			}
		}
	}
}