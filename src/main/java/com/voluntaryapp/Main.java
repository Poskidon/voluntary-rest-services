package com.voluntaryapp;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        try {
            Tomcat tomcat = new Tomcat();
            tomcat.setPort(8080);

            // Obtenir le chemin absolu du répertoire webapp
            String webappDirPath = new File("src/main/webapp").getAbsolutePath();

            // Créer le répertoire s'il n'existe pas
            File webappDir = new File(webappDirPath);
            if (!webappDir.exists()) {
                webappDir.mkdirs();
            }

            // Configurer le contexte
            Context ctx = tomcat.addWebapp("", webappDirPath);
            ctx.setAllowCasualMultipartParsing(true);

            // Configurer le répertoire de base
            tomcat.getHost().setAppBase(webappDirPath);

            // Démarrer Tomcat
            tomcat.start();
            System.out.println("Serveur démarré sur http://localhost:8080/");
            tomcat.getServer().await();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}