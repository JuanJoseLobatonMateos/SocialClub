package org.socialclub.socialclub.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase utilitaria para cargar vistas en un panel de anclaje.
 */
public class ViewLoader {

    private static final Logger LOGGER = Logger.getLogger(ViewLoader.class.getName());

    /**
     * Constructor privado para ocultar el constructor público implícito.
     * Evita la instanciación de esta clase.
     */
    private ViewLoader() {
        // Evitar la instanciación de esta clase
    }

    /**
     * Carga una vista FXML en un panel de anclaje.
     *
     * @param pane     El panel de anclaje donde se cargará la vista.
     * @param rutaFXML La ruta del archivo FXML a cargar.
     */
    public static void cargarVistaEnPanel(AnchorPane pane, String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(ViewLoader.class.getResource(rutaFXML));
            Parent root = loader.load();
            pane.getChildren().setAll(root);
        } catch (IOException e) {
            logError(() -> "Error al cargar la vista: " + rutaFXML, e);
        }
    }

    /**
     * Registra un mensaje de error utilizando un proveedor de mensajes y una excepción.
     *
     * @param messageSupplier El proveedor de mensajes que genera el mensaje de error.
     * @param throwable       La excepción que se registrará.
     */
    private static void logError(Supplier<String> messageSupplier, Throwable throwable) {
        if (LOGGER.isLoggable(Level.SEVERE)) {
            LOGGER.log(Level.SEVERE, messageSupplier.get(), throwable);
        }
    }
}