package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXButton;
import io.github.palexdev.materialfx.controls.MFXDatePicker;
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
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.util.EncriptadorUtil;
import org.socialclub.socialclub.util.ImageUtils;
import org.socialclub.socialclub.util.Reportes;
import org.socialclub.socialclub.util.Validaciones;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Objects;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para la vista de agregar empleados.
 * Maneja la lógica de negocio y la interacción con la interfaz de usuario.
 */
public class AddEmpleadoController {

    private static final String NOMBRE = "nombre";
    private static final String APELLIDOS = "apellidos";
    private static final String TELEFONO = "telefono";
    private static final String EMAIL = "email";
    private static final String DOMICILIO = "domicilio";
    private static final String FECHA_NACIMIENTO = "fechaNacimiento";
    private static final String DNI = "dni";

    @FXML
    public Label lblPasswordError;
    @FXML
    public Label lblImagenError;
    @FXML
    public MFXButton btnReporte;
    @FXML
    public MFXButton btnUploadImagen;
    @FXML
    public MFXIconButton helpIcon;
    @FXML
    private MFXButton btnCamara;
    @FXML
    private MFXButton btnGuardar;
    @FXML
    private ImageView imgPhoto;
    @FXML
    private TableView<Empleado> tabla;
    @FXML
    private TableColumn<Empleado, String> colNombre;
    @FXML
    private TableColumn<Empleado, String> colApellidos;
    @FXML
    private TableColumn<Empleado, String> colDni;
    @FXML
    private TableColumn<Empleado, String> colTelefono;
    @FXML
    private TableColumn<Empleado, String> colEmail;
    @FXML
    private TableColumn<Empleado, String> colDomicilio;
    @FXML
    private TableColumn<Empleado, LocalDate> colFechaNacimiento;
    @FXML
    private MFXTextField txtApellidos;
    @FXML
    private MFXTextField txtDni;
    @FXML
    private MFXTextField txtDomicilio;
    @FXML
    private MFXTextField txtEmail;
    @FXML
    private MFXDatePicker txtFecha;
    @FXML
    private MFXTextField txtNombre;
    @FXML
    private MFXTextField txtPassword;
    @FXML
    private MFXTextField txtTelefono;
    @FXML
    private Label lblNombreError;
    @FXML
    private Label lblApellidosError;
    @FXML
    private Label lblDniError;
    @FXML
    private Label lblDomicilioError;
    @FXML
    private Label lblTelefonoError;
    @FXML
    private Label lblFechaError;
    @FXML
    private Label lblEmailError;

    private double xOffset = 0;
    private double yOffset = 0;

    /**
     * Inicializa el controlador AddEmpleadoController configurando iconos, estableciendo controladores de eventos,
     * cargando datos en la tabla y configurando el menú contextual.
     */
    @FXML
    private void initialize() {
        configurarIconos();
        btnGuardar.setOnAction(e -> {
            try {
                guardarEmpleado();
            } catch (Exception ex) {
                LOGGER.severe("Error al guardar el empleado: " + ex.getMessage());
            }
        });
        configurarTabla();
        cargarDatosTabla();
        configurarContextMenu();
    }

    /**
     * Configura las columnas de la vista de tabla con los adecuados generadores de celdas y controladores de eventos.
     *
     * <p>Este método establece las columnas de la tabla para mostrar los campos correspondientes de los objetos
     * {@link Empleado}. Utiliza los métodos {@link #configurarColumna(TableColumn, String)} y
     * {@link #configurarColumnaFecha(TableColumn)} para establecer las columnas con generadores de celdas y
     * controladores de eventos.
     *
     * <p>El método {@link #configurarColumna(TableColumn, String)} se utiliza para establecer las columnas que
     * muestran campos de cadena de los objetos {@link Empleado}. Establece los generadores de celdas para mostrar
     * los campos en campos de texto editables y establece el controlador de eventos para actualizar los campos
     * correspondientes cuando se editan los valores de las celdas.
     *
     * <p>El método {@link #configurarColumnaFecha(TableColumn)} se utiliza para establecer la columna que
     * muestra el campo fech de nacimiento. Establece los generadores de celdas para mostrar
     * la fecha en un campo de texto y establece el controlador de eventos para actualizar el campo correspondiente
     * cuando se editan los valores de las celdas.
     */
    private void configurarColumnas() {
        configurarColumna(colNombre, NOMBRE);
        configurarColumna(colApellidos, APELLIDOS);
        configurarColumna(colDni, DNI);
        configurarColumna(colTelefono, TELEFONO);
        configurarColumna(colEmail, EMAIL);
        configurarColumna(colDomicilio, DOMICILIO);
        configurarColumnaFecha(colFechaNacimiento);
    }

