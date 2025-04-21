package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXComboBox;
import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.mfxcomponents.controls.buttons.MFXIconButton;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.socialclub.socialclub.database.FamiliaDAO;
import org.socialclub.socialclub.database.SocioDAO;
import org.socialclub.socialclub.model.Familia;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.Reportes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para gestionar las operaciones relacionadas con los socios en la aplicación.
 * Proporciona funcionalidades para filtrar, mostrar detalles, imprimir carnets y reportes de socios.
 * También maneja la inicialización de componentes y la configuración de la interfaz de usuario.
 */
public class SocioController {
    private static final String NUMERO_SOCIO = "numeroSocio";
    private static final String NOMBRE = "nombre";
    private static final String APELLIDOS = "apellidos";

    @FXML
    public TextField txtNumSocio;
    @FXML
    public TextField txtNombre;
    @FXML
    public TextField txtApellidos;
    @FXML
    public TextField txtDNI;
    @FXML
    public TextField txtTelefono;
    @FXML
    public TextField txtEmail;
    @FXML
    public TextField txtNacimiento;
    @FXML
    public TextField txtFechaAlta;
    @FXML
    public TextField txtTitularidad;
    @FXML
    public ImageView imgPhoto;
    @FXML
    public MFXComboBox<String> cmbFiltros;
    @FXML
    public Pane containerFamilia;
    @FXML
    public MFXComboBox<String> cmbFamilias;
    @FXML
    public Pane containerRangoEdad;
    @FXML
    public ToggleGroup rangoEdad;
    @FXML
    public Pane containerBusqueda;
    @FXML
    public MFXTextField txtBusqueda;
    @FXML
    public MFXButton btnCarnets;
    @FXML
    public MFXButton btnReporte;
    @FXML
    public MFXIconButton helpIcon;
    @FXML
    private TableView<Socio> tablaSocio;
    @FXML
    private TableColumn<Socio, String> colNumSocio;
    @FXML
    private TableColumn<Socio, String> colNombre;
    @FXML
    private TableColumn<Socio, String> colApellidos;
    @FXML
    private ProgressIndicator progressIndicator;

    /**
     * Inicializa el controlador SocioController.
     * Configura los iconos, la tabla, los datos de la tabla, los ComboBox y las familias.
     * También agrega los ChangeListeners necesarios.
     */
    @FXML
    private void initialize() {
        configurarIconos();
        configurarTabla();
        cargarDatosTabla();
        inicializarComboBox();
        cargarFamilias();
        progressIndicator.setVisible(false);

        tablaSocio.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                mostrarDetallesSocio(newValue)
        );

