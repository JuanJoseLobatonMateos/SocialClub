package database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.socialclub.socialclub.database.RegistroEntradaDAO;
import org.socialclub.socialclub.model.RegistroEntrada;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.mockito.Mockito.*;

/**
 * Clase de prueba de integración para {@link RegistroEntradaDAO}.
 * Esta clase utiliza Mockito para simular el comportamiento de {@link SessionFactory}, {@link Session} y {@link Transaction}.
 */
public class RegistroEntradaDAOIntegrationTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;

    private RegistroEntradaDAO registroEntradaDAO;
    private AutoCloseable closeable;

    /**
     * Configura el entorno de prueba antes de cada método de prueba.
     * Inicializa los mocks y establece el {@link SessionFactory} en la instancia de {@link RegistroEntradaDAO}.
     *
     * @throws Exception si ocurre un error al configurar el entorno de prueba.
     */
    @BeforeEach
    public void setUp() throws Exception {
        closeable = MockitoAnnotations.openMocks(this);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        registroEntradaDAO = new RegistroEntradaDAO();

        // Usar reflexión para establecer el SessionFactory
        Field sessionFactoryField = RegistroEntradaDAO.class.getDeclaredField("sessionFactory");
        sessionFactoryField.setAccessible(true);
        sessionFactoryField.set(registroEntradaDAO, sessionFactory);
    }

    /**
     * Limpia el entorno de prueba después de cada método de prueba.
     *
     * @throws Exception si ocurre un error al limpiar el entorno de prueba.
     */
    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    /**
     * Prueba el método {@link RegistroEntradaDAO#crearRegistroEntrada(RegistroEntrada)}.
     * Verifica que el método persist() de {@link Session} se llame una vez,
     * que el método commit() de {@link Transaction} se llame una vez,
     * y que el método close() de {@link Session} se llame una vez.
     */
    @Test
    public void testCrearRegistroEntrada() {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setFecha(LocalDate.now());
        registroEntrada.setHoraEntrada(LocalTime.now());

        registroEntradaDAO.crearRegistroEntrada(registroEntrada);

        verify(session, times(1)).persist(registroEntrada);
        verify(transaction, times(1)).commit();
        verify(session, times(1)).close(); // Verifica que la sesión se cierre
    }
}