    /**
     * Configura la vista de tabla con generadores de celdas adecuados y controladores de eventos.
     *
     * <p>Este método establece las columnas de la tabla para mostrar los campos correspondientes de los objetos
     * {@link Empleado}. Utiliza los métodos {@link #configurarColumna(TableColumn, String)} y
     * {@link #configurarColumnaFecha(TableColumn)} para establecer las columnas con generadores de celdas y
     * controladores de eventos.
     *
     * <p>El método {@link #configurarColumna(TableColumn, String)} se utiliza para establecer las columnas que
     * muestran campos de cadena de los objetos {@link Empleado}. Establece los generadores de celdas para mostrar
     * los campos en campos de texto editables y establece el controlador de eventos para actualizar los campos
     * correspondientes cuando se editan los valores de las celdas.
     *
     * <p>El método {@link #configurarColumnaFecha(TableColumn)} se utiliza para establecer la columna que
     * muestra el campo fecha de nacimiento. Establece los generadores de celdas para mostrar
     * la fecha en un campo de texto y establece el controlador de eventos para actualizar el campo correspondiente
     * cuando se editan los valores de las celdas.
     */
    private void configurarTabla() {
        configurarColumnas();
        tabla.setEditable(true);
    }


    /**
     * Configura una columna de tabla para mostrar datos de cadena de los objetos {@link Empleado}.
     * Establece el generador de valores de celda, el generador de celdas y el controlador de confirmación de edición para la columna.
     *
     * @param columna La columna de tabla que se va a configurar.
     * @param campo   El nombre del campo en la clase {@link Empleado} que se mostrará en la columna.
     */
    private void configurarColumna(TableColumn<Empleado, String> columna, String campo) {
        columna.setCellValueFactory(new PropertyValueFactory<>(campo));
        columna.setCellFactory(TextFieldTableCell.forTableColumn());
        columna.setOnEditCommit(event -> actualizarEmpleado(event, campo));
    }

    /**
     * Configura una columna de tabla para mostrar datos de fecha de los objetos {@link Empleado}.
     * Establece el generador de valores de celda, el generador de celdas y el controlador de confirmación de edición para la columna.
     *
     * @param columna La columna de tabla que se va a configurar.
     */
    private void configurarColumnaFecha(TableColumn<Empleado, LocalDate> columna) {
        columna.setCellValueFactory(new PropertyValueFactory<>(AddEmpleadoController.FECHA_NACIMIENTO));
        columna.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
        columna.setOnEditCommit(event -> actualizarEmpleado(event, AddEmpleadoController.FECHA_NACIMIENTO));
    }

    /**
     * Actualiza el campo especificado del empleado con el nuevo valor.
     *
     * @param event El evento que desencadenó la actualización.
     * @param campo El nombre del campo que se va a actualizar.
     * @throws IllegalArgumentException Si el campo especificado no es válido.
     */
    private void actualizarEmpleado(TableColumn.CellEditEvent<Empleado, ?> event, String campo) {
        Empleado empleado = event.getRowValue();
        Object nuevoValor = event.getNewValue();

        DialogoController.showConfirmDialog(
                (Stage) tabla.getScene().getWindow(),
                "¿Desea actualizar el campo " + campo + " a " + nuevoValor + "?",
                e -> {
                    switch (campo) {
                        case NOMBRE:
                            empleado.setNombre((String) nuevoValor);
                            break;
                        case APELLIDOS:
                            empleado.setApellidos((String) nuevoValor);
                            break;
                        case DNI:
                            empleado.setDni((String) nuevoValor);
                            break;
                        case TELEFONO:
                            empleado.setTelefono((String) nuevoValor);
                            break;
                        case EMAIL:
                            empleado.setEmail((String) nuevoValor);
                            break;
                        case DOMICILIO:
                            empleado.setDomicilio((String) nuevoValor);
                            break;
                        case FECHA_NACIMIENTO:
                            empleado.setFechaNacimiento((LocalDate) nuevoValor);
                            break;
                        default:
                            throw new IllegalArgumentException("Campo no válido: " + campo);
                    }

                    EmpleadoDAO empleadoDAO = new EmpleadoDAO();
                    empleadoDAO.actualizarEmpleado(empleado);
                }
        );
    }

