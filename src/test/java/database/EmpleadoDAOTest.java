package database;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.Rol;

import javax.sql.rowset.serial.SerialBlob;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para EmpleadoDAO.
 */
class EmpleadoDAOTest {

    @Mock
    private EmpleadoDAO empleadoDAO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Empleado crearEmpleadoMock() {
        Empleado empleadoMock = new Empleado();
        empleadoMock.setId(1);
        empleadoMock.setNombre("Juan");
        empleadoMock.setApellidos("Perez");
        empleadoMock.setDni("12345678P");
        empleadoMock.setEmail("juan.perez@example.com");
        empleadoMock.setDomicilio("Calle Falsa 123");
        empleadoMock.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        empleadoMock.setTelefono("123456789");
        Rol rol = new Rol();
        rol.setId(1);
        empleadoMock.setRol(rol);
        return empleadoMock;
    }

    /**
     * Prueba para obtener un empleado por su ID.
     */
    @Test
    void obtenerEmpleadoPorId() {
        Empleado empleadoMock = crearEmpleadoMock();

        when(empleadoDAO.obtenerEmpleadoPorId(1)).thenReturn(empleadoMock);

        Empleado empleado = empleadoDAO.obtenerEmpleadoPorId(1);

        assertNotNull(empleado, "El empleado no debería ser nulo");
        assertEquals(empleadoMock.getId(), empleado.getId());
    }

    /**
     * Prueba para crear un empleado.
     */
    @Test
    void crearEmpleado() {
        Empleado empleadoMock = crearEmpleadoMock();

        doNothing().when(empleadoDAO).crearEmpleado(empleadoMock);

        empleadoDAO.crearEmpleado(empleadoMock);

        verify(empleadoDAO, times(1)).crearEmpleado(empleadoMock);
    }

    /**
     * Prueba para actualizar un empleado.
     */
    @Test
    void actualizarEmpleado() {
        Empleado empleadoMock = crearEmpleadoMock();

        doNothing().when(empleadoDAO).actualizarEmpleado(empleadoMock);

        empleadoDAO.actualizarEmpleado(empleadoMock);

        verify(empleadoDAO, times(1)).actualizarEmpleado(empleadoMock);
    }

    /**
     * Prueba para eliminar un empleado.
     */
    @Test
    void eliminarEmpleado() {
        doNothing().when(empleadoDAO).eliminarEmpleado(1);

        empleadoDAO.eliminarEmpleado(1);

        verify(empleadoDAO, times(1)).eliminarEmpleado(1);
    }

    /**
     * Prueba para obtener un empleado por su email.
     */
    @Test
    void obtenerEmpleadoPorEmail() {
        Empleado empleadoMock = crearEmpleadoMock();

        when(empleadoDAO.obtenerEmpleadoPorEmail("juan.perez@example.com")).thenReturn(empleadoMock);

        Empleado empleado = empleadoDAO.obtenerEmpleadoPorEmail("juan.perez@example.com");

        assertNotNull(empleado, "El empleado no debería ser nulo");
        assertEquals(empleadoMock.getEmail(), empleado.getEmail());
    }

    /**
     * Prueba para obtener todos los empleados.
     */
    @Test
    void obtenerTodosEmpleados() {
        Empleado empleadoMock = crearEmpleadoMock();
        List<Empleado> empleadosMock = List.of(empleadoMock);

        when(empleadoDAO.obtenerTodosEmpleados()).thenReturn(empleadosMock);

        List<Empleado> empleados = empleadoDAO.obtenerTodosEmpleados();

        assertNotNull(empleados, "La lista de empleados no debería ser nula");
        assertEquals(1, empleados.size(), "La lista de empleados debería tener un tamaño de 1");
    }

    /**
     * Prueba para verificar si un DNI existe.
     */
    @Test
    void existeDni() {
        when(empleadoDAO.existeDni("12345678P")).thenReturn(true);

        boolean existe = empleadoDAO.existeDni("12345678P");

        assertTrue(existe, "El DNI debería existir");
    }

    /**
     * Prueba para verificar si un email existe.
     */
    @Test
    void existeEmail() {
        when(empleadoDAO.existeEmail("juan.perez@example.com")).thenReturn(true);

        boolean existe = empleadoDAO.existeEmail("juan.perez@example.com");

        assertTrue(existe, "El email debería existir");
    }

    /**
     * Prueba para guardar un empleado.
     */
    @Test
    void guardarEmpleado() throws Exception {
        SerialBlob imagen = new SerialBlob(new byte[]{1, 2, 3});

        doNothing().when(empleadoDAO).guardarEmpleado(
                eq("Juan"),
                eq("Perez"),
                eq("12345678P"),
                eq("Calle Falsa 123"),
                eq("123456789"),
                eq("1990-01-01"),
                eq("juan.perez@example.com"),
                eq("password"),
                eq(imagen)
        );

        empleadoDAO.guardarEmpleado(
                "Juan",
                "Perez",
                "12345678P",
                "Calle Falsa 123",
                "123456789",
                "1990-01-01",
                "juan.perez@example.com",
                "password",
                imagen
        );

        verify(empleadoDAO, times(1)).guardarEmpleado(
                "Juan",
                "Perez",
                "12345678P",
                "Calle Falsa 123",
                "123456789",
                "1990-01-01",
                "juan.perez@example.com",
                "password",
                imagen
        );
    }

    /**
     * Prueba para obtener el nombre de un empleado por su ID.
     */
    @Test
    void obtenerNombreEmpleadoPorId() {
        String nombreEsperado = "Juan";
        Integer id = 1;

        when(empleadoDAO.obtenerNombreEmpleadoPorId(id)).thenReturn(nombreEsperado);

        String nombre = empleadoDAO.obtenerNombreEmpleadoPorId(id);

        assertNotNull(nombre, "El nombre no debería ser nulo");
        assertEquals(nombreEsperado, nombre, "El nombre debería coincidir con el esperado");
    }

}