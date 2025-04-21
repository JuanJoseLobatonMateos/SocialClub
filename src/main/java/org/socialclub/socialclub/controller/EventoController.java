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
import javafx.geometry.Pos;
import javafx.print.PrinterJob;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.database.EventoDAO;
import org.socialclub.socialclub.model.Evento;
import org.socialclub.socialclub.util.ImageUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;


/**
 * Esta clase maneja la funcionalidad de la pantalla de Evento. Incluye métodos para inicializar la pantalla,
 * guardar eventos, configurar columnas de tabla, cargar datos, y manejar interacciones del usuario.
 */
public class EventoController {
    private static final Logger logger = LoggerFactory.getLogger(EventoController.class);

    @FXML
    public TableView<Evento> tableEventos;
    @FXML
    public TableColumn<Evento, LocalDate> colFecha;
    @FXML
    public TableColumn<Evento, String> colEvento;
    @FXML
    public MFXDatePicker calendar;
    @FXML
    public MFXTextField lblTitulo;
    @FXML
    public ImageView imgPhoto;
    @FXML
    public MFXButton btnFile;
    @FXML
    public MFXButton btnGuardar;
    @FXML
    public MFXIconButton helpIcon;

    /**
     * Método que se ejecuta cuando se inicializa la pantalla.
     */
    @FXML
    private void initialize() {
        configurarIconos();
        cargarDatosTabla();
        configurarTabla();
        configurarEstiloFilas();
        btnGuardar.setOnAction(e -> {
            try {
                guardarEvento();
            } catch (Exception ex) {
                logger.error("Error al guardar evento", ex);
            }
        });
    }

    /**
     * Método que guarda un evento en la base de datos.
     *
     * @throws IOException Si hay un error al cargar la imagen.
     */
    private void guardarEvento() throws IOException {
        LocalDate fechaEvento = calendar.getValue();
        if (fechaEvento.isBefore(LocalDate.now())) {
            DialogoController.showInfoDialog((Stage) tableEventos.getScene().getWindow(), "Fecha Inválida", event -> {
            });
        } else {
            Evento evento = new Evento();
            evento.setNombre(lblTitulo.getText());
            evento.setFecha(fechaEvento);

            // Cargar la imagen capturada desde imgPhoto
            Image image = imgPhoto.getImage();
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            byte[] fileContent = baos.toByteArray();
            evento.setImagen(fileContent);

            EventoDAO eventoDAO = new EventoDAO();
            eventoDAO.guardarEvento(evento);
            DialogoController.showInfoDialog((Stage) tableEventos.getScene().getWindow(), "Evento Guardado", event -> cargarDatosTabla());
        }
    }

    /**
     * Método que configura el estilo de las filas de la tabla de eventos.
     */
    private void configurarEstiloFilas() {
        tableEventos.setRowFactory(tv -> {
            TableRow<Evento> row = createTableRow();
            ContextMenu contextMenu = createContextMenu(row);
            row.setContextMenu(contextMenu);
            handleRowDoubleClick(row);
            return row;
        });
    }

    /**
     * Método que crea una nueva fila de tabla para el evento.
     *
     * @return La fila de tabla creada.
     */
    private TableRow<Evento> createTableRow() {
        return new TableRow<>() {
            @Override
            protected void updateItem(Evento evento, boolean empty) {
                super.updateItem(evento, empty);
                applyRowStyle(this, evento, empty);
            }
        };
    }

    /**
     * Método que aplica el estilo a una fila de tabla de evento.
     *
     * @param row    La fila de tabla.
     * @param evento El evento asociado con la fila.
     * @param empty  Si la fila está vacía.
     */
    private void applyRowStyle(TableRow<Evento> row, Evento evento, boolean empty) {
        if (evento == null || empty) {
            row.setStyle("");
        } else {
            if (evento.getFecha().isBefore(LocalDate.now())) {
                row.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
            } else {
                row.setStyle("-fx-background-color: #0ec32c; -fx-text-fill: black; -fx-border-color: white; -fx-border-width: 0 0 1 0;");
            }
        }
    }

