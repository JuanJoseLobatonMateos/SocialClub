package org.socialclub.socialclub.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.socialclub.socialclub.util.ConfigLoader;

import java.io.IOException;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Clase principal de la aplicación JavaFX.
 * Esta clase extiende de Application y es el punto de entrada de la aplicación JavaFX.
 */
public class App extends Application {

    /**
     * Método principal que inicia la aplicación JavaFX.
     * Este método se llama cuando se ejecuta la aplicación.
     *
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        // Iniciar la aplicación JavaFX
        launch(args);


    }

    /**
     * Método de inicio de la aplicación JavaFX.
     * Este método se llama cuando la aplicación JavaFX se inicia.
     *
     * @param primaryStage La ventana principal de la aplicación.
     */
    @Override
    public void start(Stage primaryStage) {
        try {

            // Cargar el archivo FXML de la ventana principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ConfigLoader.getMainViewPath()));
            Parent root = loader.load();
            // Crear la escena con el contenido cargado
            Scene scene = new Scene(root);
            // Establecer el color de fondo transparente
            scene.setFill(Color.TRANSPARENT);
            // Configurar la ventana principal
            primaryStage.setScene(scene);
            primaryStage.setTitle(ConfigLoader.getWindowTitle()); // Establecer el título de la ventana
            primaryStage.initStyle(StageStyle.TRANSPARENT); // Estilo de ventana transparente
            primaryStage.setResizable(false); // No permitir redimensionar la ventana
            // Mostrar la ventana principal
            primaryStage.show();
        } catch (IOException e) {
            // Registrar el error si ocurre una excepción al cargar la ventana principal
            LOGGER.severe("Error al cargar la ventana principal: " + e.getMessage());
        }
    }
}