    /**
     * Carga datos de la base de datos en la vista de tabla.
     *
     * <p>Esta función recupera todos los registros de empleados de la base de datos utilizando la clase {@link EmpleadoDAO}.
     * Luego crea una lista observable de objetos {@link Empleado} y establece esta lista como los elementos para la vista de tabla.
     *
     * @see EmpleadoDAO#obtenerTodosEmpleados()
     * @see javafx.collections.FXCollections#observableArrayList(java.util.Collection)
     */
    private void cargarDatosTabla() {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        ObservableList<Empleado> empleados = FXCollections.observableArrayList(empleadoDAO.obtenerTodosEmpleados());
        tabla.setItems(empleados);
    }

    /**
     * Valida los campos de entrada para los datos del empleado.
     *
     * @return true si todos los campos son válidos, false en caso contrario.
     * @throws IllegalArgumentException si algún campo no es válido.
     */
    private boolean validarCampos() {
        boolean valid = true;
        String error;

        error = Validaciones.validarNombre(txtNombre.getText());
        if (error != null) {
            lblNombreError.setText(error);
            valid = false;
        }

        error = Validaciones.validarApellidos(txtApellidos.getText());
        if (error != null) {
            lblApellidosError.setText(error);
            valid = false;
        }

        error = Validaciones.validarDniEmpleado(txtDni.getText());
        if (error != null) {
            lblDniError.setText(error);
            valid = false;
        }

        error = Validaciones.validarTelefono(txtTelefono.getText());
        if (error != null) {
            lblTelefonoError.setText(error);
            valid = false;
        }

        error = Validaciones.validarDireccion(txtDomicilio.getText());
        if (error != null) {
            lblDomicilioError.setText(error);
            valid = false;
        }

        error = Validaciones.validarFechaNacimientoEmpleado(txtFecha.getValue());
        if (error != null) {
            lblFechaError.setText(error);
            valid = false;
        }

        error = Validaciones.validarEmail(txtEmail.getText());
        if (error != null) {
            lblEmailError.setText(error);
            valid = false;
        }

        error = Validaciones.validarPassword(txtPassword.getText());
        if (error != null) {
            lblPasswordError.setText(error);
            valid = false;
        }

        Image defaultImage = new Image(Objects.requireNonNull(getClass().getResource("/images/noimage.jpg")).toString());
        Image img = imgPhoto.getImage();
        if (img == null || (img.getUrl() != null && img.getUrl().equals(defaultImage.getUrl()))) {
            lblImagenError.setText("Debe añadir una imagen");
            valid = false;
        }

        return valid;
    }

    /**
     * Restablece todos los mensajes de error de etiquetas a una cadena vacía.
     * Este método borra los mensajes de error mostrados en la interfaz de usuario
     * para indicar que todos los campos de entrada son válidos.
     */
    private void resetearMensajesError() {
        lblNombreError.setText("");
        lblApellidosError.setText("");
        lblDniError.setText("");
        lblDomicilioError.setText("");
        lblTelefonoError.setText("");
        lblFechaError.setText("");
        lblEmailError.setText("");
        lblPasswordError.setText("");
        lblImagenError.setText("");
    }

