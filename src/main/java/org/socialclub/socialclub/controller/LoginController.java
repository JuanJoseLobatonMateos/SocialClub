package org.socialclub.socialclub.controller;

import io.github.palexdev.materialfx.controls.MFXIconWrapper;
import io.github.palexdev.materialfx.controls.MFXPasswordField;
import io.github.palexdev.materialfx.controls.MFXTextField;
import io.github.palexdev.materialfx.validation.Constraint;
import io.github.palexdev.mfxresources.fonts.MFXFontIcon;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.util.EncriptadorUtil;
import org.socialclub.socialclub.util.SessionManager;
import org.socialclub.socialclub.util.TooltipUtil;

import java.util.List;

import static org.socialclub.socialclub.util.ConfigLoader.LOGGER;

/**
 * Controlador para manejar la lógica de inicio de sesión de los empleados.
 */
public class LoginController {

    private static final String ADMIN_USERNAME = "admin";
    private static final String INVALID_PSEUDO_CLASS = "invalid";
    private static final String LOGIN_PROMPT_TEXT = "Email";
    private static final String PASSWORD_PROMPT_TEXT = "Password";
    private static final String INVALID_LOGIN_MESSAGE = "Usuario o contraseña incorrectos.";
    private static final int EMAIL_MIN_LENGTH = 6;

    @FXML
    private MFXTextField loginField;

    @FXML
    private MFXPasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private Label validationLabel;

    private VentanaPrincipalController ventanaPrincipalController;

    /**
     * Establece el controlador de la ventana principal.
     *
     * @param ventanaPrincipalController el controlador de la ventana principal
     */
    public void setVentanaPrincipalController(VentanaPrincipalController ventanaPrincipalController) {
        this.ventanaPrincipalController = ventanaPrincipalController;
    }

    /**
     * Inicializa los componentes y configura validaciones y eventos.
     */
    @FXML
    private void initialize() {


        setupFields();
        setupValidationListeners();
        setupLoginButtonIcon();
        setupEnterKeyPress();
        setupTooltips();
    }

    /**
     * Configura los tooltips para los campos de texto y el botón de inicio de sesión.
     */
    private void setupTooltips() {
        TooltipUtil.setupTooltip(loginField, "Introduce tu email de usuario");
        TooltipUtil.setupTooltip(passwordField, "Introduce tu contraseña");
    }

    /**
     * Configura los campos de texto y sus restricciones.
     */
    private void setupFields() {
        setupLoginField();
        setupPasswordField();
    }

    /**
     * Configura el campo de texto del nombre de usuario.
     */
    private void setupLoginField() {
        loginField.setTextLimit(45);
        loginField.setPromptText(LOGIN_PROMPT_TEXT);
        loginField.getValidator().constraint(
                "El email de usuario debe tener al menos " + EMAIL_MIN_LENGTH + " caracteres",
                loginField.textProperty().length().greaterThanOrEqualTo(EMAIL_MIN_LENGTH)
        );
        loginField.setLeadingIcon(new MFXIconWrapper("fas-user", 16, Color.web("#4D4D4D"), 24));
    }

    /**
     * Configura el campo de texto de la contraseña.
     */
    private void setupPasswordField() {
        passwordField.setPromptText(PASSWORD_PROMPT_TEXT);
        passwordField.setLeadingIcon(new MFXIconWrapper("fas-lock", 16, Color.web("#4D4D4D"), 24));
    }

    /**
     * Configura los listeners de validación para los campos.
     */
    private void setupValidationListeners() {
        passwordField.getValidator().validProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                validationLabel.setVisible(false);
            }
        });

        passwordField.delegateFocusedProperty().addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(oldValue) && Boolean.FALSE.equals(newValue)) {
                validatePasswordField();
            }
        });
    }

    /**
     * Valida el campo de la contraseña y muestra un mensaje si es inválido.
     */
    private void validatePasswordField() {
        final List<Constraint> constraints = passwordField.validate();
        if (!constraints.isEmpty()) {
            passwordField.pseudoClassStateChanged(PseudoClass.getPseudoClass(INVALID_PSEUDO_CLASS), true);
            validationLabel.setText(constraints.getFirst().getMessage());
            validationLabel.setVisible(true);
        }
    }

    /**
     * Configura el ícono del botón de inicio de sesión.
     */
    private void setupLoginButtonIcon() {
        MFXFontIcon icon = new MFXFontIcon("fas-arrow-right-to-bracket", 24, Color.WHITE);
        loginButton.setGraphic(new MFXIconWrapper(icon, 24));
    }

    /**
     * Configura el evento de presionar Enter para iniciar sesión.
     */
    private void setupEnterKeyPress() {
        passwordField.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                loginButton.fire();
            }
        });
    }

    /**
     * Maneja el evento de inicio de sesión.
     *
     * @param event el evento de acción
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        final String email = loginField.getText();
        final String password = passwordField.getText();

        if (isValidLogin(email, password)) {
            showSuccessfulLogin(event);
        } else {
            showInvalidLoginMessage();
        }
    }

    /**
     * Verifica si las credenciales de inicio de sesión son válidas.
     *
     * @param email    el email del usuario
     * @param password la contraseña ingresada por el usuario
     * @return true si las credenciales son válidas, false en caso contrario
     */
private boolean isValidLogin(String email, String password) {
    try {
        if (ADMIN_USERNAME.equals(email) && ADMIN_USERNAME.equals(password)) {
            // Autenticar al usuario admin sin verificar la contraseña cifrada
            SessionManager.getInstance().setAuthenticatedEmployeeId(1); // Asumiendo que el ID del admin es 1
            return true;
        }

        EmpleadoDAO empleadoDAO = new EmpleadoDAO();
        Empleado empleado = empleadoDAO.obtenerEmpleadoPorEmail(email);
        if (empleado != null && EncriptadorUtil.verifyPassword(password, empleado.getContrasenia())) {
            SessionManager.getInstance().setAuthenticatedEmployeeId(empleado.getId());
            return true;
        }
    } catch (Exception e) {
        LOGGER.severe("Error al validar el empleado: " + e.getMessage());
    }
    return false;
}
    /**
     * Muestra un mensaje de error de inicio de sesión inválido.
     */
    private void showInvalidLoginMessage() {
        validationLabel.setText(INVALID_LOGIN_MESSAGE);
        validationLabel.setVisible(true);
        loginField.clear();
        passwordField.clear();
    }

    /**
     * Muestra un mensaje de inicio de sesión exitoso y cambia a la vista principal.
     *
     * @param event el evento de acción
     */
    private void showSuccessfulLogin(ActionEvent event) {
        Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        DialogoController.showInfoDialog(
                primaryStage,
                "Bienvenido, " + obtenerNombreEmpleado(),
                event1 -> switchToSecondaryView()
        );
    }

    /**
     * Cambia a la vista principal.
     */
    private void switchToSecondaryView() {
        if (ventanaPrincipalController != null) {
            ventanaPrincipalController.loadInitialView("/View/main.fxml");
        } else {
            LOGGER.severe("La ventana principal es null");
        }
    }

    /**
     * Obtiene el nombre del empleado autenticado.
     *
     * @return el nombre del empleado autenticado, o null si no hay un empleado autenticado
     */
    private String obtenerNombreEmpleado() {
        Integer authenticatedEmployeeId = SessionManager.getInstance().getAuthenticatedEmployeeId();
        if (authenticatedEmployeeId != null) {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            return empleadoDAO.obtenerNombreEmpleadoPorId(authenticatedEmployeeId);
        }
        return null;
    }
}
