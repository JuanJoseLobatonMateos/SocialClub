package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.*;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.converter.LocalDateStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.database.SocioDAO;
import org.socialclub.socialclub.model.Familia;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.*;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Integer.parseInt;
import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para la vista de agregar socios.
 */
public class AddSocioController {
    private static final String NUMERO_SOCIO = "numeroSocio";
    private static final String NOMBRE = "nombre";
    private static final String APELLIDOS = "apellidos";
    private static final String TELEFONO = "telefono";
    private static final String EMAIL = "email";
    private static final String FECHA_NACIMIENTO = "fechaNacimiento";
    private static final String DNI = "dni";

    @FXML
    public MFXButton btnCarnets;
    @FXML
    public MFXIconButton helpIcon;
    @FXML
    private MFXComboBox<String> cmbTitularidad;
    @FXML
    private MFXComboBox<String> txtNumFamilia;
    @FXML
    private Label lblNumFamiliaError;
    @FXML
    private MFXTextField txtNombre;
    @FXML
    private Label lblNombreError;
    @FXML
    private MFXTextField txtApellidos;
    @FXML
    private Label lblApellidosError;
    @FXML
    private MFXTextField txtDni;
    @FXML
    private Label lblDniError;
    @FXML
    private MFXTextField txtTelefono;
    @FXML
    private Label lblTelefonoError;
    @FXML
    private MFXDatePicker txtFechaNacimiento;
    @FXML
    private Label lblFechaError;
    @FXML
    private MFXTextField txtEmail;
    @FXML
    private Label lblEmailError;
    @FXML
    private ImageView imgPhoto;
    @FXML
    private MFXButton btnFile;
    @FXML
    private MFXButton btnCamara;
    @FXML
    private Label lblImagenError;
    @FXML
    private ImageView imgHuella;
    @FXML
    private MFXButton btnHuella;
    @FXML
    private Label lblHuellaError;
    @FXML
    private TableView<Socio> tablaSocio;
    @FXML
    private TableColumn<Socio, String> colNumSocio;
    @FXML
    private TableColumn<Socio, String> colNombre;
    @FXML
    private TableColumn<Socio, String> colApellidos;
    @FXML
    private TableColumn<Socio, String> colDni;
    @FXML
    private TableColumn<Socio, LocalDate> colFechaNacimiento;
    @FXML
    private TableColumn<Socio, String> colTelefono;
    @FXML
    private TableColumn<Socio, String> colEmail;
    @FXML
    private MFXButton btnGuardar;

    private static final Logger logger = LoggerFactory.getLogger(AddSocioController.class);
    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Inicializa el controlador.
     */
    @FXML
    private void initialize() {
        configurarIconos();
        btnGuardar.setOnAction(e -> {
            try {
                guardarSocio();
            } catch (Exception ex) {
                logger.error("Error al guardar el socio: {}", ex.getMessage());
            }
        });
        configurarTabla();
        cargarDatosTabla();
        configurarContextMenu();
        configurarComboBox();
    }

    /**
     * Configura los ComboBox de la interfaz.
     */
    private void configurarComboBox() {
        ObservableList<String> numFamilias = FXCollections.observableArrayList();
        for (int i = 1; i <= 300; i++) {
            numFamilias.add(String.valueOf(i));
        }
        txtNumFamilia.setItems(numFamilias);

        ObservableList<String> titularidades = FXCollections.observableArrayList("NORMAL", "TITULAR", "COTITULAR");
        cmbTitularidad.setItems(titularidades);
    }

    /**
     * Configura los iconos de los botones.
     */
    private void configurarIconos() {
        MFXFontIcon iconCamara = new MFXFontIcon("fas-camera", 24, Color.WHITE);
        MFXFontIcon iconGuardar = new MFXFontIcon("fas-floppy-disk", 24, Color.WHITE);
        MFXFontIcon iconHuella = new MFXFontIcon("fas-fingerprint", 24, Color.WHITE);
        MFXFontIcon iconFile = new MFXFontIcon("fas-file-image", 24, Color.WHITE);
        btnCamara.setGraphic(new MFXIconWrapper(iconCamara, 24));
        btnGuardar.setGraphic(new MFXIconWrapper(iconGuardar, 24));
        btnHuella.setGraphic(new MFXIconWrapper(iconHuella, 24));
        btnFile.setGraphic(new MFXIconWrapper(iconFile, 24));
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Configura la tabla de socios.
     */
    private void configurarTabla() {
        configurarColumna(colNumSocio, NUMERO_SOCIO);
        configurarColumna(colNombre, NOMBRE);
        configurarColumna(colApellidos, APELLIDOS);
        configurarColumna(colDni, DNI);
        configurarColumna(colTelefono, TELEFONO);
        configurarColumna(colEmail, EMAIL);
        configurarColumnaFecha(colFechaNacimiento);
        tablaSocio.setEditable(true);
    }

    /**
     * Configura una columna de la tabla.
     *
     * @param columna La columna a configurar.
     * @param campo   El campo de la entidad Socio.
     */
    private void configurarColumna(TableColumn<Socio, String> columna, String campo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(campo));
        columna.setCellFactory(TextFieldTableCell.forTableColumn());
        columna.setOnEditCommit(event -> actualizarSocio(event, campo));
    }

