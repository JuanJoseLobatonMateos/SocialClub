package org.socialclub.socialclub.util;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.socialclub.socialclub.model.WeatherData;
import org.socialclub.socialclub.model.WeatherForecast;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Esta clase proporciona métodos para recuperar y mostrar información meteorológica para una ciudad determinada.
 */
public class WeatherService {
    private static final String API_KEY = "0062a76325df3ecaac52f2685c7dcfc9";
    private static final String CITY = "Jerez de la Frontera,ES";

    /**
     * Este método obtiene el pronóstico meteorológico para una ciudad determinada y lo muestra en un contenedor.
     * Utiliza una tarea en segundo plano para obtener los datos de la API de OpenWeatherMap y actualizar la interfaz de usuario con la información obtenida.
     *
     * @param climaPorHorasContainer El contenedor HBox donde se mostrarán los datos del clima por tramos.
     * @param lblClima               La etiqueta Label donde se mostrará el nombre de la ciudad.
     */
    public void obtenerClima(HBox climaPorHorasContainer, Label lblClima) {
        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String ciudadCodificada = URLEncoder.encode(CITY, StandardCharsets.UTF_8);
                String url = "https://api.openweathermap.org/data/2.5/forecast?q=" + ciudadCodificada + "&appid=" + API_KEY + "&units=metric&lang=es";

                try (HttpClient client = HttpClient.newHttpClient()) {
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    return response.body();
                }
            }
        };

        task.setOnSucceeded(event -> procesarRespuestaClima(task.getValue(), climaPorHorasContainer, lblClima));

        task.setOnFailed(event -> LOGGER.severe("Error al obtener el clima: " + task.getException().getMessage()));

        new Thread(task).start();
    }

    /**
     * Procesa la respuesta JSON del clima obtenida de la API de OpenWeatherMap y actualiza la interfaz de usuario.
     *
     * @param jsonResponse           La respuesta JSON del clima.
     * @param climaPorHorasContainer El contenedor HBox donde se mostrarán los datos del clima por tramos.
     * @param lblClima               La etiqueta Label donde se mostrará el nombre de la ciudad.
     */
    private void procesarRespuestaClima(String jsonResponse, HBox climaPorHorasContainer, Label lblClima) {
        Gson gson = new Gson();
        WeatherForecast forecast = gson.fromJson(jsonResponse, WeatherForecast.class);

        Platform.runLater(() -> {
            if (forecast != null && forecast.getList() != null) {
                climaPorHorasContainer.getChildren().clear(); // Limpiar el contenedor

                // Mostrar el nombre de la ciudad
                lblClima.setText(forecast.getCity().getName());

                // Filtrar los datos del clima por tramos
                List<WeatherData> tramosClima = filtrarTramosClima(forecast.getList());

                // Mostrar el clima por tramos
                for (WeatherData data : tramosClima) {
                    String hora = LocalDateTime.ofEpochSecond(data.getDt(), 0, ZoneOffset.UTC)
                            .format(DateTimeFormatter.ofPattern("HH:mm"));
                    String descripcion = data.getWeather()[0].getDescription();
                    double temperatura = data.getMain().getTemp();
                    String iconUrl = "https://openweathermap.org/img/wn/" + data.getWeather()[0].getIcon() + ".png";

                    // Crear un componente para mostrar la hora, temperatura, descripción e ícono
                    VBox tramoBox = new VBox(5);
                    tramoBox.setAlignment(Pos.CENTER);
                    tramoBox.setMaxWidth(Double.MAX_VALUE); // Permitir que crezca

                    // Hora del tramo
                    Label lblHoraTramo = new Label(hora);
                    lblHoraTramo.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-font-weight: bold;");

                    // Ícono del clima
                    ImageView iconoClima = crearIconoClima(iconUrl);

                    // Descripción del clima
                    Label lblDescripcion = new Label(descripcion);
                    lblDescripcion.setStyle("-fx-font-size: 12px; -fx-text-fill: black; -fx-font-weight: bold;");

                    // Temperatura sin decimales
                    int temperaturaRedondeada = (int) Math.round(temperatura);
                    Label lblTemperatura = new Label(temperaturaRedondeada + "°C");
                    lblTemperatura.setStyle("-fx-font-size: 12px; -fx-text-fill: black;");

                    // Agregar elementos al VBox
                    tramoBox.getChildren().addAll(lblHoraTramo, iconoClima, lblDescripcion, lblTemperatura);

                    // Agregar el VBox al HBox principal
                    climaPorHorasContainer.getChildren().add(tramoBox);

                    // Permitir que el VBox ocupe el espacio disponible en el HBox
                    HBox.setHgrow(tramoBox, Priority.ALWAYS);
                }
            }
        });
    }

        /**
     * Crea un componente ImageView que muestra un ícono de clima basado en la URL proporcionada.
     * Ajusta el tamaño del ícono a 50x50 píxeles.
     *
     * @param iconUrl La URL del ícono de clima.
     * @return Un componente ImageView con el ícono de clima ajustado.
     */
    private ImageView crearIconoClima(String iconUrl) {
        Image image = new Image(iconUrl);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(50); // Ajusta el tamaño del ícono
        imageView.setFitHeight(50);
        return imageView;
    }

    /**
 * Filtra los datos del clima para obtener un subconjunto de datos que representan el clima en intervalos de tiempo específicos.
 *
 * @param datosClima La lista de datos del clima obtenidos de la API de OpenWeatherMap.
 * @return Una lista de objetos WeatherData que representan el clima en intervalos de tiempo específicos (0, 3, 6, 9, 12, 15, 18, 21 horas).
 */
private List<WeatherData> filtrarTramosClima(List<WeatherData> datosClima) {
    List<WeatherData> tramos = new ArrayList<>();

    // Definir los intervalos de tiempo para cada tramo
    int[] horasTramos = {0, 3, 6, 9, 12, 15, 18, 21};

    for (int hora : horasTramos) {
        for (WeatherData data : datosClima) {
            LocalDateTime fechaHora = LocalDateTime.ofEpochSecond(data.getDt(), 0, ZoneOffset.UTC);
            if (fechaHora.getHour() == hora) {
                tramos.add(data);
                break; // Solo tomar el primer dato que coincida con la hora
            }
        }
    }

    return tramos;
}
}