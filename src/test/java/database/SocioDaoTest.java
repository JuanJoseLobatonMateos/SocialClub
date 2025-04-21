package database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.socialclub.socialclub.database.SocioDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.Familia;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para {@link SocioDAO}.
 * Verifica el correcto funcionamiento de las operaciones CRUD relacionadas con socios.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
class SocioDaoTest {

    @Mock
    private Session session;

    @Mock
    private Transaction hibernateTransaction;

    @Mock
    private SessionFactory sessionFactory;


    private AutoCloseable closeable;
    private SocioDAO socioDAO;

    /**
     * Configura el entorno de prueba antes de cada test.
     * Inicializa los mocks y configura comportamientos básicos.
     */
    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        socioDAO = new SocioDAO();

        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(hibernateTransaction);
        when(sessionFactory.getCurrentSession()).thenReturn(session);
    }

    /**
     * Limpia los recursos utilizados después de cada prueba.
     *
     * @throws Exception sí ocurre algún error al cerrar los mocks
     */
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * Verifica el guardado de un socio cuando la familia no existe.
     * Debe crear una nueva familia y el socio asociado.
     */
    @Test
    void testGuardarSocio() {
        when(session.get(eq(Familia.class), anyInt())).thenReturn(null);

        try (MockedStatic<HibernateUtil> mockedStatic = mockStatic(HibernateUtil.class)) {
            mockedStatic.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            Familia familia = new Familia();
            familia.setId(1);
            Empleado empleado = new Empleado();
            empleado.setId(1);

            socioDAO.guardarSocio(familia, "Nombre", "Apellidos", "123456789", "12345678A",
                    "correo@example.com", "2000-01-01", null, null,
                    Socio.Titularidad.TITULAR, (byte) 0, LocalDate.now().toString(), empleado, "contraseña");

            verify(session).beginTransaction();
            verify(session).persist(any(Familia.class));
            verify(session).persist(any(Socio.class));
            verify(hibernateTransaction).commit();
        }
    }

    /**
     * Verifica la actualización de datos de un socio existente.
     * Debe realizar la operación merge y confirmar la transacción.
     */
    @Test
    void testActualizarSocio() {
        Socio socio = new Socio();
        socio.setNumeroSocio("S001");
        socio.setNombre("Juan");

        try (MockedStatic<HibernateUtil> mockedStatic = mockStatic(HibernateUtil.class)) {
            mockedStatic.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            socioDAO.actualizarSocio(socio);

            verify(session).beginTransaction();
            verify(session).merge(socio);
            verify(hibernateTransaction).commit();
        }
    }

    /**
     * Verifica la eliminación de un socio existente.
     * Debe obtener el socio por su ID y eliminarlo de la base de datos.
     */
    @Test
    void testEliminarSocio() {
        Socio socio = new Socio();
        socio.setNumeroSocio("S001");
        when(session.get(Socio.class, "S001")).thenReturn(socio);

        try (MockedStatic<HibernateUtil> mockedStatic = mockStatic(HibernateUtil.class)) {
            mockedStatic.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            socioDAO.eliminarSocio("S001");

            verify(session).beginTransaction();
            verify(session).get(Socio.class, "S001");
            verify(session).remove(socio);
            verify(hibernateTransaction).commit();
        }
    }

    /**
     * Verifica el guardado de un socio cuando la familia ya existe.
     * No debe crear una nueva familia, solo el socio asociado.
     */
    @Test
    void testGuardarSocioConFamiliaExistente() {
        Familia familiaExistente = new Familia();
        familiaExistente.setId(1);
        familiaExistente.setNombreTitular("Nombre Existente");
        when(session.get(Familia.class, 1)).thenReturn(familiaExistente);

        try (MockedStatic<HibernateUtil> mockedStatic = mockStatic(HibernateUtil.class)) {
            mockedStatic.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);

            Familia familia = new Familia();
            familia.setId(1);
            Empleado empleado = new Empleado();
            empleado.setId(1);

            socioDAO.guardarSocio(familia, "Nombre", "Apellidos", "123456789", "12345678A",
                    "correo@example.com", "2000-01-01", null, null,
                    Socio.Titularidad.TITULAR, (byte) 0, LocalDate.now().toString(), empleado, "contraseña");

            verify(session).beginTransaction();
            verify(session, never()).persist(any(Familia.class));
            verify(session).persist(any(Socio.class));
            verify(hibernateTransaction).commit();
        }
    }
}