    /**
     * Configura una columna de fecha de la tabla.
     *
     * @param columna La columna a configurar.
     */
    private void configurarColumnaFecha(TableColumn<Socio, LocalDate> columna) {
        columna.setCellValueFactory(new PropertyValueFactory<>(AddSocioController.FECHA_NACIMIENTO));
        columna.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        columna.setOnEditCommit(event -> actualizarSocio(event, AddSocioController.FECHA_NACIMIENTO));
    }

    /**
     * Actualiza un socio en la base de datos.
     *
     * @param event El evento de edición de la celda.
     * @param campo El campo a actualizar.
     */
    private void actualizarSocio(TableColumn.CellEditEvent<Socio, ?> event, String campo) {
        Socio socio = event.getRowValue();
        Object nuevoValor = event.getNewValue();

        DialogoController.showConfirmDialog(
                (Stage) tablaSocio.getScene().getWindow(),
                "¿Desea actualizar el campo " + campo + " a " + nuevoValor + "?",
                e -> {
                    switch (campo) {
                        case NOMBRE:
                            socio.setNombre((String) nuevoValor);
                            break;
                        case APELLIDOS:
                            socio.setApellidos((String) nuevoValor);
                            break;
                        case DNI:
                            socio.setDni((String) nuevoValor);
                            break;
                        case TELEFONO:
                            socio.setTelefono((String) nuevoValor);
                            break;
                        case EMAIL:
                            socio.setEmail((String) nuevoValor);
                            break;
                        case FECHA_NACIMIENTO:
                            socio.setFechaNacimiento((LocalDate) nuevoValor);
                            break;
                        default:
                            throw new IllegalArgumentException("Campo no válido: " + campo);
                    }

                    SocioDAO socioDAO = new SocioDAO();
                    socioDAO.actualizarSocio(socio);
                }
        );
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
     * Guarda un socio en la base de datos.
     */
    private void guardarSocio() throws IllegalArgumentException, IOException, SQLException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        Familia numFamilia = new Familia();
        String numFamiliaStr = txtNumFamilia.getText();
        if (!validarNumFamilia(numFamiliaStr)) return;
        numFamilia.setId(parseInt(numFamiliaStr));

        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String dni = txtDni.getText();
        String telefono = txtTelefono.getText();
        LocalDate fechaNacimiento = txtFechaNacimiento.getValue();
        String email = txtEmail.getText();
        String titularidad = cmbTitularidad.getValue() != null ? cmbTitularidad.getValue() : "NORMAL";
        if (fechaNacimiento == null) {
            lblFechaError.setText("La fecha de nacimiento no puede estar vacía");
            return;
        }
        if (!validarCampos(nombre, apellidos, dni, telefono, fechaNacimiento, email)) return;
        if (!validarImagenYHuella()) return;

        SerialBlob imagen = obtenerImagen();
        SerialBlob huella = obtenerHuella();

        DialogoController.showConfirmDialog(
                (Stage) tablaSocio.getScene().getWindow(),
                "¿Desea guardar este Socio?",
                e -> guardarSocioEnBaseDeDatos(empleadoDAO, numFamilia, nombre, apellidos, dni, telefono, fechaNacimiento, email, titularidad, imagen, huella)
        );
    }

    /**
     * Valida el número de familia.
     *
     * @param numFamiliaStr El número de familia en formato String.
     * @return true si el número de familia es válido, false en caso contrario.
     */
    private boolean validarNumFamilia(String numFamiliaStr) {
        if (numFamiliaStr == null || numFamiliaStr.isEmpty()) {
            lblNumFamiliaError.setText("Debe seleccionar un número de familia");
            return false;
        }
        lblNumFamiliaError.setText("");
        return true;
    }

