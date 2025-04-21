package org.socialclub.socialclub.controller;

import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.socialclub.socialclub.util.HibernateUtil;

import java.io.IOException;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador de la ventana principal de la aplicación.
 * Maneja la funcionalidad de la barra de herramientas, la carga de vistas y la gestión de la ventana.
 */
public class VentanaPrincipalController {
    private static final Logger logger = Logger.getLogger(VentanaPrincipalController.class.getName());
    private double xOffset = 0;
    private double yOffset = 0;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private HBox toolbar;

    @FXML
    private MFXFontIcon alwaysOnTopIcon;

    @FXML
    private MFXFontIcon minimizeIcon;

    @FXML
    private MFXFontIcon closeIcon;

    @FXML
    private AnchorPane panelPrincipal;
    @FXML
    public MFXFontIcon helpIcon;

    @FXML
    private void initialize() {
        loadVentanaPrincipal();
        // Añadir controlador de eventos de mouse pressed a la barra de herramientas
        toolbar.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        // Añadir controlador de eventos de mouse dragged a la barra de herramientas
        toolbar.setOnMouseDragged(event -> {
            Stage stage = (Stage) toolbar.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

        // Crear tooltips para los iconos
        Tooltip alwaysOnTopTooltip = new Tooltip("Primer plano");
        Tooltip minimizeTooltip = new Tooltip("Minimizar");
        Tooltip closeTooltip = new Tooltip("Cerrar");

        // Asignar tooltips a los iconos
        Tooltip.install(alwaysOnTopIcon, alwaysOnTopTooltip);
        Tooltip.install(minimizeIcon, minimizeTooltip);
        Tooltip.install(closeIcon, closeTooltip);
    }


    private void loadVentanaPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/View/login.fxml")));
            Parent initialView = loader.load();

            // Obtener el controlador de LoginController
            LoginController loginController = loader.getController();
            loginController.setVentanaPrincipalController(this); // Pasar la referencia

            panelPrincipal.getChildren().setAll(initialView);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar VentanaPrincipal", e);
        }
    }

    @FXML
    private void handleAlwaysOnTopIconClick(MouseEvent event) {
        Stage stage = (Stage) ((MFXFontIcon) event.getSource()).getScene().getWindow();
        stage.setAlwaysOnTop(!stage.isAlwaysOnTop());
    }


    @FXML
    private void handleMinimizeIconClick(MouseEvent event) {
        // Obtener la ventana actual y minimizarla
        Stage stage = (Stage) ((MFXFontIcon) event.getSource()).getScene().getWindow();
        stage.setIconified(true);
    }

    @FXML
    private void handleCloseIconClick(MouseEvent event) {
        Stage primaryStage = (Stage) ((MFXFontIcon) event.getSource()).getScene().getWindow();
        DialogoController.showConfirmDialog(primaryStage, "¿Estas seguro?", event1 -> {
            HibernateUtil.shutdown();
            Platform.exit();
            System.exit(0);
        });
    }

    /**
     * Carga la vista inicial en el panel principal.
     *
     * @param fxmlPath La ruta al archivo FXML de la vista que se desea cargar.
     */
    public void loadInitialView(String fxmlPath) {
        try {
            Parent initialView = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            panelPrincipal.getChildren().setAll(initialView);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar la vista inicial", e);
        }
    }

    /**
     * Maneja el evento de clic en el icono de ayuda.
     * Abre una ventana modal que muestra el contenido de la ayuda.
     *
     * @param event El evento de ratón que desencadenó este método.
     */
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            // Cargar el archivo FXML de la vista de ayuda
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_principal.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Toolbar");
            helpStage.setScene(new Scene(helpRoot));
            helpStage.getIcons().add(new Image("/images/logo.png"));

            // Configurar la ventana como modal y siempre en primer plano
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.initOwner((((MFXFontIcon) event.getSource()).getScene().getWindow()));
            helpStage.setAlwaysOnTop(true);
            helpStage.setResizable(false);

            helpStage.showAndWait();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error al cargar la ventana de ayuda", e);
        }
    }
}