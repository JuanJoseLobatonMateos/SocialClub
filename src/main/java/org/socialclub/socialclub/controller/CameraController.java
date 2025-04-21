package org.socialclub.socialclub.controller;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamException;
import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para manejar la cámara y capturar imágenes.
 */
public class CameraController {

    private static final Logger LOGGER = Logger.getLogger(CameraController.class.getName());

    @FXML
    public AnchorPane pnlCamera;
    @FXML
    public MFXIconButton helpIcon;
    @FXML
    Ellipse elipsePhoto;
    @FXML
    private MFXComboBox<String> comboCameras;
    @FXML
    private ImageView imgLiveCamera; // Imagen en vivo de la cámara

    @FXML
    private Canvas canvasPhotoCamera; // Canvas para mostrar la imagen capturada

    @FXML
    private MFXButton btnOff; // Botón para apagar la cámara

    @FXML
    private MFXButton btnOn; // Botón para encender la cámara

    @FXML
    private MFXButton btnShot; // Botón para capturar la foto

    @FXML
    private MFXButton btnSave; // Botón para guardar la foto

    private Webcam webcam; // Variable para la cámara

    /**
     * Inicializa el controlador, cargando las cámaras disponibles y configurando los listeners.
     */
    @FXML
    private void initialize() {
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
        // Cargar cámaras disponibles en el ComboBox
        loadAvailableCameras();
        btnShot.setDisable(true); // Desactivar botón de captura al inicio
        btnSave.setDisable(true);  // Desactivar botón de guardar al inicio
        btnOff.setDisable(true); // Desactivar botón de apagado al inicio
        elipsePhoto.setVisible(false);

        // Añadir un listener al ComboBox para cambiar de cámara automáticamente
        comboCameras.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                handleOnCamera(); // Cambiar de cámara automáticamente
            }
        });
        // Añadir un ChangeListener para asegurarse de que la escena esté disponible
        pnlCamera.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.windowProperty().addListener((obs, oldWindow, newWindow) -> {
                    if (newWindow != null) {
                        releaseCamera();
                        ((Stage) newWindow).close();
                    }
                });
            }
        });
    }

    /**
     * Carga las cámaras disponibles en el ComboBox.
     */
    private void loadAvailableCameras() {
        List<Webcam> webcams = Webcam.getWebcams(); // Obtener lista de cámaras
        for (Webcam cam : webcams) {
            comboCameras.getItems().add(cam.getName()); // Agregar cada cámara al ComboBox
        }
        comboCameras.getSelectionModel().selectFirst(); // Selecciona la primera cámara por defecto
    }

    /**
     * Maneja el encendido de la cámara seleccionada.
     */
    @FXML
    private void handleOnCamera() {
        // Obtener la cámara seleccionada
        String cameraName = comboCameras.getSelectionModel().getSelectedItem();

        if (cameraName != null && !cameraName.isEmpty()) { // Verificar que se ha seleccionado una cámara
            if (webcam != null && webcam.isOpen()) {
                webcam.close(); // Cierra la cámara actual antes de cambiar a la nueva
            }

            webcam = Webcam.getWebcamByName(cameraName); // Cambiar a la cámara seleccionada

            if (webcam != null) {
                try {
                    webcam.setViewSize(new Dimension(640, 480)); // Cambia la resolución
                    startCamera(); // Iniciar el stream de la cámara

                    btnShot.setDisable(false); // Habilitar botón de captura
                    btnOff.setDisable(false); // Habilitar botón de apagado
                    btnSave.setDisable(true);  // Desactivar botón de guardar
                    elipsePhoto.setVisible(true); // Mostrar elíptica de foto
                    btnOn.setDisable(true); // Desactivar botón de encendido
                } catch (WebcamException e) {
                    LOGGER.log(Level.SEVERE, "Error al abrir la cámara: {0}", e.getMessage());
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error inesperado: {0}", e.getMessage());
                }
            } else {
                LOGGER.log(Level.WARNING, "No se pudo encontrar la cámara: {0}", cameraName);
            }
        } else {
            LOGGER.log(Level.WARNING, "No se ha seleccionado ninguna cámara.");
        }
    }

    /**
     * Maneja el apagado de la cámara.
     */
    @FXML
    private void handleOffCamera() {
        // Apagar la cámara
        if (webcam != null && webcam.isOpen()) {
            stopCamera(); // Detener la cámara
            btnShot.setDisable(true); // Desactivar botón de captura
            btnSave.setDisable(true);  // Desactivar botón de guardar
            btnOff.setDisable(true); // Desactivar botón de apagado
            imgLiveCamera.setImage(null); // Limpiar la imagen en vivo
            canvasPhotoCamera.getGraphicsContext2D().clearRect(0, 0, canvasPhotoCamera.getWidth(), canvasPhotoCamera.getHeight()); // Limpiar el Canvas
            elipsePhoto.setVisible(false);
        }
    }

    /**
     * Maneja la captura de una foto desde la cámara.
     */
    @FXML
    private void handleCapturePhoto() {
        // Captura una foto de la cámara
        if (webcam != null && webcam.isOpen()) {
            // Variable para almacenar la imagen capturada
            BufferedImage capturedBufferedImage = webcam.getImage(); // Captura la imagen en formato BufferedImage

            // Recortar y redimensionar la imagen
            BufferedImage croppedImage = cropAndResizeImage(capturedBufferedImage);
            Image fxImage = convertToFXImage(croppedImage);

            // Dibujar la imagen en el Canvas
            drawImageOnCanvas(fxImage);

            btnSave.setDisable(false); // Habilitar botón de guardar
        } else {
            LOGGER.log(Level.WARNING, "La cámara no está abierta.");
        }
    }

    /**
     * Recorta y redimensiona la imagen capturada.
     *
     * @param originalImage La imagen original capturada.
     * @return La imagen recortada y redimensionada.
     */
    private BufferedImage cropAndResizeImage(BufferedImage originalImage) {
        int targetWidth = 60;
        int targetHeight = 80;
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        // Calcular las coordenadas para recortar la imagen al centro
        int cropX = (width - targetWidth * 5) / 2;
        int cropY = (height - targetHeight * 5) / 2;

        // Recortar la imagen a 300x400 píxeles
        BufferedImage croppedImage = originalImage.getSubimage(cropX, cropY, targetWidth * 5, targetHeight * 5);

        // Redimensionar la imagen al doble de su tamaño original
        int scaledWidth = (int) (413 * 1.3);
        int scaledHeight = (int) (531 * 1.3);
        BufferedImage resizedImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(croppedImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return resizedImage;
    }

    /**
     * Dibuja la imagen en el Canvas.
     *
     * @param fxImage La imagen a dibujar.
     */
    private void drawImageOnCanvas(Image fxImage) {
        if (fxImage == null) {
            LOGGER.log(Level.WARNING, "La imagen fxImage es nula.");
            return; // Salir del método si la imagen es nula
        }

        // Obtener el GraphicsContext del Canvas
        GraphicsContext gc = canvasPhotoCamera.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasPhotoCamera.getWidth(), canvasPhotoCamera.getHeight()); // Limpiar el Canvas

        // Dimensiones del Canvas
        double canvasWidth = canvasPhotoCamera.getWidth();
        double canvasHeight = canvasPhotoCamera.getHeight();

        // Aplicar el factor de recorte para obtener las dimensiones escaladas
        double scaledWidth = fxImage.getWidth() * 0.4;
        double scaledHeight = fxImage.getHeight() * 0.4;

        // Calcular las coordenadas para dibujar la imagen centrada en el Canvas
        double drawX = (canvasWidth - scaledWidth) / 2; // Centrado horizontalmente
        double drawY = (canvasHeight - scaledHeight) / 2; // Centrado verticalmente

        // Dibujar la imagen escalada en el Canvas
        gc.drawImage(fxImage, drawX, drawY, scaledWidth, scaledHeight);
    }

    /**
     * Maneja el guardado de la foto capturada.
     */
    @FXML
    private void handleSavePhoto() {
        // Captura la imagen del Canvas
        WritableImage writableImage = new WritableImage((int) canvasPhotoCamera.getWidth(), (int) canvasPhotoCamera.getHeight());
        canvasPhotoCamera.snapshot(null, writableImage);

        // Convertir WritableImage a BufferedImage
        BufferedImage bufferedImage = convertToBufferedImage(writableImage);

        // Convertir BufferedImage a Image de JavaFX
        Image fxImage = convertToFXImage(bufferedImage);

        // Libera la cámara
        releaseCamera();

        // Cierra la ventana del CameraController
        Stage stage = (Stage) canvasPhotoCamera.getScene().getWindow();
        stage.close();

        // Devuelve la imagen capturada
        imgLiveCamera.setImage(fxImage);
    }

    /**
     * Convierte una WritableImage a BufferedImage.
     *
     * @param writableImage La imagen a convertir.
     * @return La imagen convertida en BufferedImage.
     */
    private BufferedImage convertToBufferedImage(WritableImage writableImage) {
        // Convierte WritableImage a BufferedImage
        BufferedImage bufferedImage = new BufferedImage(
                (int) writableImage.getWidth(), (int) writableImage.getHeight(), BufferedImage.TYPE_INT_ARGB);

        for (int x = 0; x < writableImage.getWidth(); x++) {
            for (int y = 0; y < writableImage.getHeight(); y++) {
                bufferedImage.setRGB(x, y, writableImage.getPixelReader().getArgb(x, y)); // Asigna el color del píxel
            }
        }
        return bufferedImage; // Retorna la imagen convertida
    }

    /**
     * Inicia el stream de la cámara.
     */
    private void startCamera() {
        try {
            // Iniciar el stream de la cámara
            if (webcam != null && !webcam.isOpen()) {
                webcam.open(); // Abre la cámara
                new Thread(() -> {
                    while (webcam.isOpen()) {
                        // Obtener la imagen y mostrarla
                        BufferedImage image = webcam.getImage();
                        if (image != null) {
                            Image fxImage = convertToFXImage(image); // Convertir a Image de JavaFX
                            imgLiveCamera.setImage(fxImage); // Mostrar imagen en ImageView
                        }
                    }
                }).start();
            }
        } catch (WebcamException e) {
            LOGGER.log(Level.SEVERE, "Error al abrir la cámara: {0}", e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error inesperado: {0}", e.getMessage());
        }
    }

    /**
     * Detiene el stream de la cámara.
     */
    private void stopCamera() {
        // Detener el stream de la cámara
        if (webcam != null && webcam.isOpen()) {
            webcam.close(); // Cerrar la cámara
            LOGGER.log(Level.INFO, "Cámara cerrada.");
        }
    }

    /**
     * Convierte un BufferedImage a Image de JavaFX.
     *
     * @param bufferedImage La imagen a convertir.
     * @return La imagen convertida en Image de JavaFX.
     */
    private Image convertToFXImage(BufferedImage bufferedImage) {
        // Convierte BufferedImage a WritableImage de JavaFX
        WritableImage fxImage = new WritableImage(bufferedImage.getWidth(), bufferedImage.getHeight());
        PixelWriter pixelWriter = fxImage.getPixelWriter();
        // Copia píxeles de BufferedImage a WritableImage
        for (int x = 0; x < bufferedImage.getWidth(); x++) {
            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                pixelWriter.setArgb(x, y, bufferedImage.getRGB(x, y)); // Asigna el color del píxel
            }
        }
        return fxImage; // Retorna la imagen convertida
    }

    /**
     * Libera la cámara.
     */
    public void releaseCamera() {
        if (webcam != null) {
            stopCamera(); // Detener la cámara si está abierta
            webcam.close(); // Cerrar la cámara
            webcam = null; // Liberar la referencia
            LOGGER.log(Level.INFO, "Cámara liberada.");
        }
    }

    /**
     * Obtiene la imagen capturada.
     *
     * @return La imagen capturada.
     */
    public Image getCapturedImage() {
        return imgLiveCamera.getImage();
    }

    /**
     * Maneja el evento de clic en el icono de ayuda.
     * Carga la vista de ayuda, inicializa el controlador de ayuda con la URL de ayuda,
     * y muestra la ventana de ayuda como una ventana modal y siempre en primer plano.
     *
     * @param event El evento de clic que desencadena este método.
     */
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            // Cargar el archivo FXML de la vista de ayuda
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_camara.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda cámara");
            helpStage.setScene(new Scene(helpRoot));
            helpStage.getIcons().add(new Image("/images/logo.png"));

            // Configurar la ventana como modal y siempre en primer plano
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.initOwner((((MFXIconButton) event.getSource()).getScene().getWindow()));
            helpStage.setAlwaysOnTop(true);
            helpStage.setResizable(false);

            helpStage.showAndWait();
        } catch (IOException e) {
            LOGGER.severe("Error al cargar la ventana de ayuda: " + e.getMessage());
        }
    }
}