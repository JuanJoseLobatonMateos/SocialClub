package org.socialclub.socialclub.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Esta clase se utiliza para cargar y acceder a las propiedades de la aplicación desde un archivo de configuración.
 * El archivo de configuración se carga en tiempo de carga de clase y se almacena en una instancia de {@link Properties}.
 * La clase también contiene métodos de utilidad para obtener propiedades específicas de la aplicación.
 */
public class ConfigLoader {
    public static final Logger LOGGER = Logger.getLogger(ConfigLoader.class.getName());
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigLoader.class.getResourceAsStream("/config/config.properties")) {
            if (input == null) {
                LOGGER.severe("No se pudo encontrar el archivo de configuración: /config/config.properties");
            } else {
                properties.load(input);
            }
        } catch (IOException ex) {
            LOGGER.severe("Error al cargar el archivo de configuración: " + ex.getMessage());
        }
    }


    private ConfigLoader() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no debe ser instanciada.");
    }


    /**
     * Obtiene el valor de la propiedad con la clave dada.
     *
     * @param key La clave de la propiedad que se desea obtener.
     * @return El valor de la propiedad correspondiente a la clave dada, o {@code null} si la clave no existe.
     */
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }


    /**
     * Obtiene la ruta de la vista principal de la aplicación.
     * La ruta se obtiene de la propiedad "main.view.path" del archivo de configuración.
     *
     * @return La ruta de la vista principal de la aplicación.
     */
    public static String getMainViewPath() {
        return properties.getProperty("main.view.path");
    }


    /**
     * Obtiene el título de la ventana de la aplicación.
     * El título se obtiene de la propiedad "app.title" del archivo de configuración.
     *
     * @return El título de la ventana de la aplicación.
     */
    public static String getWindowTitle() {
        return properties.getProperty("app.title");
    }

}