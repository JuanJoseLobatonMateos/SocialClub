package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.utils.SwingFXUtils;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.BigDecimalStringConverter;
import javafx.util.converter.IntegerStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.database.InstalacionDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.Instalacion;
import org.socialclub.socialclub.util.ConfigLoader;
import org.socialclub.socialclub.util.SessionManager;
import org.socialclub.socialclub.util.TooltipUtil;
import org.socialclub.socialclub.util.Validaciones;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Esta clase administra la funcionalidad para agregar nuevas instalaciones al sistema.
 * Incluye métodos para inicializar campos, validar la entrada del usuario, guardar nuevas instalaciones,
 * actualizar instalaciones existentes y eliminar instalaciones.
 */
public class AddInstalacionController {

    @FXML
    public Label lblNombreError;
    @FXML
    public Label lblCapacidadError;
    @FXML
    public Label lblPrecioError;
    @FXML
    public MFXIconButton helpIcon;

    @FXML
    private MFXComboBox<String> cmbDisponibilidad;

    @FXML
    private MFXComboBox<String> cmbTipo;

    @FXML
    private MFXTextField txtNombre;

    @FXML
    private MFXTextField txtCapacidad;

    @FXML
    private MFXTextField txtPrecio;

    @FXML
    private Spinner<Integer> spiDuracion;

    @FXML
    private Spinner<LocalTime> spiHoraApertura;

    @FXML
    private Spinner<LocalTime> spiHoraCierre;

    @FXML
    private ImageView imgPhoto;

    @FXML
    private MFXButton btnGuardar;

    @FXML
    private TableView<Instalacion> tabla;

    @FXML
    private TableColumn<Instalacion, Integer> colId;

    @FXML
    private TableColumn<Instalacion, String> colTipo;

    @FXML
    private TableColumn<Instalacion, String> colNombre;

    @FXML
    private TableColumn<Instalacion, Integer> colCapacidad;

    @FXML
    private TableColumn<Instalacion, Integer> colDuracion;

    @FXML
    private TableColumn<Instalacion, BigDecimal> colAlquiler;

    @FXML
    private TableColumn<Instalacion, LocalTime> colHoraApertura;

    @FXML
    private TableColumn<Instalacion, LocalTime> colHoraCierre;

    @FXML
    private TableColumn<Instalacion, Byte> colDisponibilidad;

    ContextMenu contextMenu;
    private static final String DISPONIBLE = "Disponible";
    private static final String NO_DISPONIBLE = "No Disponible";
    private static final String CAPACIDAD = "capacidad";
    private static final String DURACION = "duracion";
    private static final String PRECIO_ALQUILER = "precioAlquiler";
    private static final String HORA_INI = "horaIni";
    private static final String HORA_FIN = "horaFin";
    private static final String DISPONIBILIDAD = "disponibilidad";
    private static final Logger logger = LoggerFactory.getLogger(AddInstalacionController.class);

    private static final String[] TIPOS_INSTALACION = {"Pista de Padel", "Deportiva", "Recreativa"};
    private static final String[] OPCION_DISPONIBILIDAD = {DISPONIBLE, NO_DISPONIBLE};

    private static final String PATH_IMAGE = ConfigLoader.getProperty("image.base.path");

    /**
     * Inicializa los componentes de la interfaz gráfica y configura los eventos necesarios.
     * Este método se llama automáticamente después de que se cargue el archivo FXML.
     */
    @FXML
    private void initialize() {
        inicializarCampos();
        configurarTablaInstalacion();
        cargarDatosTabla();
        btnGuardar.setOnAction(e -> {
            try {
                guardarInstalacion();
            } catch (Exception ex) {
                logger.error("Error al guardar la instalación", ex);
            }
        });
        configurarContextMenu();
        TooltipUtil.setupTooltip(spiDuracion, "Seleccione la duración del alquiler en minutos");
        TooltipUtil.setupTooltip(spiHoraApertura, "Seleccione la hora de apertura");
        TooltipUtil.setupTooltip(spiHoraCierre, "Seleccione la hora de cierre");
        configurarIconos();
    }

    private void configurarIconos() {
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
        MFXFontIcon fontIcon = new MFXFontIcon("fas-floppy-disk", 24, Color.WHITE);
        btnGuardar.setGraphic(new MFXIconWrapper(fontIcon, 24));
    }