    /**
     * Valida los campos del formulario.
     *
     * @param nombre          El nombre del socio.
     * @param apellidos       Los apellidos del socio.
     * @param dni             El DNI del socio.
     * @param telefono        El teléfono del socio.
     * @param fechaNacimiento La fecha de nacimiento del socio.
     * @param email           El email del socio.
     * @return true si todos los campos son válidos, false en caso contrario.
     */
    private boolean validarCampos(String nombre, String apellidos, String dni, String telefono, LocalDate fechaNacimiento, String email) {
        boolean valid = true;
        lblNombreError.setText("");
        lblApellidosError.setText("");
        lblDniError.setText("");
        lblTelefonoError.setText("");
        lblFechaError.setText("");
        lblEmailError.setText("");

        String error = Validaciones.validarNombre(nombre);
        if (error != null) {
            lblNombreError.setText(error);
            valid = false;
        }

        error = Validaciones.validarApellidos(apellidos);
        if (error != null) {
            lblApellidosError.setText(error);
            valid = false;
        }

        boolean esMenorDeEdad = Period.between(fechaNacimiento, LocalDate.now()).getYears() < 18;
        error = Validaciones.validarDniSocio(dni, esMenorDeEdad);
        if (error != null) {
            lblDniError.setText(error);
            valid = false;
        }

        error = Validaciones.validarTelefono(telefono);
        if (error != null) {
            lblTelefonoError.setText(error);
            valid = false;
        }

        error = Validaciones.validarFechaNacimientoSocio(fechaNacimiento);
        if (error != null) {
            lblFechaError.setText(error);
            valid = false;
        }

        error = Validaciones.validarEmail(email);
        if (error != null) {
            lblEmailError.setText(error);
            valid = false;
        }

        return valid;
    }

