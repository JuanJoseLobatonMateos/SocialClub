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
import org.socialclub.socialclub.database.EventoDAO;
import org.socialclub.socialclub.model.Evento;
import org.socialclub.socialclub.util.HibernateUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para EventoDAO.
 */
@SuppressWarnings("ALL")
class EventoDAOTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query<Evento> query;

    private EventoDAO eventoDAO;
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
        eventoDAO = new EventoDAO();
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
     * Crea un objeto mock de Evento.
     *
     * @return un objeto mock de Evento.
     */
    private Evento crearEventoMock() {
        Evento evento = new Evento();
        evento.setId(1);
        evento.setNombre("Evento Test");
        return evento;
    }

    /**
     * Prueba para guardar un evento.
     * Verifica que el evento se guarde correctamente.
     */
    @Test
    void testGuardarEvento() {
        Evento eventoMock = crearEventoMock();

        eventoDAO.guardarEvento(eventoMock);

        verify(session, times(1)).persist(eventoMock);
        verify(transaction, times(1)).commit();
    }

    /**
     * Prueba para obtener todos los eventos.
     * Verifica que la lista de eventos no sea nula y tenga el tamaño esperado.
     */
    @Test
    void testObtenerTodosEventos() {
        List<Evento> eventosMock = List.of(crearEventoMock());
        when(session.createQuery("from Evento", Evento.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(eventosMock);

        List<Evento> eventos = eventoDAO.obtenerTodosEventos();

        assertNotNull(eventos, "La lista de eventos no debería ser nula");
        assertEquals(1, eventos.size(), "La lista de eventos debería tener un tamaño de 1");
    }

    /**
     * Prueba para eliminar un evento.
     * Verifica que el evento se elimine correctamente.
     */
    @Test
    void testEliminarEvento() {
        Evento eventoMock = crearEventoMock();
        when(session.get(Evento.class, 1)).thenReturn(eventoMock);

        eventoDAO.eliminarEvento(1);

        verify(session, times(1)).remove(eventoMock);
        verify(transaction, times(1)).commit();
    }

}