        cmbFamilias.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
                cargarMiembrosFamilia(newValue)
        );

        rangoEdad.selectedToggleProperty().addListener((observable, oldValue, newValue) ->
                cargarSociosPorRangoEdad(newValue.getUserData().toString())
        );

        txtBusqueda.textProperty().addListener((observable, oldValue, newValue) ->
                filtrarSocios(newValue)
        );
    }

    /**
     * Filtra los socios según el texto ingresado en el campo de búsqueda.
     *
     * @param filtro El texto de filtro.
     */
    private void filtrarSocios(String filtro) {
        SocioDAO socioDAO = new SocioDAO();
        List<Socio> sociosList = socioDAO.obtenerTodosSocios();
        if (sociosList == null) {
            sociosList = new ArrayList<>();
        }

        List<Socio> sociosFiltrados = new ArrayList<>();
        for (Socio socio : sociosList) {
            if ((socio.getNombre() != null && socio.getNombre().toLowerCase().contains(filtro.toLowerCase())) ||
                    (socio.getApellidos() != null && socio.getApellidos().toLowerCase().contains(filtro.toLowerCase())) ||
                    (socio.getDni() != null && socio.getDni().toLowerCase().contains(filtro.toLowerCase())) ||
                    (socio.getTelefono() != null && socio.getTelefono().toLowerCase().contains(filtro.toLowerCase())) ||
                    (socio.getEmail() != null && socio.getEmail().toLowerCase().contains(filtro.toLowerCase())) ||
                    (socio.getNumeroSocio() != null && socio.getNumeroSocio().toLowerCase().contains(filtro.toLowerCase()))) {
                sociosFiltrados.add(socio);
            }
        }

        ObservableList<Socio> socios = FXCollections.observableArrayList(sociosFiltrados);
        tablaSocio.setItems(socios);
    }

    /**
     * Carga los socios según el rango de edad seleccionado.
     *
     * @param rangoEdad El rango de edad seleccionado.
     */
    private void cargarSociosPorRangoEdad(String rangoEdad) {
        SocioDAO socioDAO = new SocioDAO();
        List<Socio> sociosList = socioDAO.obtenerSociosPorRangoEdad(rangoEdad);
        if (sociosList == null) {
            sociosList = new ArrayList<>();
        }
        ObservableList<Socio> socios = FXCollections.observableArrayList(sociosList);
        tablaSocio.setItems(socios);
    }

    /**
     * Carga los miembros de la familia seleccionada.
     *
     * @param familiaSeleccionada La familia seleccionada.
     */
    private void cargarMiembrosFamilia(String familiaSeleccionada) {
        String[] partes = familiaSeleccionada.split(" - ");
        int idFamilia = Integer.parseInt(partes[0]);

        SocioDAO socioDAO = new SocioDAO();
        List<Socio> sociosList = socioDAO.obtenerSociosPorFamilia(idFamilia);
        if (sociosList == null) {
            sociosList = new ArrayList<>();
        }
        ObservableList<Socio> socios = FXCollections.observableArrayList(sociosList);
        tablaSocio.setItems(socios);
    }

    /**
     * Inicializa el ComboBox de filtros.
     */
    private void inicializarComboBox() {
        cmbFiltros.getItems().addAll("Todos", "Filtrar por familia", "Filtrar por rango de edad", "Filtrar por socio");

        cmbFiltros.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                switch (newValue.intValue()) {
                    case 1:
                        containerFamilia.setVisible(true);
                        containerRangoEdad.setVisible(false);
                        containerBusqueda.setVisible(false);
                        break;
                    case 2:
                        containerFamilia.setVisible(false);
                        containerRangoEdad.setVisible(true);
                        containerBusqueda.setVisible(false);
                        break;
                    case 3:
                        containerFamilia.setVisible(false);
                        containerRangoEdad.setVisible(false);
                        containerBusqueda.setVisible(true);
                        break;
                    default:
                        containerFamilia.setVisible(false);
                        containerRangoEdad.setVisible(false);
                        containerBusqueda.setVisible(false);
                        cargarDatosTabla();
                        break;
                }
            }
        });
    }

    /**
     * Configura la tabla de socios.
     */
    private void configurarTabla() {
        configurarColumna(colNumSocio, NUMERO_SOCIO);
        configurarColumna(colNombre, NOMBRE);
        configurarColumna(colApellidos, APELLIDOS);
        tablaSocio.setEditable(false);
    }

    /**
     * Configura una columna de la tabla.
     *
     * @param columna La columna a configurar.
     * @param campo   El campo de la columna.
     */
    private void configurarColumna(TableColumn<Socio, String> columna, String campo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(campo));
        columna.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Carga los datos de la tabla de socios.
     */
    private void cargarDatosTabla() {
        SocioDAO socioDAO = new SocioDAO();
        List<Socio> sociosList = socioDAO.obtenerTodosSocios();
        if (sociosList == null) {
            sociosList = new ArrayList<>();
        }
        ObservableList<Socio> socios = FXCollections.observableArrayList(sociosList);
        tablaSocio.setItems(socios);
    }

    /**
     * Muestra los detalles de un socio seleccionado.
     *
     * @param socio El socio seleccionado.
     */
    private void mostrarDetallesSocio(Socio socio) {
        if (socio == null) {
            // Limpiar los campos si no hay un socio seleccionado
            txtNumSocio.setText("");
            txtNombre.setText("");
            txtApellidos.setText("");
            txtDNI.setText("");
            txtTelefono.setText("");
            txtEmail.setText("");
            txtNacimiento.setText("");
            txtFechaAlta.setText("");
            txtTitularidad.setText("");
            imgPhoto.setImage(null);
            return;
        }

        txtNumSocio.setText(socio.getNumeroSocio());
        txtNombre.setText(socio.getNombre());
        txtApellidos.setText(socio.getApellidos());
        txtDNI.setText(socio.getDni());
        txtTelefono.setText(socio.getTelefono());
        txtEmail.setText(socio.getEmail());
        txtNacimiento.setText(socio.getFechaNacimiento().toString());
        txtFechaAlta.setText(socio.getFechaAlta().toString());
        txtTitularidad.setText(String.valueOf(socio.getTitularidad()));

        if (socio.getFoto() != null) {
            Image image = new Image(new ByteArrayInputStream(socio.getFoto()));
            imgPhoto.setImage(image);
        } else {
            InputStream imageStream = getClass().getResourceAsStream("/images/noimage.jpg");
            if (imageStream != null) {
                imgPhoto.setImage(new Image(imageStream));
            } else {
                LOGGER.severe("La imagen no se encuentra");
            }
        }
    }

    /**
     * Carga las familias en el ComboBox de familias.
     */
    private void cargarFamilias() {
        FamiliaDAO familiaDAO = new FamiliaDAO();
        List<Familia> familiasList = familiaDAO.obtenerTodasFamilias();
        cmbFamilias.getItems().clear();
        for (Familia familia : familiasList) {
            String displayText = String.format("%d - %s %s (%d miembros)",
                    familia.getId(),
                    familia.getNombreTitular(),
                    familia.getApellidosTitular(),
                    familia.getNumeroMiembros());
            cmbFamilias.getItems().add(displayText);
        }
    }

    /**
     * Maneja la acción de imprimir carnets.
     */
    public void handleImprimirCarnets() {
        progressIndicator.setVisible(true);
        List<Socio> socios = new ArrayList<>(tablaSocio.getItems());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Reportes.imprimirCarnets(socios);
                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
            }
        };

        new Thread(task).start();
    }

    /**
     * Configura los iconos de los botones.
     */
    private void configurarIconos() {
        MFXFontIcon iconTarjeta = new MFXFontIcon("fas-print", Color.WHITE);
        MFXFontIcon iconReporte = new MFXFontIcon("fas-print", Color.WHITE);
        btnCarnets.setGraphic(new MFXIconWrapper(iconTarjeta, 24));
        btnReporte.setGraphic(new MFXIconWrapper(iconReporte, 24));

        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Maneja la acción de imprimir un reporte de socios.
     *
     * @param mouseEvent El evento de clic del ratón.
     */
    @FXML
    public void handleImprimirReporte(MouseEvent mouseEvent) {
        progressIndicator.setVisible(true);
        List<Socio> socios = new ArrayList<>(tablaSocio.getItems());

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                Reportes.imprimirReporteSocios(socios);
                return null;
            }

            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
            }

            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
            }
        };

        new Thread(task).start();
    }

    /**
     * Maneja el evento de clic en el ícono de ayuda.
     * Abre una ventana modal que muestra el contenido de ayuda relacionado con los socios.
     *
     * @param event El evento de ratón que desencadena el clic.
     */
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            // Cargar el archivo FXML de la vista de ayuda
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_socios.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Socios");
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