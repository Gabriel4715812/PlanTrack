package com.plantrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Profile("demo")
public class BrowserLauncher {

    @Value("${server.port:8080}")
    private int serverPort;

    @EventListener(ApplicationReadyEvent.class)
    public void openBrowserAfterStartup() {
        new Thread(() -> {
            try {
                Thread.sleep(2000);

                String url = "http://localhost:" + serverPort + "/login";
                openBrowser(url);

            } catch (Exception e) {
                System.out.println("No se pudo abrir el navegador automáticamente.");
                System.out.println("Ingrese manualmente a: http://localhost:" + serverPort + "/login");
            }
        }).start();
    }

    private void openBrowser(String url) throws Exception {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            new ProcessBuilder("cmd", "/c", "start", "", url).start();
        } else if (os.contains("mac")) {
            new ProcessBuilder("open", url).start();
        } else {
            new ProcessBuilder("xdg-open", url).start();
        }
    }
}