    /**
     * Método que crea un menú contextual para una fila de tabla de evento.
     *
     * @param row La fila de tabla.
     * @return El menú contextual creado.
     */
    private ContextMenu createContextMenu(TableRow<Evento> row) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem eliminarItem = new MenuItem("Eliminar");
        eliminarItem.setStyle("-fx-background-color: white; -fx-text-fill: black; -fx-font-size: 14px;");
        eliminarItem.setOnAction(event -> {
            Evento evento = row.getItem();
            if (evento != null) {
                eliminarEvento(evento);
            }
        });
        contextMenu.getItems().add(eliminarItem);
        return contextMenu;
    }

    /**
     * Método que maneja el doble clic en una fila de tabla de evento.
     *
     * @param row La fila de tabla.
     */
    private void handleRowDoubleClick(TableRow<Evento> row) {
        row.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && (!row.isEmpty())) {
                Evento evento = row.getItem();
                mostrarImagenEvento(evento);
            }
        });
    }

    /**
     * Método que muestra la imagen del evento en una nueva ventana.
     *
     * @param evento El evento del que se va a mostrar la imagen.
     */
    private void mostrarImagenEvento(Evento evento) {
        try {
            // Crear una nueva ventana para mostrar la imagen
            Stage stage = new Stage();
            Image image = new Image(new ByteArrayInputStream(evento.getImagen()));
            VBox root = createImageViewLayout(image);

            Scene scene = new Scene(root);
            stage.setTitle("Imagen del Evento");
            stage.setScene(scene);
            stage.sizeToScene(); // Ajustar el tamaño de la ventana al contenido
            stage.show();
        } catch (Exception e) {
            logger.error("Error al mostrar la imagen del evento", e);
        }
    }

    /**
     * Método que crea un diseño de VBox para mostrar la imagen del evento.
     *
     * @param image La imagen del evento.
     * @return El diseño de VBox creado.
     */
    private VBox createImageViewLayout(Image image) {
        ImageView imageView = new ImageView();
        imageView.setImage(image);

        // Ajustar el tamaño de la ventana al tamaño de la imagen
        imageView.setPreserveRatio(true);
        imageView.setFitWidth(image.getWidth());
        imageView.setFitHeight(image.getHeight());

        // Crear un botón de impresión
        MFXButton btnImprimir = new MFXButton("Imprimir");
        btnImprimir.setStyle("-fx-background-color: rgba(30, 60, 90, 1); -fx-text-fill: white; -fx-font-size: 14px; margin-bottom: 10px;");
        btnImprimir.setOnAction(e -> imprimirImagen(image));

        // Crear un diseño de VBox y agregar la imagen y el botón
        VBox root = new VBox(imageView, btnImprimir);
        root.setSpacing(10);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new javafx.geometry.Insets(10));

        return root;
    }

    /**
     * Método que imprime la imagen del evento.
     *
     * @param image La imagen del evento.
     */
    private void imprimirImagen(Image image) {
        PrinterJob printerJob = PrinterJob.createPrinterJob();
        if (printerJob != null && printerJob.showPrintDialog(null)) {
            boolean success = printerJob.printPage(new ImageView(image));
            if (success) {
                printerJob.endJob();
            }
        }
    }

    /**
     * Método que elimina un evento de la base de datos.
     *
     * @param evento El evento que se va a eliminar.
     */
    private void eliminarEvento(Evento evento) {
        String mensaje = String.format("¿Estás seguro de que deseas eliminar el evento %s con fecha %s?", evento.getNombre(), evento.getFecha());
        DialogoController.showConfirmDialog((Stage) tableEventos.getScene().getWindow(), mensaje, event -> {
            EventoDAO eventoDAO = new EventoDAO();
            eventoDAO.eliminarEvento(evento.getId());
            DialogoController.showInfoDialog((Stage) tableEventos.getScene().getWindow(), "Evento Eliminado", event2 -> cargarDatosTabla()); // Recargar la tabla después de eliminar la reserva
        });
    }

    /**
     * Método que configura las columnas de la tabla de eventos.
     */
    private void configurarTabla() {
        configurarColumnaFecha(colFecha);
        configurarColumnaEvento(colEvento);
    }

    /**
     * Método que configura la columna de fecha de la tabla de eventos.
     *
     * @param colFecha La columna de fecha.
     */
    private void configurarColumnaFecha(TableColumn<Evento, LocalDate> colFecha) {
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colFecha.setCellFactory(TextFieldTableCell.forTableColumn(new LocalDateStringConverter()));
    }

    /**
     * Método que configura la columna de evento de la tabla de eventos.
     *
     * @param colEvento La columna de evento.
     */
    private void configurarColumnaEvento(TableColumn<Evento, String> colEvento) {
        colEvento.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEvento.setCellFactory(TextFieldTableCell.forTableColumn());
    }

    /**
     * Método que carga los datos de eventos de la base de datos en la tabla.
     */
    private void cargarDatosTabla() {
        EventoDAO eventoDAO = new EventoDAO();
        List<Evento> eventos = eventoDAO.obtenerTodosEventos();
        ObservableList<Evento> observableList = FXCollections.observableArrayList(eventos);
        tableEventos.setItems(observableList);
    }

    /**
     * Método que configura los iconos de los botones.
     */
    private void configurarIconos() {
        MFXFontIcon iconoReporte = new MFXFontIcon("fas-print", 24, Color.WHITE);
        MFXFontIcon iconGuardar = new MFXFontIcon("fas-floppy-disk", 24, Color.WHITE);
        btnFile.setGraphic(new MFXIconWrapper(iconoReporte, 24));
        btnGuardar.setGraphic(new MFXIconWrapper(iconGuardar, 24));
        helpIcon.setIcon(new MFXFontIcon("fas-circle-question", 14, Color.BLACK));
        helpIcon.setTooltip(new Tooltip("Ayuda"));
    }

    /**
     * Método que maneja el evento de cargar una imagen desde el sistema de archivos.
     *
     * @param mouseEvent El evento de ratón.
     */
    @FXML
    public void handleUploadImagen(MouseEvent mouseEvent) {
        ImageUtils.handleUploadImagen(mouseEvent, imgPhoto);
    }

    /**
     * Maneja el evento de clic en el icono de ayuda. Abre una ventana modal con el contenido de ayuda relacionado con la pantalla de Eventos.
     *
     * @param event El evento de ratón que desencadenó este método.
     */
    @FXML
    private void handleHelpIconClick(MouseEvent event) {
        try {
            // Cargar el archivo FXML de la vista de ayuda
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/View/help.fxml"));
            Parent helpRoot = fxmlLoader.load();

            // Obtener el controlador y pasar la URL de ayuda
            HelpController helpController = fxmlLoader.getController();
            var resource = getClass().getResource("/help/help_eventos.html");
            String helpUrl = resource != null ? resource.toExternalForm() : null;
            helpController.initialize(helpUrl);

            Stage helpStage = new Stage();
            helpStage.setTitle("Ayuda Eventos");
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