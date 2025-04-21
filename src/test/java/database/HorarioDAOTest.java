package database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.socialclub.socialclub.database.HorarioDAO;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Clase de prueba para HorarioDAO.
 */
class HorarioDAOTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query<LocalTime> query;

    private HorarioDAO horarioDAO;
    private MockedStatic<HibernateUtil> mockedHibernateUtil;

    /**
     * Configura el entorno de prueba antes de cada prueba.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedHibernateUtil = mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        horarioDAO = new HorarioDAO();
    }

    /**
     * Limpia el entorno de prueba después de cada prueba.
     */
    @AfterEach
    void tearDown() {
        if (mockedHibernateUtil != null) {
            mockedHibernateUtil.close();
        }
    }


    /**
     * Prueba para obtener las horas por instalación.
     * Verifica que la lista de horas no sea nula y tenga el tamaño esperado.
     */
    @Test
    void testObtenerHorasPorInstalacion() {
        List<LocalTime> horasMock = List.of(LocalTime.of(9, 0));
        when(session.createQuery("select h.horaInicio from Horario h where h.idInstalacion.id = :idInstalacion", LocalTime.class)).thenReturn(query);
        when(query.setParameter("idInstalacion", 1)).thenReturn(query);
        when(query.getResultList()).thenReturn(horasMock);

        List<LocalTime> horas = horarioDAO.obtenerHorasPorInstalacion(1);

        assertNotNull(horas, "La lista de horas no debería ser nula");
        assertEquals(1, horas.size(), "La lista de horas debería tener un tamaño de 1");
    }

    // Otros métodos de prueba pueden ser añadidos aquí
}