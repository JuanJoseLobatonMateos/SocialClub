package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
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
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.LocalTimeStringConverter;
import org.socialclub.socialclub.database.RegistroEntradaDAO;
import org.socialclub.socialclub.model.RegistroEntrada;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.Reportes;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static javafx.animation.Animation.INDEFINITE;
import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para la gestión de los registros de entrada y salida de socios.
 * Proporciona la funcionalidad para visualizar, editar y actualizar los registros
 * de entrada y salida de los socios en la interfaz de usuario.
 */
public class RegistroController {

    private static final String HORA_ENTRADA = "horaEntrada";
    private static final String NUMERO_SOCIO = "numeroSocio";
    private static final String NOMBRE = "nombre";
    private static final String APELLIDOS = "apellidos";
    private static final String HORA_SALIDA = "horaSalida";
    private static final String CAMPO_NO_VALIDO = "Campo no válido: ";

    @FXML
    public MFXDatePicker cmbCalendar;
    @FXML
    public TableView<RegistroEntrada> tblEntradas;
    @FXML
    public TableColumn<RegistroEntrada, LocalTime> colHoraEntrada;
    @FXML
    public TableColumn<RegistroEntrada, Socio> colNumeroSocioEntrante;
    @FXML
    public TableColumn<RegistroEntrada, String> colNombreEntrante;
    @FXML
    public TableColumn<RegistroEntrada, String> colApellidosEntrante;
    @FXML
    public TableView<RegistroEntrada> tblSalidas;
    @FXML
    public TableColumn<RegistroEntrada, LocalTime> colHoraSalida;
    @FXML
    public TableColumn<RegistroEntrada, Socio> colNumeroSocioSaliente;
    @FXML
    public TableColumn<RegistroEntrada, String> colNombreSaliente;
    @FXML
    public TableColumn<RegistroEntrada, String> colApellidosSaliente;
    @FXML
    public MFXButton btnReporteRegistro;
    @FXML
    public Label lblTotalSocios;
    @FXML
    public Label lblSociosDentro;
    @FXML
    public MFXIconButton helpIcon;