    /**
     * Guarda los datos de un empleado en la base de datos.
     *
     * @throws IllegalArgumentException si algún campo no es válido.
     * @throws IOException              si hay un error al leer la imagen.
     * @throws SQLException             si hay un error al interactuar con la base de datos.
     */
    public void guardarEmpleado() throws IllegalArgumentException, IOException, SQLException {
        EmpleadoDAO empleadoDAO = new EmpleadoDAO();

        // Restablece los mensajes de error
        resetearMensajesError();

        // Valida los campos de entrada
        if (!validarCampos()) {
            return;
        }

        // Obtiene los valores de los campos de entrada
        String nombre = txtNombre.getText();
        String apellidos = txtApellidos.getText();
        String dni = txtDni.getText();
        String domicilio = txtDomicilio.getText();
        String telefono = txtTelefono.getText();
        LocalDate fechaNacimiento = txtFecha.getValue();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        // Hashea la contraseña
        String hashedPassword = EncriptadorUtil.hashPassword(password);

        // Obtiene la imagen seleccionada
        Image image = imgPhoto.getImage();
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", baos);
        byte[] fileContent = baos.toByteArray();
        SerialBlob imagen = new SerialBlob(fileContent);

        // Muestra un diálogo de confirmación al usuario antes de guardar los datos
        DialogoController.showConfirmDialog(
                (Stage) tabla.getScene().getWindow(),
                "¿Desea guardar este empleado?",
                e -> {
                    // Guarda los datos del empleado en la base de datos
                    empleadoDAO.guardarEmpleado(
                            nombre, apellidos, dni, domicilio, telefono,
                            fechaNacimiento.toString(), email, hashedPassword, imagen
                    );
                    // Actualiza la vista de tabla para mostrar los datos actualizados
                    cargarDatosTabla();
                }
        );
    }

    /**
     * Configura un ícono para el botón dado utilizando el nombre de ícono especificado.
     *
     * @param boton El botón al que se va a configurar el ícono.
     * @param icono El nombre del ícono que se va a utilizar.
     *              El ícono se crea utilizando la clase MFXFontIcon con el nombre de ícono, tamaño y color especificados.
     *              Luego, el ícono se establece como el gráfico para el botón dado.
     */
    private void configurarIcono(MFXButton boton, String icono) {
        MFXFontIcon fontIcon = new MFXFontIcon(icono, 24, Color.WHITE);
        boton.setGraphic(new MFXIconWrapper(fontIcon, 24));
    }

