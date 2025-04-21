package org.socialclub.socialclub.util;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Un manejador personalizado para registrar mensajes en un área de texto de JavaFX.
 * Esta clase extiende la clase Handler del paquete java.util.logging.
 * Sobrescribe los métodos publish, flush y close para manejar los registros de log y mostrarlos en el área de texto.
 */
public class TextAreaHandler extends Handler {
    private final TextArea textArea;

    /**
     * Construye un nuevo TextAreaHandler con el área de texto especificada.
     *
     * @param textArea El área de texto donde se mostrarán los mensajes de registro.
     */
    public TextAreaHandler(TextArea textArea) {
        this.textArea = textArea;
    }

    /**
     * Publica un registro de log en el área de texto.
     * Si el formateador es nulo, establece un SimpleFormatter como el formateador predeterminado.
     * El mensaje de log formateado se luego anexa al área de texto utilizando Platform.runLater para asegurar la seguridad de subprocesos.
     * Si se produce una excepción durante el formateo, informa el error utilizando el método reportError.
     *
     * @param logRecord El registro de log que se publicará.
     */
    @Override
    public void publish(LogRecord logRecord) {
        if (getFormatter() == null) {
            setFormatter(new java.util.logging.SimpleFormatter());
        }
        try {
            String message = getFormatter().format(logRecord);
            Platform.runLater(() -> textArea.appendText(message));
        } catch (Exception ex) {
            reportError(null, ex, ErrorManager.FORMAT_FAILURE);
        }
    }

    /**
     * Vacía cualquier salida almacenada en búfer.
     * Este método no realiza ninguna acción en esta implementación.
     */
    @Override
    public void flush() {
        // No se requiere ninguna acción
    }

    /**
     * Cierra el manejador y libera cualquier recurso.
     * Este método no realiza ninguna acción en esta implementación.
     *
     * @throws SecurityException Si existe un administrador de seguridad y su método checkWrite deniega el acceso de escritura al manejador.
     */
    @Override
    public void close() throws SecurityException {
        // No se requiere ninguna acción
    }
}