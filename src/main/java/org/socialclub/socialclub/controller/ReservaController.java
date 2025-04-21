package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import org.socialclub.socialclub.database.HorarioDAO;
import org.socialclub.socialclub.database.InstalacionDAO;
import org.socialclub.socialclub.database.ReservaDAO;
import org.socialclub.socialclub.database.SocioDAO;
import org.socialclub.socialclub.model.Instalacion;
import org.socialclub.socialclub.model.Reserva;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.Reportes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para gestionar las reservas en la aplicación.
 */
public class ReservaController {

    // FXML Components
    @FXML
    public TableView<Reserva> tableReservas;
    @FXML
    public TableColumn<Reserva, LocalDate> colFecha;
    @FXML
    public TableColumn<Reserva, Instalacion> colInstalacion;
    @FXML
    public TableColumn<Reserva, LocalTime> colHora;
    @FXML
    public TableColumn<Reserva, Socio> colNumSocio;
    @FXML
    public TableColumn<Reserva, String> colNombreSocio;
    @FXML
    public MFXIconButton helpIcon;
    @FXML
    private MFXComboBox<String> cmbInstalacion;
    @FXML
    private MFXComboBox<String> cmbSocio;
    @FXML
    private MFXDatePicker calendar;
    @FXML
    private MFXButton btnReservas;
    @FXML
    private GridPane gridHorarios;

    /**
     * Inicializa los componentes de la interfaz gráfica de la aplicación.
     * Este método se llama cuando se inicia la aplicación.
     */
    @FXML
    public void initialize() {
        configurarIconos();
        inicializarComboBoxSocios();
        inicializarComboBoxInstalaciones();
        configurarListeners();
        cargarDatosTabla();
        configurarTabla();
        configurarEstiloFilas();
    }

