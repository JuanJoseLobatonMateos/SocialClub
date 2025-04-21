package org.socialclub.socialclub.controller;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.capture.DPFPCapture;
import com.digitalpersona.onetouch.capture.event.DPFPDataAdapter;
import com.digitalpersona.onetouch.capture.event.DPFPDataEvent;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import com.digitalpersona.onetouch.verification.DPFPVerification;
import com.digitalpersona.onetouch.verification.DPFPVerificationResult;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXButton;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.database.RegistroEntradaDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.RegistroEntrada;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.ImageUtils;
import org.socialclub.socialclub.util.SalidaAutomaticaScheduler;
import org.socialclub.socialclub.util.SessionManager;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

/**
 * Controlador principal para la aplicación del club social.
 * Esta clase maneja la interfaz de usuario y las interacciones del usuario con la aplicación.
 * Incluye funcionalidades para la captura de huellas dactilares, verificación de empleados,
 * y registro de entradas y salidas de socios.
 */
public class MainController {

    private static final Logger LOGGER = Logger.getLogger(MainController.class.getName());

    @FXML
    public ImageView imgUser;
    @FXML
    public MFXButton btnLogout;
    @FXML
    public VBox verticalBox;
    @FXML
    public MFXIconButton helpIcon;
    @FXML
    private MFXIconButton btnDashboard;
    @FXML
    private MFXIconButton btnSocios;
    @FXML
    public MFXIconButton btnRegistro;
    @FXML
    private MFXIconButton btnReservas;
    @FXML
    private MFXIconButton btnMensajes;
    @FXML
    private MFXIconButton btnSettings;
    @FXML
    private AnchorPane pane;

    private final DPFPVerification verificator = DPFPGlobal.getVerificationFactory().createVerification();
    private boolean huellaControllerLanzado = false;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private static final String DB_URL = "jdbc:mysql://clubsocial.zapto.org/clubsocial";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";
    private final Object lock = new Object();

    /**
     * Inicializa el controlador.
     */
    @FXML
    private void initialize() {
        SalidaAutomaticaScheduler scheduler = new SalidaAutomaticaScheduler();
        scheduler.iniciar();
        configurarIconos();
        verificarEmpleadoLogueado();
        handleDashboardAction();
        iniciarLecturaContinuaHuella();
    }

