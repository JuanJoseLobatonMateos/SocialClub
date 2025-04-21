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
import org.socialclub.socialclub.database.ReservaDAO;
import org.socialclub.socialclub.model.Instalacion;
import org.socialclub.socialclub.model.Reserva;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para ReservaDAO.
 */
@SuppressWarnings("ALL")
class ReservaDaoTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query<Reserva> reservaQuery;

    private ReservaDAO reservaDAO;
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
        reservaDAO = new ReservaDAO();
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
     * Crea un objeto mock de Reserva.
     *
     * @return un objeto mock de Reserva.
     */
    private Reserva crearReservaMock() {
        Reserva reserva = new Reserva();
        reserva.setId(1);
        reserva.setFecha(LocalDate.now());
        reserva.setHora(LocalTime.now());
        reserva.setIdInstalacion(new Instalacion());
        reserva.setNumeroSocio(new Socio());
        return reserva;
    }

    /**
     * Prueba para obtener reservas por fecha.
     * Verifica que la lista de reservas no sea nula y tenga el tamaño esperado.
     */
    @Test
    void testObtenerReservasPorFecha() {
        List<Reserva> reservasMock = List.of(crearReservaMock());
        when(session.createQuery("from Reserva where fecha = :fecha", Reserva.class)).thenReturn(reservaQuery);
        when(reservaQuery.setParameter("fecha", LocalDate.now())).thenReturn(reservaQuery);
        when(reservaQuery.getResultList()).thenReturn(reservasMock);

        List<Reserva> reservas = reservaDAO.obtenerReservasPorFecha(LocalDate.now());

        assertNotNull(reservas, "La lista de reservas no debería ser nula");
        assertEquals(1, reservas.size(), "La lista de reservas debería tener un tamaño de 1");
    }

    /**
     * Prueba para obtener todas las reservas.
     * Verifica que la lista de reservas no sea nula y tenga el tamaño esperado.
     */
    @Test
    void testObtenerReservas() {
        List<Reserva> reservasMock = List.of(crearReservaMock());
        when(session.createQuery("from Reserva", Reserva.class)).thenReturn(reservaQuery);
        when(reservaQuery.getResultList()).thenReturn(reservasMock);

        List<Reserva> reservas = reservaDAO.obtenerReservas();

        assertNotNull(reservas, "La lista de reservas no debería ser nula");
        assertEquals(1, reservas.size(), "La lista de reservas debería tener un tamaño de 1");
    }

    /**
     * Prueba para eliminar una reserva.
     * Verifica que la reserva se elimine correctamente.
     */
    @Test
    void testEliminarReserva() {
        Reserva reservaMock = crearReservaMock();
        when(session.get(Reserva.class, 1)).thenReturn(reservaMock);

        reservaDAO.eliminarReserva(1);

        verify(session, times(1)).remove(reservaMock);
        verify(transaction, times(1)).commit();
    }

    /**
     * Prueba para obtener las horas reservadas.
     * Verifica que la lista de horas reservadas no sea nula y tenga el tamaño esperado.
     */
    @Test
    void testObtenerHorasReservadas() {
        List<LocalTime> horasMock = List.of(LocalTime.now());
        Query<LocalTime> queryMock = mock(Query.class);
        when(session.createQuery("select r.hora from Reserva r where r.idInstalacion.id = :idInstalacion and r.fecha = :fecha", LocalTime.class)).thenReturn(queryMock);
        when(queryMock.setParameter("idInstalacion", 1)).thenReturn(queryMock);
        when(queryMock.setParameter("fecha", LocalDate.now())).thenReturn(queryMock);
        when(queryMock.getResultList()).thenReturn(horasMock);

        List<LocalTime> horasReservadas = reservaDAO.obtenerHorasReservadas(1, LocalDate.now());

        assertNotNull(horasReservadas, "La lista de horas reservadas no debería ser nula");
        assertEquals(1, horasReservadas.size(), "La lista de horas reservadas debería tener un tamaño de 1");
    }
}