    /**
     * Valida la imagen y la huella del socio.
     *
     * @return true si la imagen y la huella son válidas, false en caso contrario.
     */
    private boolean validarImagenYHuella() {
        boolean valid = true;
        lblImagenError.setText("");
        lblHuellaError.setText("");

        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResource("/images/noimage.jpg")).toString());
        Image img = imgPhoto.getImage();
        if (img == null || (img.getUrl() != null && img.getUrl().equals(defaultImage.getUrl()))) {
            lblImagenError.setText("Debe añadir una imagen");
            valid = false;
        }

        if (imgHuella.getImage() == null) {
            lblHuellaError.setText("Debe capturar una huella");
            valid = false;
        }

        return valid;
    }

    /**
     * Obtiene la imagen del socio.
     *
     * @return La imagen del socio en formato SerialBlob.
     * @throws IOException  Si ocurre un error al leer la imagen.
     * @throws SQLException Si ocurre un error al convertir la imagen a SerialBlob.
     */
    private SerialBlob obtenerImagen() throws IOException, SQLException {
        Image image = imgPhoto.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] fileContent = baos.toByteArray();
        return new SerialBlob(fileContent);
    }


 /**
           * Obtiene la huella desde un archivo y la convierte en un `SerialBlob`.
           * Si el archivo existe, lee su contenido, intenta eliminar el archivo y retorna un `SerialBlob` con los datos leídos.
           * Si el archivo no existe, retorna `null`.
           *
           * @return Un `SerialBlob` con los datos de la huella si el archivo existe, o `null` si no existe.
           * @throws IOException Si ocurre un error al leer el archivo o al convertir los datos a `SerialBlob`.
           * @throws SQLException Si ocurre un error al crear el `SerialBlob`.
           */
          private SerialBlob obtenerHuella() throws IOException, SQLException {
              Path huellaPath = Paths.get("huellaTemplate.dat");
              if (Files.exists(huellaPath)) {
                  byte[] huellaBytes = Files.readAllBytes(huellaPath);
                  try {
                      Files.delete(huellaPath); // Intentar borrar el archivo
                  } catch (IOException e) {
                      logger.error("No se pudo eliminar el archivo de huella: {}", huellaPath.toAbsolutePath(), e);
                  }
                  return new SerialBlob(huellaBytes);
              } else {
                  return null; // Retornar null si el archivo de huella no existe
              }
          }

    /**
     * Guarda un socio en la base de datos.
     *
     * @param empleadoDAO     El DAO de empleados.
     * @param numFamilia      La familia del socio.
     * @param nombre          El nombre del socio.
     * @param apellidos       Los apellidos del socio.
     * @param dni             El DNI del socio.
     * @param telefono        El teléfono del socio.
     * @param fechaNacimiento La fecha de nacimiento del socio.
     * @param email           El email del socio.
     * @param titularidad     La titularidad del socio.
     * @param imagen          La imagen del socio.
     * @param huella          La huella del socio.
     */
    private void guardarSocioEnBaseDeDatos(EmpleadoDAO empleadoDAO, Familia numFamilia, String nombre, String apellidos, String dni, String telefono, LocalDate fechaNacimiento, String email, String titularidad, SerialBlob imagen, SerialBlob huella) {
        try {
            SocioDAO socioDAO = new SocioDAO();
            String password = PasswordGenerator.generateRandomPassword();
            socioDAO.guardarSocio(numFamilia, nombre, apellidos, telefono, dni, email, fechaNacimiento.toString(), imagen, huella, Socio.Titularidad.valueOf(titularidad), (byte) 0, LocalDate.now().toString(), empleadoDAO.obtenerEmpleadoPorId(1), EncriptadorUtil.hashPassword(password));
            EmailUtils.sendEmail(email, password);
            cargarDatosTabla();
            reiniciarInputs();
        } catch (Exception ex) {
            logger.error("Error al guardar el socio en la base de datos: {}", ex.getMessage());
        }
    }
    /**
     * Maneja la captura de huella.
     */
    @FXML
    public void handleCapturaHuella() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/huella.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Huella");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            Image huellaImage = HuellaController.getHuellaImage();
            if (huellaImage != null) {
                imgHuella.setImage(huellaImage);
            }
        } catch (IOException ex) {
            logger.error("Error al cargar la ventana de captura de huella: {}", ex.getMessage());
        }
    }

    /**
     * Configura el menú contextual de la tabla de socios.
     */
    private void configurarContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        contextMenu.getItems().add(crearMenuItemEliminar());

        tablaSocio.setRowFactory(tv -> {
            TableRow<Socio> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });
            return row;
        });
    }

    /**
     * Crea un menú de contexto para eliminar un socio.
     *
     * @return El menú de contexto para eliminar un socio.
     */
    private MenuItem crearMenuItemEliminar() {
        MenuItem deleteItem = new MenuItem("Eliminar Socio");
        deleteItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");

        deleteItem.setOnAction(event -> {
            Socio socioSeleccionado = tablaSocio.getSelectionModel().getSelectedItem();
            if (socioSeleccionado != null) {
                DialogoController.showConfirmDialog(
                        (Stage) tablaSocio.getScene().getWindow(),
                        "¿Desea eliminar este socio?",
                        e -> eliminarSocio(socioSeleccionado)
                );
            }
        });
        return deleteItem;
    }

    /**
     * Reinicia los campos de entrada del formulario.
     */
    private void reiniciarInputs() {
        txtNumFamilia.setValue(null);
        txtNombre.clear();
        txtApellidos.clear();
        txtDni.clear();
        txtTelefono.clear();
        txtFechaNacimiento.setValue(null);
        txtEmail.clear();
        cmbTitularidad.setValue(null);
        imgPhoto.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/noimage.jpg")).toString()));
        imgHuella.setImage(new Image(Objects.requireNonNull(getClass().getResource("/images/nohuella.jpg")).toString()));
        lblNombreError.setText("");
        lblApellidosError.setText("");
        lblDniError.setText("");
        lblTelefonoError.setText("");
        lblFechaError.setText("");
        lblEmailError.setText("");
        lblImagenError.setText("");
        lblHuellaError.setText("");
    }

    /**
     * Elimina un socio de la base de datos y actualiza la tabla.
     *
     * @param socio El socio a eliminar.
     */
    private void eliminarSocio(Socio socio) {
        SocioDAO socioDAO = new SocioDAO();
        socioDAO.eliminarSocio(socio.getNumeroSocio());
        cargarDatosTabla();
    }

    /**
     * Maneja la captura de imagen desde la cámara.
     */
    @FXML
    public void handleCapturaImagen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/camara.fxml"));
            Parent root = loader.load();

            Stage primaryStage = (Stage) btnCamara.getScene().getWindow();
            Scene primaryScene = primaryStage.getScene();
            GaussianBlur blur = new GaussianBlur(10);
            primaryScene.getRoot().setEffect(blur);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.initStyle(StageStyle.TRANSPARENT);
            stage.initOwner(primaryStage);

            root.setOnMousePressed(event -> {
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            });
            root.setOnMouseDragged(event -> {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            });

            primaryScene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                if (!stage.isFocused()) {
                    stage.close();
                    CameraController controller = loader.getController();
                    controller.releaseCamera();
                    primaryScene.getRoot().setEffect(null);
                }
            });

            stage.showAndWait();
            primaryScene.getRoot().setEffect(null);

            CameraController controller = loader.getController();
            Image capturedImage = controller.getCapturedImage();

            if (capturedImage != null) {
                imgPhoto.setImage(capturedImage);
            }
        } catch (IOException e) {
            logger.error("Error al cargar la ventana de captura de imagen: {}", e.getMessage());
        }
    }

    /**
     * Maneja la carga de una imagen desde el sistema de archivos.
     *
     * @param mouseEvent El evento de clic del ratón.
     */
    @FXML
    public void handleUploadImagen(MouseEvent mouseEvent) {
        ImageUtils.handleUploadImagen(mouseEvent, imgPhoto);
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
            var resource = getClass().getResource("/help/help_add_socio.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda para añadir socio");
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