    /**
     * Inicializa los campos de la interfaz gráfica, configurando los valores y eventos necesarios.
     */
    private void inicializarCampos() {
        cmbTipo.setItems(FXCollections.observableArrayList(TIPOS_INSTALACION));
        cmbTipo.setPromptText("Tipo de instalación");
        cmbDisponibilidad.setItems(FXCollections.observableArrayList(OPCION_DISPONIBILIDAD));
        cmbDisponibilidad.setPromptText("Disponibilidad");
        inicializarSpinners();
        cmbTipo.setOnAction(e -> actualizarImagen());
    }

    /**
     * Actualiza la imagen mostrada en la interfaz según el tipo de instalación seleccionado.
     */
    private void actualizarImagen() {
        String tipo = cmbTipo.getValue();
        String imageBasePath = null;
        if (tipo != null) {
            imageBasePath = switch (tipo) {
                case "Pista de Padel" -> PATH_IMAGE + "padel.jpg";
                case "Deportiva" -> PATH_IMAGE + "futbol.jpg";
                case "Recreativa" -> PATH_IMAGE + "salon.jpg";
                default -> PATH_IMAGE + "noimage.jpg";
            };
        }
        assert imageBasePath != null;
        Image image = new Image(Objects.requireNonNull(getClass().getResource(imageBasePath)).toString(), imgPhoto.getFitWidth(), imgPhoto.getFitHeight(), false, true);
        imgPhoto.setImage(image);
    }

    /**
     * Carga los datos de las instalaciones en la tabla de la interfaz gráfica.
     */
    private void cargarDatosTabla() {
        InstalacionDAO instalacionDAO = new InstalacionDAO();
        List<Instalacion> listaInstalaciones = instalacionDAO.obtenerInstalaciones();
        ObservableList<Instalacion> instalaciones = FXCollections.observableArrayList(listaInstalaciones);
        tabla.setItems(instalaciones);
    }

    /**
     * Guarda una nueva instalación en la base de datos.
     *
     * @throws IOException Si ocurre un error al procesar la imagen de la instalación.
     */
    private void guardarInstalacion() throws IOException {
        InstalacionDAO instalacionDAO = new InstalacionDAO();

        String nombre = txtNombre.getText();
        String capacidadStr = txtCapacidad.getText();
        String precioStr = txtPrecio.getText();
        Integer duracion = spiDuracion.getValue();
        LocalTime horaApertura = spiHoraApertura.getValue();
        LocalTime horaCierre = spiHoraCierre.getValue();
        String disponibilidadStr = cmbDisponibilidad.getValue();
        String tipo = cmbTipo.getValue();

        boolean valido = true;

        lblNombreError.setText("");
        lblCapacidadError.setText("");
        lblPrecioError.setText("");

        String error;

        // Validar el nombre de la instalación
        error = Validaciones.validarNombreInstalacion(nombre);
        if (error != null) {
            lblNombreError.setText(error);
            valido = false;
        }

        // Validar la capacidad de la instalación
        error = Validaciones.validarCapacidadInstalacion(capacidadStr);
        if (error != null) {
            lblCapacidadError.setText(error);
            valido = false;
        }

        // Validar el precio de alquiler de la instalación
        error = Validaciones.validarPrecioAlquiler(precioStr);
        if (error != null) {
            lblPrecioError.setText(error);
            valido = false;
        }

        if (!valido) {
            return;
        }

        // Procesar la imagen de la instalación
        Image image = imgPhoto.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        byte[] fileContent = baos.toByteArray();

        // Crear una nueva instalación
        Instalacion instalacion = new Instalacion();
        instalacion.setNombre(nombre);
        instalacion.setCapacidad(Integer.parseInt(capacidadStr));
        instalacion.setPrecioAlquiler(new BigDecimal(precioStr));
        instalacion.setDuracion(duracion);
        instalacion.setHoraIni(horaApertura);
        instalacion.setHoraFin(horaCierre);
        instalacion.setDisponibilidad((byte) (DISPONIBLE.equals(disponibilidadStr) ? 1 : 0));
        instalacion.setTipo(tipo);
        instalacion.setImagen(fileContent);

        // Asignar el empleado autenticado a la instalación
        Integer authenticatedEmployeeId = SessionManager.getInstance().getAuthenticatedEmployeeId();
        if (authenticatedEmployeeId != null) {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(authenticatedEmployeeId);
            instalacion.setIdEmpleado(empleado);
        }

        // Mostrar un diálogo de confirmación antes de guardar la instalación
        DialogoController.showConfirmDialog(
                (Stage) tabla.getScene().getWindow(),
                "¿Desea guardar esta instalación?",
                e -> instalacionDAO.crearInstalacion(instalacion)
        );

        cargarDatosTabla();
    }