    /**
     * Los íconos se crean utilizando la clase MFXFontIcon con los nombres de ícono especificados, tamaño y color.
     * Luego, los íconos se establecen como los gráficos para los botones dados.
     */
    private void configurarIconos() {
        configurarIcono(btnCamara, "fas-camera");
        configurarIcono(btnGuardar, "fas-floppy-disk");
        configurarIcono(btnReporte, "fas-print");
        configurarIcono(btnUploadImagen, "fas-file-image");
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Configura un menú contextual para la vista de tabla.
     * El menú contextual contiene un elemento para eliminar un empleado.
     * Cuando se hace clic con el botón derecho en una fila de la vista de tabla, se muestra el menú contextual.
     */
    private void configurarContextMenu() {
        // Crea un nuevo menú contextual
        ContextMenu contextMenu = new ContextMenu();

        // Añade un elemento para eliminar un empleado al menú contextual
        contextMenu.getItems().add(crearMenuItemEliminar());

        // Establece el generador de filas para la vista de tabla
        tabla.setRowFactory(tv -> {
            // Crea una nueva fila de tabla
            TableRow<Empleado> row = new TableRow<>();

            // Cuando se hace clic en una fila de la vista de tabla
            row.setOnMouseClicked(event -> {
                // Si se hace clic con el botón derecho y la fila no está vacía
                if (event.getButton() == MouseButton.SECONDARY && !row.isEmpty()) {
                    // Muestra el menú contextual en la posición donde se hizo clic
                    contextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
            });

            // Devuelve la fila de tabla
            return row;
        });
    }

    /**
     * Crea un elemento de menú para eliminar un empleado.
     *
     * @return Un objeto MenuItem con la etiqueta "Eliminar Empleado".
     * Cuando se selecciona este elemento de menú, muestra un diálogo de confirmación al usuario.
     * Si el usuario confirma la eliminación, llama al método {@link #eliminarEmpleado(Empleado)}.
     */
    private MenuItem crearMenuItemEliminar() {
        MenuItem deleteItem = new MenuItem("Eliminar Empleado");
        deleteItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");
        deleteItem.setOnAction(event -> {
            Empleado empleadoSeleccionado = tabla.getSelectionModel().getSelectedItem();
            if (empleadoSeleccionado != null) {
                DialogoController.showConfirmDialog(
                        (Stage) tabla.getScene().getWindow(),
                        "¿Desea eliminar este empleado?",
                        e -> eliminarEmpleado(empleadoSeleccionado)
                );
            }
        });
        return deleteItem;
    }


    /**
     * Elimina un empleado de la base de datos si no es administrador.
     *
     * @param empleado El empleado que se va a eliminar.
     * Si el empleado tiene rol de administrador, muestra un mensaje de error y no permite la eliminación.
     * De lo contrario, elimina al empleado de la base de datos y actualiza la vista.
     */
    public void eliminarEmpleado(Empleado empleado) {
        // Verificar si es administrador (puedes ajustar esta lógica según cómo identifiques a los admins)
        if (empleado.getEmail().equals("admin")) {
            // Mostrar diálogo de error indicando que no se puede eliminar un administrador
            DialogoController.showInfoDialog(
                    (Stage) tabla.getScene().getWindow(),
                    "No se puede eliminar al administrador",
                    event -> {}
            );
        } else {
            // Si no es administrador, proceder con la eliminación normal
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            empleadoDAO.eliminarEmpleado(empleado.getId());
            cargarDatosTabla();
        }
    }

    /**
     * Configura una ventana de cámara para capturar imágenes.
     *
     * @param primaryStage La escena principal para mostrar la ventana de cámara.
     * @param loader       El cargador de FXML para cargar la vista de cámara.
     * @param root         El nodo raíz para la vista de cámara.
     *                     Esta función configura la ventana de cámara, maneja eventos de ratón para arrastrar y cerrar la ventana,
     *                     y libera los recursos de cámara cuando se cierra la ventana.
     */
    @FXML
    private void configurarVentanaCamara(Stage primaryStage, FXMLLoader loader, Parent root) {
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
    }

    /**
     * Maneja la captura de una imagen desde la cámara.
     * Esta función carga la vista de la cámara desde el archivo FXML, configura la ventana de la cámara,
     * y captura una imagen. La imagen capturada se establece como la imagen para la vista de imagen.
     */
    public void handleCapturaImagen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/View/camara.fxml"));
            Parent root = loader.load();

            Stage primaryStage = (Stage) btnCamara.getScene().getWindow();
            configurarVentanaCamara(primaryStage, loader, root);

            CameraController controller = loader.getController();
            Image capturedImage = controller.getCapturedImage();

            if (capturedImage != null) {
                imgPhoto.setImage(capturedImage);
            }
        } catch (IOException e) {
            LOGGER.severe("Error al cargar la cámara: " + e.getMessage());
        }
    }

    /**
     * Maneja la carga de una imagen estableciendo la imagen seleccionada en la vista de imagen.
     *
     * @param mouseEvent El evento del mouse que desencadenó la carga de la imagen.
     *                   Esta función se llama cuando el usuario hace clic en el botón "Cargar imagen".
     *                   Utiliza la clase ImageUtils para manejar la carga de una imagen y establece la imagen seleccionada en la vista de imagen.
     */
    @FXML
    public void handleUploadImagen(MouseEvent mouseEvent) {
        ImageUtils.handleUploadImagen(mouseEvent, imgPhoto);
    }

    /**
     * Maneja la impresión de un informe de empleados.
     * Esta función llama al método para imprimir el informe de empleados desde la clase Reportes.
     *
     * @see Reportes#imprimirReporteEmpleados()
     */
    @FXML
    public void handleImprimirReporteEmpleado() {
        Reportes.imprimirReporteEmpleados();
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
            var resource = getClass().getResource("/help/help_add_empleado.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda para añadir empleado");
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