    @FXML
    private void initialize() {
        configurarIconos();
        inicializarComboBox();
        configurarTabla();
        cargarDatosTabla(LocalDate.now());
        iniciarPolling();
    }

private void configurarTabla() {
        configurarColumnaHora(colHoraEntrada, HORA_ENTRADA);
        configurarColumnaSocio(colNumeroSocioEntrante);
        configurarColumna(colNombreEntrante, NOMBRE);
        configurarColumna(colApellidosEntrante, APELLIDOS);
        configurarColumnaHora(colHoraSalida, HORA_SALIDA);
        configurarColumnaSocio(colNumeroSocioSaliente);
        configurarColumna(colNombreSaliente, NOMBRE);
        configurarColumna(colApellidosSaliente, APELLIDOS);

        tblEntradas.setEditable(false);
        tblSalidas.setEditable(false);
    }
    private void configurarColumna(TableColumn<RegistroEntrada, String> columna, String campo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(campo));
        columna.setCellFactory(TextFieldTableCell.forTableColumn());
        columna.setOnEditCommit(event -> actualizarRegistro(event, campo));
    }

    private void configurarColumnaHora(TableColumn<RegistroEntrada, LocalTime> columna, String campo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(campo));
        columna.setCellFactory(TextFieldTableCell.forTableColumn(new LocalTimeStringConverter(DateTimeFormatter.ofPattern("HH:mm"), DateTimeFormatter.ofPattern("HH:mm"))));
        columna.setOnEditCommit(event -> actualizarRegistroHora(event, campo));
    }

  private void configurarColumnaSocio(TableColumn<RegistroEntrada, Socio> columna) {
      columna.setCellValueFactory(new PropertyValueFactory<>(NUMERO_SOCIO));
      columna.setCellFactory(TextFieldTableCell.forTableColumn(new SocioStringConverter()));
      columna.setOnEditCommit(this::actualizarRegistroSocio);
  }

   private void actualizarRegistroSocio(TableColumn.CellEditEvent<RegistroEntrada, Socio> event) {
       RegistroEntrada registro = event.getRowValue();
       Socio nuevoValor = event.getNewValue();

       registro.setNumeroSocio(nuevoValor);

       RegistroEntradaDAO registroDAO = new RegistroEntradaDAO();
       registroDAO.actualizarRegistroEntrada(registro);
   }

    private void actualizarRegistro(TableColumn.CellEditEvent<RegistroEntrada, String> event, String campo) {
        RegistroEntrada registro = event.getRowValue();
        String nuevoValor = event.getNewValue();

        switch (campo) {
            case NOMBRE:
                registro.getNumeroSocio().setNombre(nuevoValor);
                break;
            case APELLIDOS:
                registro.getNumeroSocio().setApellidos(nuevoValor);
                break;
            default:
                throw new IllegalArgumentException(CAMPO_NO_VALIDO + campo);
        }

        RegistroEntradaDAO registroDAO = new RegistroEntradaDAO();
        registroDAO.actualizarRegistroEntrada(registro);
    }

    private void actualizarRegistroHora(TableColumn.CellEditEvent<RegistroEntrada, LocalTime> event, String campo) {
        RegistroEntrada registro = event.getRowValue();
        LocalTime nuevoValor = event.getNewValue();

        switch (campo) {
            case HORA_ENTRADA:
                registro.setHoraEntrada(nuevoValor);
                break;
            case HORA_SALIDA:
                registro.setHoraSalida(nuevoValor);
                break;
            default:
                throw new IllegalArgumentException(CAMPO_NO_VALIDO + campo);
        }

        RegistroEntradaDAO registroDAO = new RegistroEntradaDAO();
        registroDAO.actualizarRegistroEntrada(registro);
    }


    private void cargarDatosTabla(LocalDate fecha) {
        RegistroEntradaDAO registroDAO = new RegistroEntradaDAO();
        List<RegistroEntrada> entradasList = registroDAO.obtenerRegistrosEntradaPorFecha(fecha);
        List<RegistroEntrada> salidasList = registroDAO.obtenerRegistrosEntradaPorFecha(fecha).stream()
                .filter(registro -> registro.getHoraSalida() != null)
                .toList(); // Reemplazado con toList()

        ObservableList<RegistroEntrada> entradas = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(entradasList));
        ObservableList<RegistroEntrada> salidas = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(salidasList));

        tblEntradas.setItems(entradas);
        tblSalidas.setItems(salidas);

        actualizarContadores();
    }


    private void inicializarComboBox() {
        cmbCalendar.setValue(LocalDate.now());
        cmbCalendar.setPromptText("Selecciona una fecha");
        cmbCalendar.valueProperty().addListener((observable, oldValue, newValue) -> cargarDatosTabla(newValue));
    }

    private void configurarIconos() {
        MFXFontIcon iconoReporte = new MFXFontIcon("fas-print", 24, Color.WHITE);
        btnReporteRegistro.setGraphic(iconoReporte);
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

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

    private void actualizarContadores() {
        RegistroEntradaDAO registroDAO = new RegistroEntradaDAO();
        int totalSocios = registroDAO.obtenerTotalSocios();
        int sociosDentro = registroDAO.obtenerTotalSociosDentro();

        lblTotalSocios.setText("Total de Socios: " + totalSocios);
        lblSociosDentro.setText("Socios Dentro: " + sociosDentro);
    }

    @FXML
    public void handleImprimirReporteRegistro() {
        List<RegistroEntrada> registrosVisibles = new ArrayList<>(tblEntradas.getItems());

        List<Map<String, Object>> registrosMap = new ArrayList<>();
        for (RegistroEntrada registro : registrosVisibles) {
            Map<String, Object> map = new HashMap<>();
            map.put(NUMERO_SOCIO, String.valueOf(registro.getNumeroSocio().getNumeroSocio()));
            map.put(NOMBRE, registro.getNumeroSocio().getNombre());
            map.put(APELLIDOS, registro.getNumeroSocio().getApellidos());
            map.put(HORA_SALIDA, registro.getHoraSalida() != null ? registro.getHoraSalida().toString() : "Dentro");
            map.put("fecha", registro.getFecha());
            map.put(HORA_ENTRADA, registro.getHoraEntrada());
            registrosMap.add(map);
        }

        Map<String, Object> parametros = new HashMap<>();
        Reportes.imprimirReporteRegistros(registrosMap, parametros);
    }

    private void verificarNuevosRegistros() {
        LocalDate fechaSeleccionada = cmbCalendar.getValue();
        List<RegistroEntrada> registrosActuales = new RegistroEntradaDAO().obtenerRegistrosEntradaPorFecha(fechaSeleccionada);
        ObservableList<RegistroEntrada> entradas = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(registrosActuales));

        if (!entradas.equals(tblEntradas.getItems())) {
            tblEntradas.setItems(entradas);
            actualizarContadores();
        }

        List<RegistroEntrada> salidasFiltradas = registrosActuales.stream()
                .filter(registro -> registro.getHoraSalida() != null)
                .toList(); // Reemplazado con toList()
        ObservableList<RegistroEntrada> salidas = FXCollections.unmodifiableObservableList(FXCollections.observableArrayList(salidasFiltradas));

        if (!salidas.equals(tblSalidas.getItems())) {
            tblSalidas.setItems(salidas);
        }
    }
    private void iniciarPolling() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(5), event -> verificarNuevosRegistros()));
        timeline.setCycleCount(INDEFINITE);
        timeline.play();
    }
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            // Cargar el archivo FXML de la vista de ayuda
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_registro.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Registro");
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