    /**
     * Actualiza un campo específico de una instalación en la base de datos.
     *
     * @param event El evento de edición de la celda.
     * @param campo El nombre del campo que se va a actualizar.
     */
    private void actualizarInstalacion(TableColumn.CellEditEvent<Instalacion, ?> event, String campo) {
        Instalacion instalacion = event.getRowValue();
        Object nuevoValor = event.getNewValue();

        // Mostrar un diálogo de confirmación antes de actualizar la instalación
        DialogoController.showConfirmDialog(
                (Stage) tabla.getScene().getWindow(),
                "¿Desea actualizar el campo " + campo + " a " + nuevoValor + "?",
                e -> {
                    switch (campo) {
                        case CAPACIDAD:
                            instalacion.setCapacidad((Integer) nuevoValor);
                            break;
                        case DURACION:
                            instalacion.setDuracion((Integer) nuevoValor);
                            break;
                        case PRECIO_ALQUILER:
                            instalacion.setPrecioAlquiler((BigDecimal) nuevoValor);
                            break;
                        case HORA_INI:
                            instalacion.setHoraIni((LocalTime) nuevoValor);
                            break;
                        case HORA_FIN:
                            instalacion.setHoraFin((LocalTime) nuevoValor);
                            break;
                        case DISPONIBILIDAD:
                            instalacion.setDisponibilidad((byte) nuevoValor);
                            break;
                        default:
                            logger.error("Campo no válido");
                            break;
                    }

                    InstalacionDAO instalacionDAO = new InstalacionDAO();
                    instalacionDAO.updateInstalacion(instalacion);
                }
        );
    }

    /**
     * Configura la tabla de instalaciones, estableciendo las columnas y sus propiedades.
     * También define los eventos de edición para cada columna.
     */
    private void configurarTablaInstalacion() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colId.setOnEditCommit(event -> actualizarInstalacion(event, "id"));

        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTipo.setCellFactory(TextFieldTableCell.forTableColumn());
        colTipo.setOnEditCommit(event -> actualizarInstalacion(event, "tipo"));

        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNombre.setCellFactory(TextFieldTableCell.forTableColumn());
        colNombre.setOnEditCommit(event -> actualizarInstalacion(event, "nombre"));

        colCapacidad.setCellValueFactory(new PropertyValueFactory<>(CAPACIDAD));
        colCapacidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colCapacidad.setOnEditCommit(event -> actualizarInstalacion(event, CAPACIDAD));

        colDuracion.setCellValueFactory(new PropertyValueFactory<>(DURACION));
        colDuracion.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colDuracion.setOnEditCommit(event -> actualizarInstalacion(event, DURACION));

        colAlquiler.setCellValueFactory(new PropertyValueFactory<>(PRECIO_ALQUILER));
        colAlquiler.setCellFactory(TextFieldTableCell.forTableColumn(new BigDecimalStringConverter()));
        colAlquiler.setOnEditCommit(event -> actualizarInstalacion(event, PRECIO_ALQUILER));

        colHoraApertura.setCellValueFactory(new PropertyValueFactory<>(HORA_INI));
        colHoraApertura.setCellFactory(TextFieldTableCell.forTableColumn(new LocalTimeStringConverter()));
        colHoraApertura.setOnEditCommit(event -> actualizarInstalacion(event, HORA_INI));

        colHoraCierre.setCellValueFactory(new PropertyValueFactory<>(HORA_FIN));
        colHoraCierre.setCellFactory(TextFieldTableCell.forTableColumn(new LocalTimeStringConverter()));
        colHoraCierre.setOnEditCommit(event -> actualizarInstalacion(event, HORA_FIN));

