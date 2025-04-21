package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.socialclub.socialclub.database.RegistroEntradaDAO;
import org.socialclub.socialclub.database.ReservaDAO;
import org.socialclub.socialclub.model.RegistroEntrada;
import org.socialclub.socialclub.model.Reserva;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.WeatherService;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

import static javafx.animation.Animation.INDEFINITE;
import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para el panel de control del club social.
 * Gestiona la interfaz de usuario y las interacciones con la base de datos.
 */
public class DashboardController {
    @FXML
    public Label lblFecha;
    @FXML
    public Label lblHora;
    @FXML
    public PieChart aforo;
    @FXML
    public HBox climaPorHorasContainer;
    @FXML
    public Label lblClima;
    @FXML
    public MFXComboBox<String> cmbEntrada;
    @FXML
    public MFXComboBox<String> cmbSalida;
    @FXML
    public Button btnEntrada;
    @FXML
    public Button btnSalida;
    @FXML
    public Label lblEntradaSalida;
    @FXML
    public Pane paneReservas;
    @FXML
    public Pane paneEntrada;
    @FXML
    public ImageView imgEntrada;
    @FXML
    public Label lblNumeroSocioEntrada;
    @FXML
    public Label lblTipoCarnetEntrada;
    @FXML
    public Label lblPromocionEntrada;
    @FXML
    public Label lblNombreEntrada;
    @FXML
    public Label lblEntrada;
    @FXML
    public Pane paneSalida;
    @FXML
    public ImageView imgSalida;
    @FXML
    public Label lblNombreSalida;
    @FXML
    public Label lblNumeroSocioSalida;
    @FXML
    public Label lblTipoCarnetSalida;
    @FXML
    public Label lblPromocionSalida;
    @FXML
    public Label lblSalida;
    @FXML
    public ListView<Reserva> listReservas;
    @FXML
    public Label lblNoHayEntradas;
    @FXML
    public Label lblNoHaySalidas;
    @FXML
    public MFXIconButton helpIcon;
    private List<Socio> sociosDentroCache;
    private List<Socio> sociosFueraCache;
    private LocalDateTime ultimaActualizacionSocios = LocalDateTime.MIN;
    private static final Duration CACHE_DURATION = Duration.seconds(10);
    private RegistroEntrada ultimaEntradaCache;
    private RegistroEntrada ultimaSalidaCache;
    private LocalDateTime ultimaActualizacionRegistros = LocalDateTime.MIN;

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.forLanguageTag(LOCALE_ES));
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.forLanguageTag(LOCALE_ES));
    private final RegistroEntradaDAO registroEntradaDAO = new RegistroEntradaDAO();
    private final WeatherService weatherService = new WeatherService();
    private static final int AFORO_MAXIMO = 225;
    private static final String LOCALE_ES = "es-ES";


    /**
     * Inicializa el controlador y configura los componentes de la interfaz de usuario.
     */
    @FXML
    public void initialize() {
        mostrarFechaActual();
        configurarHora();
        configurarAforo();
        weatherService.obtenerClima(climaPorHorasContainer, lblClima);
        inicializarComboBox();
        btnEntrada.setOnAction(event -> registrarEntrada());
        btnSalida.setOnAction(event -> registrarSalida());
        cargarReservasDeHoy();
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Carga las reservas del día actual y las muestra en la lista de reservas.
     */
    private void cargarReservasDeHoy() {
        ReservaDAO reservaDAO = new ReservaDAO();
        List<Reserva> reservasDeHoy = reservaDAO.obtenerReservasPorFecha(LocalDate.now());
        reservasDeHoy.sort(Comparator.comparing(Reserva::getHora));
        ObservableList<Reserva> items = FXCollections.observableArrayList(reservasDeHoy);
        listReservas.setItems(items);
        listReservas.setCellFactory(param -> new ListCell<>() {
            private final ImageView imageView = new ImageView();
            private final Label label = new Label();

            @Override
            protected void updateItem(Reserva reserva, boolean empty) {
                super.updateItem(reserva, empty);
                if (empty || reserva == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String imagePath = obtenerImagenPorTipoReserva(reserva.getIdInstalacion().getTipo());
                    imageView.setImage(new Image(imagePath));
                    imageView.setFitHeight(50);
                    imageView.setFitWidth(50);
                    label.setText(String.format("%s%nHora: %s%nSocio nº: %s", reserva.getIdInstalacion().getNombre(), reserva.getHora(), reserva.getNumeroSocio().getNumeroSocio()));
                    label.setStyle("-fx-text-fill: black; -fx-font-size: 14px;-fx-padding: 0 0 0 10px;");
                    setGraphic(new HBox(imageView, label));
                }
            }
        });
    }

    /**
     * Obtiene la ruta de la imagen correspondiente al tipo de reserva.
     *
     * @param nombreInstalacion El nombre de la instalación.
     * @return La ruta de la imagen correspondiente.
     */
    private String obtenerImagenPorTipoReserva(String nombreInstalacion) {
        return switch (nombreInstalacion.toLowerCase()) {
            case "pista de padel" -> "/images/padel.jpg";
            case "deportiva" -> "/images/futbol.jpg";
            case "recreativa" -> "/images/salon.jpg";
            default -> "/images/nohuella.jpg";
        };
    }

    /**
     * Registra la entrada de un socio.
     */
    private void registrarEntrada() {
        String socioInfo = cmbEntrada.getSelectionModel().getSelectedItem();
        if (socioInfo != null) {
            String[] parts = socioInfo.split(" - ");
            if (parts.length > 0) {
                try {
                    String numeroSocio = parts[0].trim();
                    Socio socio = new Socio();
                    socio.setNumeroSocio(numeroSocio);
                    RegistroEntrada registroEntrada = new RegistroEntrada();
                    registroEntrada.setNumeroSocio(socio);
                    registroEntrada.setFecha(LocalDate.now());
                    registroEntrada.setHoraEntrada(LocalTime.now());
                    DialogoController.showConfirmDialog((Stage) lblClima.getScene().getWindow(), "El socio: %s va a entrar ¿Está seguro?".formatted(socioInfo), e -> {
                        registroEntradaDAO.crearRegistroEntrada(registroEntrada);
                        actualizarAforo();
                        cmbEntrada.getItems().clear();
                        cmbSalida.getItems().clear();
                        inicializarComboBox();
                    });
                } catch (NumberFormatException e) {
                    LOGGER.severe("Error al registrar la entrada");
                }
            }
        }
    }

    /**
     * Registra la salida de un socio.
     */
    private void registrarSalida() {
        String socioInfo = cmbSalida.getSelectionModel().getSelectedItem();
        if (socioInfo != null) {
            String numeroSocio = obtenerNumeroSocio(socioInfo);
            if (numeroSocio != null) {
                procesarSalidaSocio(numeroSocio, socioInfo);
            }
        }
    }

    /**
     * Obtiene el número de socio a partir de la información del socio.
     *
     * @param socioInfo La información del socio.
     * @return El número de socio.
     */
    private String obtenerNumeroSocio(String socioInfo) {
        String[] parts = socioInfo.split(" - ");
        return parts.length > 0 ? parts[0].trim() : null;
    }

    /**
     * Procesa la salida de un socio.
     *
     * @param numeroSocio El número de socio.
     * @param socioInfo   La información del socio.
     */
    private void procesarSalidaSocio(String numeroSocio, String socioInfo) {
        Socio socio = registroEntradaDAO.obtenerSocioPorNumero(numeroSocio);
        if (socio != null) {
            RegistroEntrada registroEntrada = registroEntradaDAO.obtenerRegistroEntradaSinSalida(socio);
            if (registroEntrada != null) {
                registrarHoraSalida(registroEntrada, socioInfo);
            } else {
                logWarning("No se encontró un registro de entrada sin salida para el socio: %s", socioInfo);
            }
        } else {
            logWarning("No se encontró el socio con número: %s", numeroSocio);
        }
    }

    /**
     * Registra la hora de salida de un socio.
     *
     * @param registroEntrada El registro de entrada del socio.
     * @param socioInfo       La información del socio.
     */
    private void registrarHoraSalida(RegistroEntrada registroEntrada, String socioInfo) {
        registroEntrada.setHoraSalida(LocalTime.now());
        String mensajeConfirmacion = String.format("El socio: %s va a salir ¿Está seguro?", socioInfo);
        DialogoController.showConfirmDialog((Stage) lblClima.getScene().getWindow(), mensajeConfirmacion, e -> {
            registroEntradaDAO.actualizarRegistroEntrada(registroEntrada);
            actualizarAforo();
            limpiarYActualizarComboBox();
        });
    }

    /**
     * Registra un mensaje de advertencia en el log.
     *
     * @param mensaje   El mensaje de advertencia.
     * @param parametro El parámetro del mensaje.
     */
    private void logWarning(String mensaje, String parametro) {
        if (LOGGER.isLoggable(Level.WARNING)) {
            LOGGER.warning(String.format(mensaje, parametro));
        }
    }

    /**
     * Limpia y actualiza los ComboBox de entrada y salida.
     */
    private void limpiarYActualizarComboBox() {
        cmbEntrada.getItems().clear();
        cmbSalida.getItems().clear();
        inicializarComboBox();
    }

    /**
     * Inicializa los ComboBox de entrada y salida con la información de los socios.
     */


    private void inicializarComboBox() {
        LocalDateTime ahora = LocalDateTime.now();

        // Refrescar el caché si ha pasado el tiempo establecido
        if (ChronoUnit.SECONDS.between(ultimaActualizacionSocios, ahora) > CACHE_DURATION.toSeconds()) {
            sociosDentroCache = registroEntradaDAO.obtenerSociosDentro();
            sociosFueraCache = registroEntradaDAO.obtenerSociosFuera();
            ultimaActualizacionSocios = ahora;
        }

        // Usar datos en caché
        for (Socio socio : sociosFueraCache) {
            String socioInfo = socio.getNumeroSocio() + " - " + socio.getNombre() + " " + socio.getApellidos();
            cmbEntrada.getItems().add(socioInfo);
        }

        for (Socio socio : sociosDentroCache) {
            String socioInfo = socio.getNumeroSocio() + " - " + socio.getNombre() + " " + socio.getApellidos();
            cmbSalida.getItems().add(socioInfo);
        }
    }

    /**
     * Muestra la fecha actual en el label correspondiente.
     */
    private void mostrarFechaActual() {
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag(LOCALE_ES));
        String fechaFormateada = fechaActual.format(formatter);
        lblFecha.setText(fechaFormateada);
    }

    /**
     * Configura la actualización de la hora en el label correspondiente.
     */
    private void configurarHora() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0), event -> actualizarHora()), new KeyFrame(Duration.seconds(1)));
        timeline.setCycleCount(INDEFINITE);
        timeline.play();
    }

    /**
     * Actualiza la hora en el label correspondiente.
     */
    private void actualizarHora() {
            LocalDateTime ahora = LocalDateTime.now();
            String horaFormateada = ahora.format(timeFormatter);
            lblHora.setText(horaFormateada);

            // Limitar actualizaciones de datos pesados a intervalos específicos
            if (ahora.getSecond() % 10 == 0) { // Actualizar cada 10 segundos en lugar de cada segundo
                actualizarDatosAforo();
            }

            // Actualizar entrada/salida cada 5 segundos
            if (ahora.getSecond() % 5 == 0) {
                actualizarPanelesEntradaSalida();
            }
        }

        private void actualizarDatosAforo() {
            actualizarAforo();
            int sociosDentroActual = registroEntradaDAO.obtenerTotalSociosDentro();
            int sociosFueraActual = registroEntradaDAO.obtenerTotalSocios() - sociosDentroActual;

            // Solo actualizar ComboBox si los datos han cambiado
            if (cmbEntrada.getItems().size() != sociosFueraActual || cmbSalida.getItems().size() != sociosDentroActual) {
                cmbEntrada.getItems().clear();
                cmbSalida.getItems().clear();
                inicializarComboBox();
            }
        }

        private void actualizarPanelesEntradaSalida() {
            actualizarUltimaEntrada();
            actualizarUltimaSalida();
        }

    /**
     * Actualiza la información de la última entrada en el panel correspondiente.
     */
    private void actualizarUltimaEntrada() {
        LocalDateTime ahora = LocalDateTime.now();

        // Actualizar caché si ha pasado el tiempo o es nulo
        if (ultimaEntradaCache == null ||
                ChronoUnit.SECONDS.between(ultimaActualizacionRegistros, ahora) > CACHE_DURATION.toSeconds()) {
            ultimaEntradaCache = registroEntradaDAO.obtenerUltimaEntrada();
            ultimaActualizacionRegistros = ahora;
        }

        if (ultimaEntradaCache != null) {
            Socio socio = ultimaEntradaCache.getNumeroSocio();
            String fechaEntradaFormateada = ultimaEntradaCache.getFecha().format(dateFormatter);
            String horaEntradaFormateada = ultimaEntradaCache.getHoraEntrada().format(timeFormatter);

            lblEntrada.setText("Última Entrada: " + fechaEntradaFormateada + " " + horaEntradaFormateada);
            lblNombreEntrada.setText(socio.getNombre() + " " + socio.getApellidos());
            lblNumeroSocioEntrada.setText(socio.getNumeroSocio());
            lblTipoCarnetEntrada.setText(obtenerTipoCarnet(calcularEdad(socio.getFechaNacimiento())));
            lblPromocionEntrada.setText(obtenerPromocion());
            imgEntrada.setImage(obtenerImagenSocio(socio));

            paneEntrada.setVisible(true);
            lblNoHayEntradas.setVisible(false);
        } else {
            paneEntrada.setVisible(false);
            lblNoHayEntradas.setVisible(true);
        }
    }
    /**
     * Actualiza la información de la última salida en el panel correspondiente.
     */
