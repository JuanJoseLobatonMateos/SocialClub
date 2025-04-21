package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static javafx.scene.paint.Color.TRANSPARENT;

/**
 * Controlador para manejar los diálogos de información y confirmación.
 */
public class DialogoController {
    private static final Logger LOGGER = Logger.getLogger(DialogoController.class.getName());

    @FXML
    private DialogPane dialogo;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Constructor vacío.
     */
    public DialogoController() {
        // Constructor vacío
    }

    /**
     * Método de inicialización que se llama automáticamente cuando se carga el controlador.
     */
    @FXML
    public void initialize() {
        // Este método se llama automáticamente cuando se inicializa el controlador
    }

    /**
     * Muestra un diálogo de información.
     *
     * @param primaryStage La ventana principal.
     * @param message      El mensaje a mostrar.
     * @param onAccept     El manejador de eventos para el botón de aceptar.
     */
    public static void showInfoDialog(Stage primaryStage, String message, EventHandler<ActionEvent> onAccept) {
        showDialog(primaryStage, message, onAccept, false);
    }

    /**
     * Muestra un diálogo de confirmación.
     *
     * @param primaryStage La ventana principal.
     * @param message      El mensaje a mostrar.
     * @param onYes        El manejador de eventos para el botón de sí.
     */
    public static void showConfirmDialog(Stage primaryStage, String message, EventHandler<ActionEvent> onYes) {
        showDialog(primaryStage, message, onYes, true);
    }

    /**
     * Muestra un diálogo.
     *
     * @param primaryStage    La ventana principal.
     * @param message         El mensaje a mostrar.
     * @param onYes           El manejador de eventos para el botón de sí.
     * @param isConfirmDialog Indica si es un diálogo de confirmación.
     */
    private static void showDialog(Stage primaryStage, String message, EventHandler<ActionEvent> onYes, boolean isConfirmDialog) {
        try {
            FXMLLoader loader = new FXMLLoader(DialogoController.class.getClassLoader().getResource("View/dialogo.fxml"));
            Parent root = loader.load(); // Cargar el FXML y obtener la raíz
            DialogoController controller = loader.getController(); // Obtener el controlador después de cargar

            if (controller != null) {
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                scene.setFill(TRANSPARENT);
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL); // Bloquear la ventana principal
                stage.setAlwaysOnTop(false);
                stage.initStyle(StageStyle.TRANSPARENT); // Estilo de ventana transparente

                // Aplicar efecto de desenfoque a la ventana principal
                GaussianBlur blur = new GaussianBlur(10);
                primaryStage.getScene().getRoot().setEffect(blur);

                // Eliminar el efecto de desenfoque cuando se cierra el diálogo
                stage.setOnHidden(event -> primaryStage.getScene().getRoot().setEffect(null));

                controller.configureDialog(message, onYes, isConfirmDialog);
                controller.makeDialogDraggable(stage, root); // Hacer que el diálogo sea arrastrable

                // Centrar el diálogo en la ventana principal
                stage.setOnShown(event -> {
                    stage.setX(primaryStage.getX() + (primaryStage.getWidth() - stage.getWidth()) / 2);
                    stage.setY(primaryStage.getY() + (primaryStage.getHeight() - stage.getHeight()) / 2);
                });

                stage.showAndWait();
            } else {
                LOGGER.severe("Controller not found.");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error loading dialog FXML", e);
        }
    }

    /**
     * Configura el diálogo con el mensaje y los manejadores de eventos.
     *
     * @param message         El mensaje a mostrar.
     * @param onYes           El manejador de eventos para el botón de sí.
     * @param isConfirmDialog Indica si es un diálogo de confirmación.
     */
    private void configureDialog(String message, EventHandler<ActionEvent> onYes, boolean isConfirmDialog) {
        if (dialogo != null) {
            dialogo.setContentText(message);
            dialogo.setExpanded(true);

            if (isConfirmDialog) {
                dialogo.setGraphic(new MFXIconWrapper("fas-arrow-right-from-bracket", 30, Color.web("#ff0000"), 40));

                ButtonType yesButtonType = new ButtonType("Sí", ButtonType.YES.getButtonData());
                ButtonType noButtonType = new ButtonType("No", ButtonType.NO.getButtonData());
                dialogo.getButtonTypes().setAll(yesButtonType, noButtonType);
                assignHandler(yesButtonType, onYes);
                assignHandler(noButtonType, event -> dialogo.getScene().getWindow().hide());
            } else {
                dialogo.setGraphic(new MFXIconWrapper("fas-info", 30, Color.web("#ffffff"), 40));

                ButtonType acceptButtonType = new ButtonType("Aceptar", ButtonType.OK.getButtonData());
                dialogo.getButtonTypes().setAll(acceptButtonType);
                assignHandler(acceptButtonType, onYes);
            }

            // Estilizar los botones del diálogo
            dialogo.getButtonTypes().stream()
                    .map(dialogo::lookupButton)
                    .forEach(button -> button.getStyleClass().add("mfx-button"));
        } else {
            LOGGER.severe("DialogPane not found.");
        }
    }

    /**
     * Asigna un manejador de eventos a un botón en el DialogPane.
     *
     * @param buttonType El tipo de botón al que se asignará el manejador.
     * @param handler    El manejador de eventos a asignar.
     */
    private void assignHandler(ButtonType buttonType, EventHandler<ActionEvent> handler) {
        ButtonBase button = (ButtonBase) dialogo.lookupButton(buttonType);
        if (button != null && handler != null) { // Verificar que el handler no sea nulo
            button.setOnAction(event -> {
                handler.handle(event);
                Platform.runLater(() -> dialogo.getScene().getWindow().hide());
            });
        } else {
            if (button == null && LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.severe(String.format("ButtonType %s not found in DialogPane.", buttonType));
            }
            if (handler == null && LOGGER.isLoggable(Level.SEVERE)) {
                LOGGER.severe(String.format("Handler for %s is null.", buttonType));
            }
        }
    }

    /**
     * Hace que el diálogo sea arrastrable.
     *
     * @param stage La ventana del diálogo.
     * @param root  El nodo raíz del diálogo.
     */
    private void makeDialogDraggable(Stage stage, Parent root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}