        colDisponibilidad.setCellValueFactory(new PropertyValueFactory<>(DISPONIBILIDAD));
        colDisponibilidad.setCellFactory(ComboBoxTableCell.forTableColumn(
                new StringConverter<>() {
                    @Override
                    public String toString(Byte object) {
                        return object == 1 ? DISPONIBLE : NO_DISPONIBLE;
                    }

                    @Override
                    public Byte fromString(String string) {
                        return DISPONIBLE.equals(string) ? (byte) 1 : (byte) 0;
                    }
                },
                FXCollections.observableArrayList((byte) 1, (byte) 0)
        ));
        colDisponibilidad.setOnEditCommit(event -> {
            Instalacion instalacion = event.getRowValue();
            Byte nuevoValor = event.getNewValue();
            instalacion.setDisponibilidad(nuevoValor);
            actualizarInstalacion(event, DISPONIBILIDAD);
        });
        tabla.setEditable(true);
    }

    /**
     * Inicializa los spinners de la interfaz gráfica, configurando sus valores y eventos.
     */
    private void inicializarSpinners() {
        configurarSpinnerDuracion();
        configurarSpinnerHora(spiHoraApertura, LocalTime.of(8, 0), "Hora de apertura");
        configurarSpinnerHora(spiHoraCierre, LocalTime.of(23, 0), "Hora de cierre");

        // Validar que la hora de apertura no sea posterior a la hora de cierre
        spiHoraApertura.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isAfter(spiHoraCierre.getValue())) {
                spiHoraApertura.getValueFactory().setValue(oldValue);
                logger.error("La hora de apertura no puede ser posterior a la hora de cierre.");
            }
        });

        // Validar que la hora de cierre no sea anterior a la hora de apertura
        spiHoraCierre.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isBefore(spiHoraApertura.getValue())) {
                spiHoraCierre.getValueFactory().setValue(oldValue);
                logger.error("La hora de cierre no puede ser anterior a la hora de apertura.");
            }
        });
    }

    /**
     * Configura el spinner de duración del alquiler.
     */
    private void configurarSpinnerDuracion() {
        SpinnerValueFactory<Integer> factoryDuracion = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 500, 1);
        spiDuracion.setValueFactory(factoryDuracion);
        spiDuracion.getEditor().setPromptText("Duración del alquiler en minutos");
        spiDuracion.setEditable(true);
    }

    /**
     * Configura un spinner de hora con un valor inicial y un texto de sugerencia.
     *
     * @param spinner      El spinner a configurar.
     * @param valorInicial El valor inicial del spinner.
     * @param promptText   El texto de sugerencia para el editor del spinner.
     */
    private void configurarSpinnerHora(Spinner<LocalTime> spinner, LocalTime valorInicial, String promptText) {
        SpinnerValueFactory<LocalTime> factoryHora = new CustomSpinnerValueFactory(valorInicial);
        spinner.setValueFactory(factoryHora);
        spinner.setEditable(true);
        spinner.getEditor().setPromptText(promptText);
    }

    /**
     * Clase interna para configurar un SpinnerValueFactory personalizado para LocalTime.
     */
    private static class CustomSpinnerValueFactory extends SpinnerValueFactory<LocalTime> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        public CustomSpinnerValueFactory(LocalTime valorInicial) {
            setConverter(new StringConverter<>() {
                @Override
                public String toString(LocalTime time) {
                    return time != null ? time.format(formatter) : "";
                }

                @Override
                public LocalTime fromString(String string) {
                    return string != null && !string.isEmpty() ? LocalTime.parse(string, formatter) : null;
                }
            });
            setValue(valorInicial);
        }

        @Override
        public void decrement(int steps) {
            LocalTime time = getValue();
            setValue(time.minusMinutes((long) steps * 15));
        }

        @Override
        public void increment(int steps) {
            LocalTime time = getValue();
            setValue(time.plusMinutes((long) steps * 15));
        }
    }

    /**
     * Elimina una instalación de la base de datos y actualiza la tabla.
     *
     * @param instalacion La instalación a eliminar.
     */
    private void eliminarInstalacion(Instalacion instalacion) {
        InstalacionDAO instalacionDAO = new InstalacionDAO();
        instalacionDAO.deleteInstalacion(instalacion.getId());
        cargarDatosTabla();
    }

    /**
     * Configura el menú contextual para la tabla de instalaciones.
     */
    private void configurarContextMenu() {
        contextMenu = new ContextMenu();
        contextMenu.getItems().add(crearMenuItemEliminar());

        tabla.setRowFactory(tv -> {
            TableRow<Instalacion> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    /**
     * Crea un menú de contexto para eliminar una instalación.
     *
     * @return El menú de contexto para eliminar una instalación.
     */
    private MenuItem crearMenuItemEliminar() {
        MenuItem deleteItem = new MenuItem("Eliminar Instalacion");
        deleteItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");
        deleteItem.setOnAction(event -> {
            Instalacion instalacionSeleccionada = tabla.getSelectionModel().getSelectedItem();
            if (instalacionSeleccionada != null) {
                DialogoController.showConfirmDialog(
                        (Stage) tabla.getScene().getWindow(),
                        "¿Desea eliminar esta instalación?",
                        e -> eliminarInstalacion(instalacionSeleccionada)
                );
            }
        });
        return deleteItem;
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
            var resource = getClass().getResource("/help/help_add_instalacion.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda para añadir instalación");
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