    /**
     * Verifica el empleado logueado y muestra su imagen.
     */
    private void verificarEmpleadoLogueado() {
        try {
            Integer authenticatedEmployeeId = SessionManager.getInstance().getAuthenticatedEmployeeId();
            if (authenticatedEmployeeId != null) {
                EmpleadoDAO empleadoDAO = new EmpleadoDAO();
                Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(authenticatedEmployeeId);
                if (empleado.getRol().getId() == 2) {
                    Blob imagenBlob = new SerialBlob(empleado.getFoto());
                    String nombre = empleado.getNombre();
                    String apellidos = empleado.getApellidos();
                    mostrarImagenUsuario(imagenBlob, nombre, apellidos);
                }
                btnSettings.setVisible(empleado.getRol().getId() == 1);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al verificar el empleado logueado", e);
        }
    }

    /**
     * Muestra la imagen del usuario en la interfaz.
     *
     * @param imagenBlob Blob que contiene la imagen del usuario.
     */
    private void mostrarImagenUsuario(Blob imagenBlob, String nombre, String apellidos) {
        try {
            if (imagenBlob != null) {
                Image image = ImageUtils.convertBlobToFXImage(imagenBlob);
                imgUser.setImage(image);
                imgUser.setPreserveRatio(true);

                SnapshotParameters parameters = new SnapshotParameters();
                parameters.setFill(Color.TRANSPARENT);
                WritableImage roundedImage = imgUser.snapshot(parameters, null);

                imgUser.setClip(null);
                imgUser.setEffect(new DropShadow(10, Color.BLACK));
                imgUser.setImage(roundedImage);

                // Crear y configurar el Label
                Label nombreLabel = new Label(nombre + " " + apellidos);
                nombreLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
                nombreLabel.setAlignment(Pos.CENTER);
                nombreLabel.setMaxWidth(Double.MAX_VALUE);

                // Añadir los componentes al VBox y configurar el crecimiento vertical
                verticalBox.getChildren().clear();
                verticalBox.getChildren().addAll(imgUser, nombreLabel, btnDashboard, btnSocios, btnRegistro, btnReservas, btnMensajes, helpIcon, btnLogout);

                VBox.setVgrow(imgUser, Priority.ALWAYS);
                VBox.setVgrow(nombreLabel, Priority.ALWAYS);
                VBox.setVgrow(btnDashboard, Priority.ALWAYS);
                VBox.setVgrow(btnSocios, Priority.ALWAYS);
                VBox.setVgrow(btnRegistro, Priority.ALWAYS);
                VBox.setVgrow(btnReservas, Priority.ALWAYS);
                VBox.setVgrow(btnMensajes, Priority.ALWAYS);
                VBox.setVgrow(helpIcon, Priority.ALWAYS);
                VBox.setVgrow(btnLogout, Priority.ALWAYS);

                // Ajustar márgenes
                VBox.setMargin(imgUser, new Insets(5, 0, 0, 0));
                VBox.setMargin(nombreLabel, new Insets(0, 0, 0, 0));
                VBox.setMargin(btnDashboard, new Insets(8, 0, 0, 0));
                VBox.setMargin(btnSocios, new Insets(8, 0, 0, 0));
                VBox.setMargin(btnRegistro, new Insets(8, 0, 0, 0));
                VBox.setMargin(btnReservas, new Insets(8, 0, 0, 0));
                VBox.setMargin(btnMensajes, new Insets(8, 0, 0, 0));
                VBox.setMargin(helpIcon, new Insets(8, 0, 0, 0));
                VBox.setMargin(btnLogout, new Insets(8, 0, 0, 0));
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al mostrar la imagen del usuario", e);
        }
    }

    /**
     * Configura los iconos para los botones de la interfaz de usuario.
     *
     * <p>Esta función establece el icono y la información sobre herramientas (tooltip) para cada botón según los códigos de iconos de FontAwesome proporcionados.
     * Los iconos y la información sobre herramientas se establecen para los siguientes botones:
     * - btnDashboard
     * - btnSocios
     * - btnReservas
     * - btnMensajes
     * - btnSettings
     * - btnRegistro
     * - btnLogout
     * - helpIcon
     */
    private void configurarIconos() {
        btnDashboard.setIcon(new MFXFontIcon("fas-house"));
        btnDashboard.setTooltip(new Tooltip("Dashboard"));

        btnSocios.setIcon(new MFXFontIcon("fas-users"));
        btnSocios.setTooltip(new Tooltip("Socios"));

        btnReservas.setIcon(new MFXFontIcon("fas-calendar-days"));
        btnReservas.setTooltip(new Tooltip("Reservas"));

        btnMensajes.setIcon(new MFXFontIcon("fas-masks-theater"));
        btnMensajes.setTooltip(new Tooltip("Eventos"));

        btnSettings.setIcon(new MFXFontIcon("fas-gear"));
        btnSettings.setTooltip(new Tooltip("Configuración"));

        btnRegistro.setIcon(new MFXFontIcon("fas-book"));
        btnRegistro.setTooltip(new Tooltip("Registro"));

        MFXFontIcon icon = new MFXFontIcon("fas-arrow-right-from-bracket", 24, Color.WHITE);
        btnLogout.setGraphic(new MFXIconWrapper(icon, 24));
        btnLogout.setTooltip(new Tooltip("Cerrar sesión"));

        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Maneja la acción del botón Dashboard.
     */
    @FXML
    private void handleDashboardAction() {
        iniciarLecturaContinuaHuella();
        cargarVistaEnPanel("/View/dashboard.fxml");
    }

    /**
     * Maneja la acción del botón Socios.
     */
    @FXML
    private void handleSociosAction() {
        iniciarLecturaContinuaHuella();
        cargarVistaEnPanel("/View/socios.fxml");
    }

    /**
     * Maneja la acción del botón Reservas.
     */
    @FXML
    private void handleReservasAction() {
        iniciarLecturaContinuaHuella();
        cargarVistaEnPanel("/View/reservas.fxml");
    }

    /**
     * Maneja la acción del botón Eventos.
     */
    @FXML
    private void handleEventosAction() {
        iniciarLecturaContinuaHuella();
        cargarVistaEnPanel("/View/eventos.fxml");
    }

    /**
     * Maneja la acción del botón Configuración.
     */
    @FXML
    private void handleSettingsAction() {
        detenerLecturaContinuaHuella();
        cargarVistaEnPanel("/View/settings.fxml");
    }

    /**
     * Maneja la acción del botón Registro.
     */
    @FXML
    private void handleRegistroAction() {
        iniciarLecturaContinuaHuella();
        cargarVistaEnPanel("/View/registro.fxml");
    }

    /**
     * Maneja la acción del botón Logout.
     *
     * @param event El evento de clic del ratón.
     */
    @FXML
    private void handleLogoutAction(MouseEvent event) {

        Stage primaryStage = (Stage) ((MFXButton) event.getSource()).getScene().getWindow();
        DialogoController.showConfirmDialog(primaryStage, "¿Estas seguro?", event1 -> {
            try {
                onClose();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/ventanaPrincipal.fxml"));
                Parent root = loader.load();
                primaryStage.getScene().setRoot(root);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error al cargar la vista de ventana principal", e);
            }
        });
    }

    /**
     * Carga una vista en el panel principal.
     *
     * @param rutaFXML Ruta del archivo FXML a cargar.
     */
    private void cargarVistaEnPanel(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent root = loader.load();
            pane.getChildren().setAll(root);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, String.format("Error al cargar la vista: %s", rutaFXML), e);
        }
    }

    /**
     * Inicia la lectura continua de huellas.
     */
private void iniciarLecturaContinuaHuella() {
        if (huellaControllerLanzado) {
            LOGGER.info("El HuellaController ya se ha lanzado. No se iniciará la lectura continua de huellas.");
            return;
        }

        if (executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }

        huellaControllerLanzado = true;
        executorService.submit(() -> {
            while (huellaControllerLanzado) {
                try {
                    DPFPSample sample = capturarHuella();
                    if (sample != null) {
                        Platform.runLater(() -> registrarEntradaSalida(sample));
                    }
                    synchronized (lock) {
                        try {
                            lock.wait(1000);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            LOGGER.log(Level.INFO, "Lectura continua de huella interrumpida de forma controlada");
                            break;  // Salir del bucle cuando se interrumpe
                        }
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error en la lectura continua de huella", e);
                }
            }
            LOGGER.info("Hilo de lectura de huella finalizado");
        });
    }
    /**
     * Detiene la lectura continua de huellas.
     */
    private void detenerLecturaContinuaHuella() {
        huellaControllerLanzado = false;
        synchronized (lock) {
            lock.notifyAll();  // Notificar a todos los hilos en espera
        }
        executorService.shutdownNow();
        LOGGER.info("Solicitud de detención de lectura de huella enviada");
    }

    /**
     * Captura una huella dactilar.
     *
     * @return La muestra de huella capturada.
     */
    private DPFPSample capturarHuella() {
        DPFPCapture capture = null;
        final Object localLock = new Object();
        final DPFPSample[] sample = {null};

        try {
            capture = DPFPGlobal.getCaptureFactory().createCapture();

            capture.addDataListener(new DPFPDataAdapter() {
                @Override
                public void dataAcquired(DPFPDataEvent e) {
                    synchronized (localLock) {
                        sample[0] = e.getSample();
                        localLock.notifyAll();
                    }
                }
            });

            capture.startCapture();
            LOGGER.info("Capturando huella...");

            synchronized (localLock) {
                while (sample[0] == null) {
                    try {
                        localLock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        LOGGER.log(Level.SEVERE, "Captura de huella interrumpida");
                        return null;
                    }
                }
            }

            capture.stopCapture();
            LOGGER.info("Captura de huella completada.");
            return sample[0];
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error al capturar la huella dactilar", e);
        } finally {
            if (capture != null) {
                try {
                    capture.stopCapture();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error al detener la captura de huella", e);
                }
            }
        }
        return null;
    }

    /**
     * Extrae las características de una muestra de huella.
     *
     * @param sample La muestra de huella.
     * @return El conjunto de características extraídas.
     */
    private DPFPFeatureSet extractFeatures(DPFPSample sample) {
        try {
            return DPFPGlobal.getFeatureExtractionFactory().createFeatureExtraction().createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_VERIFICATION);
        } catch (DPFPImageQualityException e) {
            LOGGER.log(Level.SEVERE, "Error al extraer características de la huella", e);
            return null;
        }
    }

    /**
     * Registra la entrada o salida de un socio utilizando su huella dactilar.
     *
     * @param sample La muestra de huella capturada.
     */
    private void registrarEntradaSalida(DPFPSample sample) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            DPFPFeatureSet features = extractFeatures(sample);
            if (features == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se pudieron extraer características de la huella capturada.");
                return;
            }

            String query = "SELECT numero_socio, nombre, apellidos, huella FROM socio";
            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    if (verificarHuella(resultSet, features)) {
                        procesarRegistroEntradaSalida(resultSet);
                        return;
                    }
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "No Encontrado", "No se encontró ningún socio con la huella proporcionada.");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al registrar la entrada/salida: " + e.getMessage());
            LOGGER.log(Level.SEVERE, "Error al registrar la entrada/salida", e);
        }
    }

    /**
     * Verifica la huella dactilar contra la base de datos.
     *
     * @param resultSet El conjunto de resultados de la consulta.
     * @param features  El conjunto de características extraídas.
     * @return true si la huella es verificada, false en caso contrario.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    private boolean verificarHuella(ResultSet resultSet, DPFPFeatureSet features) throws SQLException {
        byte[] huellaGuardada = resultSet.getBytes("huella");
        if (huellaGuardada != null) {
            DPFPTemplate templateGuardada = DPFPGlobal.getTemplateFactory().createTemplate(huellaGuardada);
            DPFPVerificationResult result = verificator.verify(features, templateGuardada);
            return result.isVerified();
        }
        return false;
    }

    /**
     * Procesa el registro de entrada o salida de un socio.
     *
     * @param resultSet El conjunto de resultados de la consulta.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    private void procesarRegistroEntradaSalida(ResultSet resultSet) throws SQLException {
        String idSocio = resultSet.getString("numero_socio");
        String nombreSocio = resultSet.getString("nombre");
        String apellidosSocio = resultSet.getString("apellidos");
        LocalDate fechaActual = LocalDate.now();
        LocalTime horaActual = LocalTime.now();

        try (Session session = getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            try {
                Socio socio = session.get(Socio.class, idSocio);
                RegistroEntradaDAO registroEntradaDAO = new RegistroEntradaDAO();
                RegistroEntrada registroEntrada = registroEntradaDAO.obtenerRegistroEntradaSinSalida(socio);

                if (registroEntrada == null) {
                    crearRegistroEntrada(registroEntradaDAO, socio, fechaActual, horaActual);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", String.format("Socio: %s - %s %s ha registrado su entrada a las %s.", idSocio, nombreSocio, apellidosSocio, horaActual));
                } else {
                    actualizarRegistroSalida(registroEntradaDAO, registroEntrada, horaActual);
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", String.format("Socio: %s - %s %s ha registrado su salida a las %s.", idSocio, nombreSocio, apellidosSocio, horaActual));
                }

                transaction.commit();
            } catch (Exception e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                throw e;
            }
        }
    }

    /**
     * Crea un registro de entrada para un socio.
     *
     * @param registroEntradaDAO El DAO de registro de entrada.
     * @param socio              El socio.
     * @param fechaActual        La fecha actual.
     * @param horaActual         La hora actual.
     */
    private void crearRegistroEntrada(RegistroEntradaDAO registroEntradaDAO, Socio socio, LocalDate fechaActual, LocalTime horaActual) {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setFecha(fechaActual);
        registroEntrada.setNumeroSocio(socio);
        registroEntrada.setHoraEntrada(horaActual);
        registroEntrada.setHoraSalida(null);
        registroEntradaDAO.crearRegistroEntrada(registroEntrada);
    }

    /**
     * Actualiza un registro de salida para un socio.
     *
     * @param registroEntradaDAO El DAO de registro de entrada.
     * @param registroEntrada    El registro de entrada.
     * @param horaActual         La hora actual.
     */
    private void actualizarRegistroSalida(RegistroEntradaDAO registroEntradaDAO, RegistroEntrada registroEntrada, LocalTime horaActual) {
        registroEntrada.setHoraSalida(horaActual);
        registroEntradaDAO.actualizarRegistroEntrada(registroEntrada);
    }

    /**
     * Muestra una alerta en la interfaz.
     *
     * @param tipo    El tipo de alerta.
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> crearYMostrarAlerta(tipo, titulo, mensaje));
    }

    /**
     * Crea y muestra una alerta en la interfaz.
     *
     * @param tipo    El tipo de alerta.
     * @param titulo  El título de la alerta.
     * @param mensaje El mensaje de la alerta.
     */
    private void crearYMostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), event -> alerta.close()));
        timeline.setCycleCount(1);
        timeline.play();
    }

    /**
     * Maneja el evento de clic en el icono de ayuda.
     *
     * @param event El evento de ratón.
     */
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_main.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Navegación");
            // Seleccionar icono
            helpStage.getIcons().add(new Image("/images/logo.png"));
            helpStage.setScene(new Scene(helpRoot));

            // Configurar la ventana como modal y siempre en primer plano
            helpStage.initModality(Modality.APPLICATION_MODAL);
            helpStage.initOwner((((MFXIconButton) event.getSource()).getScene().getWindow()));
            helpStage.setAlwaysOnTop(true);
            helpStage.setResizable(false);

            helpStage.showAndWait();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error al cargar la ventana de ayuda", e);
        }
    }

    /**
     * Cierra correctamente los recursos utilizados por el controlador.
     *
     * <p>Este método se encarga de realizar una limpieza ordenada cuando la aplicación se cierra,
     * asegurando que todos los recursos se liberen adecuadamente:</p>
     * <ul>
     *   <li>Detiene el proceso de lectura continua de huella dactilar</li>
     *   <li>Intenta finalizar ordenadamente el servicio de ejecución ({@code ExecutorService})</li>
     *   <li>Si la terminación no es posible en el tiempo establecido (2 segundos), registra una advertencia</li>
     *   <li>Maneja adecuadamente la posible interrupción durante el proceso de espera</li>
     * </ul>
     *
     * <p>Este método debe ser llamado antes de cerrar la ventana principal
     * o cuando el usuario cierra sesión para evitar fugas de memoria
     * y asegurar que todos los hilos de ejecución se detengan correctamente.</p>
     */
    public void onClose() {
        detenerLecturaContinuaHuella();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
            try {
                if (!executorService.awaitTermination(2, TimeUnit.SECONDS)) {
                    LOGGER.warning("No se pudo terminar el executor service correctamente");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Interrupción durante la espera de terminación del executor", e);
            }
        }
    }
}