    /**
     * Configura los iconos de la aplicación.
     */
    private void configurarIconos() {
        MFXFontIcon iconoReporte = new MFXFontIcon("fas-print", 24, Color.WHITE);
        btnReservas.setGraphic(iconoReporte);
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Carga los datos en la tabla para mostrar las reservas.
     */
    private void cargarDatosTabla() {
        ReservaDAO reservaDAO = new ReservaDAO();
        List<Reserva> reservas = reservaDAO.obtenerReservas();
        ObservableList<Reserva> items = FXCollections.observableArrayList(reservas);
        tableReservas.setItems(items);
    }

    /**
     * Configura la tabla para mostrar las reservas.
     */
    private void configurarTabla() {
        configurarColumnaFecha(colFecha);
        configurarColumnaInstalacion(colInstalacion);
        configurarColumnaHora(colHora);
        configurarColumnaSocio(colNumSocio);
        configurarColumnaNombreSocio(colNombreSocio);

        // Ordena la tabla por fecha de la reserva, de la más reciente a la más antigua
        colFecha.setSortType(TableColumn.SortType.DESCENDING);
        tableReservas.getSortOrder().add(colFecha);
    }

    /**
     * Configura la columna "Nombre Socio" en la tabla para mostrar el nombre completo del socio.
     *
     * @param colNombreSocio La columna "Nombre Socio" en la tabla.
     */
    private void configurarColumnaNombreSocio(TableColumn<Reserva, String> colNombreSocio) {
        colNombreSocio.setCellValueFactory(cellData -> {
            Socio socio = cellData.getValue().getNumeroSocio();
            String nombreCompleto = socio != null ? socio.getNombre() + " " + socio.getApellidos() : "Desconocido";
            return new SimpleStringProperty(nombreCompleto);
        });
        colNombreSocio.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Configura la columna "Socio" en la tabla para mostrar el número del socio.
     *
     * @param colNumSocio La columna "Socio" en la tabla.
     */
    private void configurarColumnaSocio(TableColumn<Reserva, Socio> colNumSocio) {
        colNumSocio.setCellValueFactory(new PropertyValueFactory<>("numeroSocio"));
        colNumSocio.setCellFactory(TextFieldTableCell.forTableColumn(new SocioStringConverter()));
    }

    /**
     * Configura la columna "Hora" en la tabla para mostrar la hora de la reserva.
     *
     * @param colHora La columna "Hora" en la tabla.
     */
    private void configurarColumnaHora(TableColumn<Reserva, LocalTime> colHora) {
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colHora.setCellFactory(TextFieldTableCell.forTableColumn(new LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm"), DateTimeFormatter.ofPattern("HH:mm"))));
    }

    /**
     * Configura la columna "Instalación" en la tabla para mostrar el nombre de la instalación.
     *
     * @param colInstalacion La columna "Instalación" en la tabla.
     * @throws IllegalArgumentException Si la columna proporcionada es nula.
     */
    private void configurarColumnaInstalacion(TableColumn<Reserva, Instalacion> colInstalacion) {
        if (colInstalacion == null) {
            throw new IllegalArgumentException("La columna proporcionada es nula.");
        }

        colInstalacion.setCellValueFactory(new PropertyValueFactory<>("idInstalacion"));
        colInstalacion.setCellFactory(TextFieldTableCell.forTableColumn(new InstalacionStringConverter()));
    }

    /**
     * Configura la columna "Fecha" en la tabla para mostrar la fecha de la reserva.
     *
     * @param colFecha La columna "Fecha" en la tabla.
     */
    private void configurarColumnaFecha(TableColumn<Reserva, LocalDate> colFecha) {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
    }

    /**
     * Configura los listeners para el date picker y el combo box de instalaciones.
     * Cuando se cambia la fecha, actualiza las horas disponibles para la instalación seleccionada.
     * Cuando se cambia la instalación, actualiza las horas disponibles para la fecha seleccionada.
     */
    private void configurarListeners() {
        // Selecciona la fecha actual por defecto
        calendar.setValue(LocalDate.now());

        // Añade un listener para detectar cambios en la fecha seleccionada
        calendar.valueProperty().addListener((observable, oldValue, newValue) -> mostrarHorasDisponibles());

        // Añade un listener para detectar cambios en la instalación seleccionada
        cmbInstalacion.valueProperty().addListener((observable, oldValue, newValue) -> mostrarHorasDisponibles());
    }

    /**
     * Inicializa el ComboBox con los nombres de las instalaciones disponibles.
     * Las instalaciones se obtienen de la base de datos utilizando el InstalacionDAO.
     */
    private void inicializarComboBoxInstalaciones() {
        InstalacionDAO instalacionDAO = new InstalacionDAO();
        List<Instalacion> instalacionesList = instalacionDAO.obtenerInstalaciones();

        // Convierte la lista de Instalacion a una lista de nombres de instalaciones
        List<String> nombresInstalaciones = instalacionesList.stream()
                .map(Instalacion::getNombre)
                .toList(); // Usa Stream.toList()

        ObservableList<String> items = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(nombresInstalaciones));
        cmbInstalacion.setItems(items);
    }

    /**
     * Inicializa el ComboBox con los nombres de los socios que tienen 18 años o más.
     * Los socios se obtienen de la base de datos utilizando el SocioDAO.
     */
    private void inicializarComboBoxSocios() {
        SocioDAO socioDAO = new SocioDAO();
        List<Socio> sociosList = socioDAO.obtenerTodosSocios();

        // Filtrar socios mayores de edad
        List<Socio> sociosMayoresEdad = sociosList.stream()
                .filter(socio -> Period.between(socio.getFechaNacimiento(), LocalDate.now()).getYears() >= 18)
                .toList(); // Usa Stream.toList()

        ObservableList<String> items = FXCollections.observableArrayList();
        for (Socio socio : sociosMayoresEdad) {
            String displayText = String.format("%s - %s %s",
                    socio.getNumeroSocio(),
                    socio.getNombre(),
                    socio.getApellidos());
            items.add(displayText);
        }

        cmbSocio.setItems(items);

        // Habilitar búsqueda en el ComboBox
        cmbSocio.setEditable(true);
        cmbSocio.textProperty().addListener((obs, oldText, newText) -> {
            ObservableList<String> filteredItems = FXCollections.observableArrayList();
            for (String item : items) {
                if (item.toLowerCase().contains(newText.toLowerCase())) {
                    filteredItems.add(item);
                }
            }
            cmbSocio.setItems(filteredItems);
        });
    }

    /**
     * Obtiene el número de socio seleccionado en el ComboBox de socios.
     *
     * @return El número de socio seleccionado como una cadena, o null si no hay ningún socio seleccionado.
     */
    public String getNumeroSocioSeleccionado() {
        String selectedItem = cmbSocio.getSelectionModel().getSelectedItem();
        if (selectedItem != null && !selectedItem.isEmpty()) {
            return selectedItem.split(" - ")[0]; // Extraer el número de socio
        }
        return null; // Devolver null si no hay un elemento seleccionado
    }

    /**
     * Obtiene una lista de horas reservadas para una instalación específica y una fecha determinada.
     *
     * @param idInstalacion El identificador único de la instalación.
     * @param fecha         La fecha para la que se obtienen las horas reservadas.
     * @return Una lista de objetos LocalTime que representan las horas reservadas.
     */
    private List<LocalTime> obtenerHorasReservadas(int idInstalacion, LocalDate fecha) {
        ReservaDAO reservaDAO = new ReservaDAO();
        return reservaDAO.obtenerHorasReservadas(idInstalacion, fecha);
    }

    /**
     * Actualiza la interfaz de usuario para mostrar las horas disponibles para una instalación y fecha seleccionadas.
     */
    private void mostrarHorasDisponibles() {
        LocalDate fechaSeleccionada = calendar.getValue();
        String instalacionSeleccionada = cmbInstalacion.getSelectionModel().getSelectedItem();

        if (fechaSeleccionada != null && instalacionSeleccionada != null) {
            Instalacion instalacion = obtenerInstalacionSeleccionada(instalacionSeleccionada);

            if (instalacion != null) {
                List<LocalTime> todasLasHoras = obtenerTodasLasHoras(instalacion.getId());
                List<LocalTime> horasReservadas = obtenerHorasReservadas(instalacion.getId(), fechaSeleccionada);

                actualizarInterfazHorasDisponibles(todasLasHoras, horasReservadas, fechaSeleccionada, instalacionSeleccionada);
            }
        }
    }

    /**
     * Obtiene la instalación seleccionada desde la base de datos según el nombre de la instalación.
     *
     * @param instalacionSeleccionada El nombre de la instalación seleccionada.
     * @return El objeto de instalación seleccionado, o null si no se encuentra ninguna instalación.
     */
    private Instalacion obtenerInstalacionSeleccionada(String instalacionSeleccionada) {
        InstalacionDAO instalacionDAO = new InstalacionDAO();
        return instalacionDAO.obtenerInstalaciones().stream()
                .filter(inst -> inst.getNombre().equals(instalacionSeleccionada))
                .findFirst()
                .orElse(null);
    }

    /**
     * Actualiza la interfaz de usuario para mostrar las horas disponibles para una instalación y fecha seleccionadas.
     *
     * @param todasLasHoras           Una lista de todas las horas disponibles para la instalación.
     * @param horasReservadas         Una lista de horas reservadas para la instalación y fecha.
     * @param fechaSeleccionada       La fecha seleccionada.
     * @param instalacionSeleccionada La instalación seleccionada.
     */
    private void actualizarInterfazHorasDisponibles(List<LocalTime> todasLasHoras, List<LocalTime> horasReservadas, LocalDate fechaSeleccionada, String instalacionSeleccionada) {
        gridHorarios.getChildren().clear();

        if (todasLasHoras == null || todasLasHoras.isEmpty()) {
            Label mensajeLabel = new Label("No hay horas disponibles para la fecha seleccionada.");
            gridHorarios.add(mensajeLabel, 0, 0);
        } else {
            LocalDateTime ahora = LocalDateTime.now();

            for (int i = 0; i < todasLasHoras.size(); i++) {
                LocalTime hora = todasLasHoras.get(i);
                LocalDateTime fechaHora = LocalDateTime.of(fechaSeleccionada, hora);

                MFXButton horaButton = crearBotonHora(hora, fechaSeleccionada, fechaHora, ahora, horasReservadas, instalacionSeleccionada);
                gridHorarios.add(horaButton, i % gridHorarios.getColumnCount(), i / gridHorarios.getColumnCount());
            }
        }
    }

    /**
     * Crea un botón para una hora específica, aplica estilo y deshabilita el botón si es necesario.
     * Si el botón no está deshabilitado, agrega un escuchador de acciones para manejar la reserva.
     *
     * @param hora                    La hora específica para la que se crea el botón.
     * @param fechaSeleccionada       La fecha seleccionada.
     * @param fechaHora               La fecha y hora combinadas para la fecha seleccionada y la hora.
     * @param ahora                   La fecha y hora actuales.
     * @param horasReservadas         La lista de horas reservadas para la fecha e instalación seleccionadas.
     * @param instalacionSeleccionada La instalación seleccionada.
     * @return El botón creado y configurado.
     */
    private MFXButton crearBotonHora(LocalTime hora, LocalDate fechaSeleccionada, LocalDateTime fechaHora, LocalDateTime ahora, List<LocalTime> horasReservadas, String instalacionSeleccionada) {
        MFXButton horaButton = new MFXButton(hora.toString());
        aplicarEstiloYDeshabilitarBoton(horaButton, fechaSeleccionada, fechaHora, ahora, horasReservadas);

        if (!horaButton.isDisabled()) {
            horaButton.setOnAction(event -> manejarReserva(horaButton.getText(), instalacionSeleccionada, fechaSeleccionada));
        }

        return horaButton;
    }

    /**
     * Aplica estilo y deshabilita el botón según las condiciones dadas.
     *
     * @param horaButton        El botón al que se aplicará el estilo y se deshabilitará.
     * @param fechaSeleccionada La fecha seleccionada.
     * @param fechaHora         La fecha y hora combinadas para la fecha seleccionada y la hora del botón.
     * @param ahora             La fecha y hora actuales.
     * @param horasReservadas   La lista de horas reservadas para la fecha e instalación seleccionadas.
     */
    private void aplicarEstiloYDeshabilitarBoton(MFXButton horaButton, LocalDate fechaSeleccionada, LocalDateTime fechaHora, LocalDateTime ahora, List<LocalTime> horasReservadas) {
        if (fechaSeleccionada.isBefore(LocalDate.now()) || (fechaSeleccionada.isEqual(LocalDate.now()) && fechaHora.isBefore(ahora))) {
            horaButton.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
            horaButton.setDisable(true);
        } else {
            LocalTime horaButtonTime = LocalTime.parse(horaButton.getText());
            if (horasReservadas.contains(horaButtonTime)) {
                horaButton.setStyle("-fx-background-color: lightgray; -fx-text-fill: black;");
                horaButton.setDisable(true);
            } else {
                horaButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            }
        }
    }

    /**
     * Obtiene todas las horas disponibles para una instalación específica.
     *
     * @param idInstalacion El identificador único de la instalación.
     * @return Una lista de objetos LocalTime que representan todas las horas disponibles para la instalación.
     */
    private List<LocalTime> obtenerTodasLasHoras(int idInstalacion) {
        HorarioDAO horarioDAO = new HorarioDAO();
        return horarioDAO.obtenerHorasPorInstalacion(idInstalacion);
    }

    /**
     * Maneja el proceso de reserva mostrando un diálogo de confirmación al usuario,
     * guardando la reserva en la base de datos y actualizando la interfaz de usuario.
     *
     * @param hora        La hora seleccionada para la reserva.
     * @param instalacion La instalación seleccionada para la reserva.
     * @param fecha       La fecha seleccionada para la reserva.
     */
    private void manejarReserva(String hora, String instalacion, LocalDate fecha) {
        String numeroSocio = getNumeroSocioSeleccionado();

        if (numeroSocio == null || numeroSocio.isEmpty()) {
            // No hay socio seleccionado
            DialogoController.showInfoDialog((Stage) btnReservas.getScene().getWindow(), "Por favor, seleccione un socio antes de realizar la reserva.", event -> {
            });
        } else {
            // Hay socio seleccionado, preguntar si quiere reservar
            String mensaje = String.format("El socio %s desea reservar la pista %s el día %s a las %s. ¿Está seguro?",
                    numeroSocio, instalacion, fecha, hora);

            DialogoController.showConfirmDialog((Stage) btnReservas.getScene().getWindow(), mensaje, event -> {
                try {
                    // Convertir la hora de String a LocalTime
                    LocalTime horaLocalTime = LocalTime.parse(hora);

                    // Obtener el objeto Instalacion correspondiente
                    InstalacionDAO instalacionDAO = new InstalacionDAO();
                    Instalacion inst = instalacionDAO.obtenerInstalaciones().stream()
                            .filter(i -> i.getNombre().equals(instalacion))
                            .findFirst()
                            .orElseThrow(() -> new IllegalArgumentException("La instalación " + instalacion + " no existe."));

                    // Guardar la reserva en la base de datos
                    ReservaDAO reservaDAO = new ReservaDAO();
                    reservaDAO.guardarReserva(numeroSocio, inst, fecha, horaLocalTime);
                    DialogoController.showInfoDialog((Stage) btnReservas.getScene().getWindow(), "Reserva Confirmada", event2 -> {
                    });

                    // Actualizar los horarios disponibles
                    mostrarHorasDisponibles();
                    cargarDatosTabla();
                } catch (Exception ex) {
                    LOGGER.severe("Error al guardar la reserva: " + ex.getMessage());
                }
            });
        }
    }

    /**
     * Configura el estilo de las filas de la tabla de reservas.
     * Aplica un color de fondo rojo para las reservas pasadas y verde para las futuras.
     * También añade un menú contextual para eliminar reservas.
     */
    private void configurarEstiloFilas() {
        tableReservas.setRowFactory(tv -> {
            TableRow<Reserva> row = new TableRow<>() {
                @Override
                protected void updateItem(Reserva reserva, boolean empty) {
                    super.updateItem(reserva, empty);
                    if (reserva == null || empty) {
                        setStyle("");
                    } else {
                        if (reserva.getFecha().isBefore(LocalDate.now())) {
                            setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                        } else {
                            setStyle("-fx-background-color: #0ec32c; -fx-text-fill: black; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
                        }
                    }
                }
            };

            // Crear el menú contextual
            ContextMenu contextMenu = new ContextMenu();
            MenuItem eliminarItem = new MenuItem("Eliminar");
            eliminarItem.setStyle("-fx-background-color: white -fx-text-fill: black; -fx-font-size: 14px;"); // Estilo del menú contextual
            eliminarItem.setOnAction(event -> {
                Reserva reserva = row.getItem();
                if (reserva != null) {
                    eliminarReserva(reserva);
                }
            });
            contextMenu.getItems().add(eliminarItem);

            // Asignar el menú contextual a la fila
            row.setContextMenu(contextMenu);

            return row;
        });
    }

    /**
     * Elimina una reserva de la base de datos y actualiza la tabla de reservas.
     *
     * @param reserva La reserva a eliminar.
     */
    private void eliminarReserva(Reserva reserva) {
        String mensaje = "¿Estás seguro de que deseas eliminar la reserva?";

        DialogoController.showConfirmDialog((Stage) tableReservas.getScene().getWindow(), mensaje, event -> {
            ReservaDAO reservaDAO = new ReservaDAO();
            reservaDAO.eliminarReserva(reserva.getId());
            DialogoController.showInfoDialog((Stage) tableReservas.getScene().getWindow(), "Reserva Eliminada", event2 ->
                    cargarDatosTabla() // Recargar la tabla después de eliminar la reserva
            );
        });
    }

    /**
     * Maneja la acción de imprimir un reporte de reservas.
     * Convierte los datos de las reservas en un formato adecuado y llama al método de impresión.
     */
    @FXML
    public void handleImprimirReporteReservas() {
        List<Reserva> reservas = tableReservas.getItems();

        List<Map<String, Object>> reservasMap = new ArrayList<>();
        for (Reserva reserva : reservas) {
            Map<String, Object> map = new HashMap<>();
            map.put("numeroSocio", String.valueOf(reserva.getNumeroSocio().getNumeroSocio())); // Convertir a String
            map.put("nombre", reserva.getNumeroSocio().getNombre() + " " + reserva.getNumeroSocio().getApellidos());
            map.put("hora", reserva.getHora()); // Asegurar que sea String
            map.put("fecha", reserva.getFecha()); // Asegurar que sea String
            map.put("instalacion", reserva.getIdInstalacion().getNombre());   // Asegurar que sea String
            reservasMap.add(map);
        }
        Map<String, Object> parametros = new HashMap<>();
        Reportes.imprimirReporteReservas(reservasMap, parametros);
    }

    /**
     * Convertidor de String a Instalacion y viceversa.
     */
    private static class InstalacionStringConverter extends StringConverter<Instalacion> {
        @Override
        public String toString(Instalacion instalacion) {
            return instalacion != null ? instalacion.getNombre() : "";
        }

        @Override
        public Instalacion fromString(String string) {
            Instalacion instalacion = new Instalacion();
            instalacion.setNombre(string);
            return instalacion;
        }
    }

    /**
     * Convertidor de String a Socio y viceversa.
     */
    private static class SocioStringConverter extends StringConverter<Socio> {
        @Override
        public String toString(Socio socio) {
            return socio != null ? socio.getNumeroSocio() : "";
        }

        @Override
        public Socio fromString(String string) {
            Socio socio = new Socio();
            socio.setNumeroSocio(string);
            return socio;
        }
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
            var resource = getClass().getResource("/help/help_reservas.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Reservas");
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