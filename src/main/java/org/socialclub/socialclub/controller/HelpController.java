package org.socialclub.socialclub.controller;

import javafx.fxml.FXML;
import javafx.scene.web.WebView;

import java.util.logging.Logger;

    /**
     * Controlador para la vista de ayuda.
     * Este controlador carga y muestra el contenido de ayuda en un WebView.
     */
    public class HelpController {
        @FXML
        private WebView webView;
        private static final Logger LOGGER = Logger.getLogger(HelpController.class.getName());

        /**
         * Inicializa el controlador de ayuda cargando la URL de ayuda en el WebView.
         *
         * @param helpUrl La URL del archivo de ayuda que se va a cargar.
         *                Si la URL es nula, se registra una advertencia en el logger.
         */
        public void initialize(String helpUrl) {
            if (helpUrl != null) {
                webView.getEngine().load(helpUrl);
            } else {
                LOGGER.warning("No se ha encontrado el archivo de ayuda.");
            }
        }
    }