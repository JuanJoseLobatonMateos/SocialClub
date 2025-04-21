// HuellaController.java
package org.socialclub.socialclub.controller;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.processing.DPFPEnrollment;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.processing.DPFPTemplateStatus;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.socialclub.socialclub.util.TextAreaHandler;

import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controlador para la funcionalidad de huellas dactilares.
 * Procesa la captura de huellas, extrae características,
 * y realiza la inscripción de la huella en la plantilla.
 */
public class HuellaController {

    private static final Logger LOGGER = Logger.getLogger(HuellaController.class.getName());
    public TextArea txtArea;

    @FXML
    private ImageView imgHuella;

    private DPFPCapture capture;
    private DPFPEnrollment enrollment;
    private static Image huellaImage;

    @FXML
    private void initialize() {
        capture = DPFPGlobal.getCaptureFactory().createCapture();
        enrollment = DPFPGlobal.getEnrollmentFactory().createEnrollment();

        // Añadir el TextAreaHandler al Logger
        Logger logger = Logger.getLogger(HuellaController.class.getName());
        logger.addHandler(new TextAreaHandler(txtArea));

        capture.addDataListener(new DPFPDataAdapter() {
            @Override
            public void dataAcquired(DPFPDataEvent e) {
                Platform.runLater(() -> processCapture(e.getSample()));
            }
        });

        capturarHuella();
    }

    /**
     * Procesa la captura de huellas y realiza la inscripción.
     *
     * @param sample La muestra de huella dactilar capturada.
     */
    private void processCapture(DPFPSample sample) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.info("Captura de huella recibida.");
        }
        DPFPFeatureSet features = extractFeatures(sample);
        if (features != null) {
            try {
                enrollment.addFeatures(features);
                Image image = createImage(sample);
                if (image != null) {
                    setHuellaImage(image); // Update static field using synchronized method
                    imgHuella.setImage(image);
                }

                // Verificar si la plantilla está lista
                if (enrollment.getTemplateStatus() == DPFPTemplateStatus.TEMPLATE_STATUS_READY) {
                    DPFPTemplate template = enrollment.getTemplate();
                    if (LOGGER.isLoggable(Level.INFO)) {
                        LOGGER.info("Plantilla lista, captura completada.");
                    }
                    detenerCaptura();

                    // Guardar la plantilla de huella
                    guardarPlantilla(template);

                    // Cerrar la ventana
                    Stage stage = (Stage) imgHuella.getScene().getWindow();
                    stage.close();
                }
            } catch (DPFPImageQualityException ex) {
                LOGGER.log(Level.SEVERE, "Error de calidad de imagen", ex);
            }
        }
    }

    /**
     * Guarda la plantilla de huella en un archivo.
     *
     * @param template La plantilla de huella a guardar.
     */
    private void guardarPlantilla(DPFPTemplate template) {
        try {
            // Guardar la plantilla en un archivo
            java.nio.file.Path path = java.nio.file.Paths.get("huellaTemplate.dat");
            java.nio.file.Files.write(path, template.serialize());
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.info(String.format("Plantilla de huella guardada en: %s", path.toAbsolutePath()));
            }
        } catch (java.io.IOException e) {
            LOGGER.log(Level.SEVERE, "Error al guardar la plantilla de huella", e);
        }
    }

    /**
     * Extrae las características de la huella dactilar.
     *
     * @param sample La muestra de huella dactilar.
     * @return Las características extraídas de la huella.
     */
    private DPFPFeatureSet extractFeatures(DPFPSample sample) {
        try {
            return DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction().createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
        } catch (DPFPImageQualityException e) {
            LOGGER.log(Level.SEVERE, "Error al extraer características de la huella", e);
            return null;
        }
    }

    /**
     * Crea una imagen a partir de la muestra de huella dactilar.
     *
     * @param sample La muestra de huella dactilar.
     * @return La imagen creada a partir de la muestra.
     */
    private Image createImage(DPFPSample sample) {
        java.awt.Image awtImage = DPFPGlobal.getSampleConversionFactory().createImage(sample);
        BufferedImage bufferedImage = new BufferedImage(awtImage.getWidth(null), awtImage.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        bufferedImage.getGraphics().drawImage(awtImage, 0, 0, null);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    /**
     * Inicia la captura de huellas.
     */
    void capturarHuella() {
        try {
            capture.startCapture();
            LOGGER.info("Capturando huella...");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al iniciar la captura de huella", e);
        }
    }

    /**
     * Detiene la captura de huellas.
     */
    void detenerCaptura() {
        try {
            capture.stopCapture();
            LOGGER.info("Deteniendo captura...");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al detener la captura de huella", e);
        }
    }

    /**
     * Actualiza el campo estático de imagen de huella con una imagen dada.
     * Este método es sincronizado para evitar problemas de concurrencia.
     *
     * @param image La imagen de huella a establecer.
     */
    private static synchronized void setHuellaImage(Image image) {
        huellaImage = image;
    }

    /**
     * Obtiene la imagen de huella actual.
     * Este método es sincronizado para evitar problemas de concurrencia.
     *
     * @return La imagen de huella actual.
     */
    public static synchronized Image getHuellaImage() {
        return huellaImage;
    }
}