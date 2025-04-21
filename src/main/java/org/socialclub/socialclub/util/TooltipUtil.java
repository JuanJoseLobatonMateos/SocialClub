package org.socialclub.socialclub.util;

import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;

/**
 * Una clase de utilidad para configurar tooltips para los componentes TextField y Spinner de JavaFX.
 * Esta clase no debería ser instanciada.
 */
public class TooltipUtil {

    private TooltipUtil() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Configura un tooltip para el campo de texto dado con el texto de tooltip especificado.
     * El tooltip se muestra cuando el mouse se posa sobre el campo de texto y se oculta cuando el mouse sale.
     *
     * @param textField El campo de texto para configurar el tooltip.
     * @param tooltipText El texto que se mostrará en el tooltip.
     */
    public static void setupTooltip(TextField textField, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        textField.setTooltip(tooltip);

        textField.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> tooltip.show(textField, event.getScreenX(), event.getScreenY() + 10));
        textField.addEventHandler(MouseEvent.MOUSE_EXITED, event -> tooltip.hide());
    }

    /**
     * Configura un tooltip para el Spinner dado con el texto de tooltip especificado.
     * El tooltip se muestra cuando el mouse se posa sobre el Spinner y se oculta cuando el mouse sale.
     *
     * @param spinner El Spinner para configurar el tooltip.
     * @param tooltipText El texto que se mostrará en el tooltip.
     * @param <T> El tipo del valor del Spinner.
     */
    public static <T> void setupTooltip(Spinner<T> spinner, String tooltipText) {
        Tooltip tooltip = new Tooltip(tooltipText);
        spinner.setTooltip(tooltip);

        spinner.addEventHandler(MouseEvent.MOUSE_ENTERED, event -> tooltip.show(spinner, event.getScreenX(), event.getScreenY() + 10));
        spinner.addEventHandler(MouseEvent.MOUSE_EXITED, event -> tooltip.hide());
    }
}