private void actualizarUltimaSalida() {
    LocalDateTime ahora = LocalDateTime.now();

    // Actualizar caché si ha pasado el tiempo o es nulo
    if (ultimaSalidaCache == null ||
            ChronoUnit.SECONDS.between(ultimaActualizacionRegistros, ahora) > CACHE_DURATION.toSeconds()) {
        ultimaSalidaCache = registroEntradaDAO.obtenerUltimaSalida();
        // No actualizamos el timestamp aquí porque ya se hace en actualizarUltimaEntrada()
    }

    if (ultimaSalidaCache != null) {
        Socio socio = ultimaSalidaCache.getNumeroSocio();
        String fechaSalidaFormateada = ultimaSalidaCache.getFecha().format(dateFormatter);
        String horaSalidaFormateada = ultimaSalidaCache.getHoraSalida().format(timeFormatter);

        lblSalida.setText("Última Salida: " + fechaSalidaFormateada + " " + horaSalidaFormateada);
        lblNombreSalida.setText(socio.getNombre() + " " + socio.getApellidos());
        lblNumeroSocioSalida.setText(socio.getNumeroSocio());
        lblTipoCarnetSalida.setText(obtenerTipoCarnet(calcularEdad(socio.getFechaNacimiento())));
        lblPromocionSalida.setText(obtenerPromocion());
        imgSalida.setImage(obtenerImagenSocio(socio));

        paneSalida.setVisible(true);
        lblNoHaySalidas.setVisible(false);
    } else {
        paneSalida.setVisible(false);
        lblNoHaySalidas.setVisible(true);
    }
}

    /**
     * Obtiene la promoción actual.
     *
     * @return La promoción actual.
     */
    private String obtenerPromocion() {
        int year = LocalDate.now().getYear();
        return year + "/" + (year + 1);
    }

    /**
     * Calcula la edad de un socio a partir de su fecha de nacimiento.
     *
     * @param fechaNacimiento La fecha de nacimiento del socio.
     * @return La edad del socio.
     */
    private int calcularEdad(LocalDate fechaNacimiento) {
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    /**
     * Obtiene el tipo de carnet de un socio en función de su edad.
     *
     * @param edad La edad del socio.
     * @return El tipo de carnet.
     */
    private String obtenerTipoCarnet(int edad) {
        if (edad < 12) {
            return "Infantil";
        } else if (edad < 18) {
            return "Juvenil";
        } else {
            return "Adulto";
        }
    }

    /**
     * Obtiene la imagen de un socio.
     *
     * @param socio El socio.
     * @return La imagen del socio.
     */
    private Image obtenerImagenSocio(Socio socio) {
        try {
            BufferedImage imagenBuffered = socio.getFotoImagen();
            return SwingFXUtils.toFXImage(imagenBuffered, null);
        } catch (Exception e) {
            LOGGER.severe("Error al obtener la imagen del socio: " + e.getMessage());
            return new Image("resources/images/noimage.jpg");
        }
    }

    /**
     * Configura el gráfico de aforo.
     */
    private void configurarAforo() {
        int sociosDentro = registroEntradaDAO.obtenerTotalSociosDentro();
        PieChart.Data aforoDisponibleData = new PieChart.Data("Aforo Máximo: " + AFORO_MAXIMO, AFORO_MAXIMO);
        PieChart.Data aforoActual = new PieChart.Data("Aforo Actual: " + sociosDentro, sociosDentro);
        aforo.getData().addAll(aforoActual, aforoDisponibleData);
    }

    /**
     * Actualiza el gráfico de aforo.
     */
    public void actualizarAforo() {
        aforo.getData().clear();
        configurarAforo();
    }

    /**
     * Maneja el evento de clic en el ícono de ayuda.
     *
     * @param event El evento del mouse.
     */
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            // Cargar el archivo FXML de la vista de ayuda
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_dashboard.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Dashboard");
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