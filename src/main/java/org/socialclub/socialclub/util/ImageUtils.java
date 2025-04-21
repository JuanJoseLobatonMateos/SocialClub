package org.socialclub.socialclub.util;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Clase de utilidad para operaciones relacionadas con imágenes.
 */
public class ImageUtils {

    private ImageUtils() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Convierte un objeto Blob a un BufferedImage.
     *
     * @param blob El objeto Blob a convertir.
     * @return El BufferedImage que representa al Blob.
     * @throws IOException Si se produce un error de E/S durante la conversión.
     * @throws SQLException Si se produce un error de acceso a la base de datos.
     */
    public static BufferedImage convertBlobToImage(Blob blob) throws IOException, SQLException {
        ByteArrayInputStream bais = new ByteArrayInputStream(blob.getBytes(1, (int) blob.length()));
        return ImageIO.read(bais);
    }

    /**
     * Convierte un objeto Blob a un objeto Image utilizando JavaFX.
     *
     * @param blob El objeto Blob a convertir.
     * @return El objeto Image que representa al Blob.
     * @throws IOException Si se produce un error de E/S durante la conversión.
     * @throws SQLException Si se produce un error de acceso a la base de datos.
     */
    public static Image convertBlobToFXImage(Blob blob) throws IOException, SQLException {
        BufferedImage bufferedImage = convertBlobToImage(blob);
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    /**
     * Convierte un array de bytes a un BufferedImage.
     *
     * @param bytes El array de bytes a convertir.
     * @return El BufferedImage que representa al array de bytes.
     * @throws IOException Si se produce un error de E/S durante la conversión.
     */
    public static BufferedImage convertBytesToImage(byte[] bytes) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        return ImageIO.read(bais);
    }

    /**
     * Gestiona la carga de un archivo de imagen y establece la imagen seleccionada en un ImageView.
     *
     * @param mouseEvent El evento de ratón que desencadenó la carga.
     * @param imgPhoto El ImageView en el que se establecerá la imagen seleccionada.
     */
    public static void handleUploadImagen(MouseEvent mouseEvent, ImageView imgPhoto) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(((Node) mouseEvent.getSource()).getScene().getWindow());
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imgPhoto.setImage(image);
            } catch (Exception e) {
                LOGGER.severe("Error al cargar la imagen: " + e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error al cargar la imagen");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }
}