package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import org.socialclub.socialclub.util.ViewLoader;

/**
 * Esta clase representa el controlador para la vista de ajustes en la aplicación Social Club.
 * Gestiona la funcionalidad relacionada con la adición de empleados, instalaciones y socios.
 */
public class SettingsController {

    private static final String TRANSPARENT_BUTTON_CLASS = "transparent-button";

    @FXML
    private MFXButton btnAddEmpleado;

    @FXML
    private MFXButton btnAddInstalacion;

    @FXML
    private MFXButton btnAddSocio;

    @FXML
    private AnchorPane pane; // Asegúrate de que esta línea esté presente

    @FXML
    private void initialize() {
        configurarIconos();
        handleAddEmpleado();
        btnAddEmpleado.setOnAction(e -> handleAddEmpleado());
        btnAddInstalacion.setOnAction(e -> handleAddInstalacion());
        btnAddSocio.setOnAction(e -> handleAddSocio());
    }

    /**
     * Gestiona la acción de añadir una nueva instalación.
     * Carga la vista "addInstalacion.fxml" en el panel de ancla especificado.
     */
    private void handleAddInstalacion() {
        ViewLoader.cargarVistaEnPanel(pane, "/View/addInstalacion.fxml");
    }

    /**
     * Gestiona la acción de añadir un nuevo socio.
     * Carga la vista "addSocio.fxml" en el panel de ancla especificado.
     */
    private void handleAddSocio() {
        ViewLoader.cargarVistaEnPanel(pane, "/View/addSocio.fxml");
    }

    /**
     * Configura los iconos para los botones de añadir empleado, añadir instalación y añadir socio.
     * También aplica la clase CSS "transparent-button" a los botones.
     */
    private void configurarIconos() {
        // Configurar los iconos para los botones
        MFXFontIcon icon1 = new MFXFontIcon("fas-person-circle-plus", 24, Color.WHITE);
        MFXFontIcon icon2 = new MFXFontIcon("fas-person-swimming", 24, Color.WHITE);
        MFXFontIcon icon3 = new MFXFontIcon("fas-table-tennis-paddle-ball", 24, Color.WHITE);

        // Asignar iconos a los botones
        btnAddEmpleado.setGraphic(new MFXIconWrapper(icon1, 24));
        btnAddInstalacion.setGraphic(new MFXIconWrapper(icon3, 24));
        btnAddSocio.setGraphic(new MFXIconWrapper(icon2, 24));

        // Aplicar la clase CSS a los botones
        btnAddEmpleado.getStyleClass().add(TRANSPARENT_BUTTON_CLASS);
        btnAddInstalacion.getStyleClass().add(TRANSPARENT_BUTTON_CLASS);
        btnAddSocio.getStyleClass().add(TRANSPARENT_BUTTON_CLASS);
    }

    /**
     * Gestiona la acción de añadir un nuevo empleado.
     * Carga la vista "addEmpleado.fxml" en el panel de ancla especificado.
     */
    @FXML
    private void handleAddEmpleado() {
        ViewLoader.cargarVistaEnPanel(pane, "/View/addEmpleado.fxml");
    }
}