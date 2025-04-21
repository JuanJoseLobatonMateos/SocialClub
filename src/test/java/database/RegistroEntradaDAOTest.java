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
import org.socialclub.socialclub.database.RegistroEntradaDAO;
import org.socialclub.socialclub.model.RegistroEntrada;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class RegistroEntradaDAOTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query<RegistroEntrada> query;
    @Mock
    private Query<Socio> socioQuery;
    @Mock
    private Query<Long> queryLong;

    private RegistroEntradaDAO registroEntradaDAO;
    private MockedStatic<HibernateUtil> mockedHibernateUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockedHibernateUtil = mockStatic(HibernateUtil.class);
        mockedHibernateUtil.when(HibernateUtil::getSessionFactory).thenReturn(sessionFactory);
        when(sessionFactory.openSession()).thenReturn(session);
        when(session.beginTransaction()).thenReturn(transaction);
        registroEntradaDAO = new RegistroEntradaDAO();
    }

    @AfterEach
    void tearDown() {
        if (mockedHibernateUtil != null) {
            mockedHibernateUtil.close();
        }
    }

    private RegistroEntrada crearRegistroEntradaMock() {
        RegistroEntrada registroEntrada = new RegistroEntrada();
        registroEntrada.setId(1);
        registroEntrada.setFecha(LocalDate.now());
        registroEntrada.setHoraEntrada(LocalTime.now());
        return registroEntrada;
    }

    private Socio crearSocioMock() {
        Socio socio = new Socio();
        socio.setIdSocio(1);
        socio.setNumeroSocio("12345");
        return socio;
    }

    @Test
    void testCrearRegistroEntrada() {
        RegistroEntrada registroEntradaMock = crearRegistroEntradaMock();

        registroEntradaDAO.crearRegistroEntrada(registroEntradaMock);

        verify(session, times(1)).persist(registroEntradaMock);
        verify(transaction, times(1)).commit();
    }

    @Test
    void testActualizarRegistroEntrada() {
        RegistroEntrada registroEntradaMock = crearRegistroEntradaMock();

        registroEntradaDAO.actualizarRegistroEntrada(registroEntradaMock);

        verify(session, times(1)).merge(registroEntradaMock);
        verify(transaction, times(1)).commit();
    }

    @Test
    void testObtenerRegistrosEntradaPorFecha() {
        LocalDate fecha = LocalDate.now();
        List<RegistroEntrada> registrosMock = List.of(new RegistroEntrada());

        when(session.createQuery("FROM RegistroEntrada WHERE fecha = :fecha", RegistroEntrada.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(registrosMock);

        List<RegistroEntrada> registros = registroEntradaDAO.obtenerRegistrosEntradaPorFecha(fecha);

        assertNotNull(registros);
        assertEquals(1, registros.size());
    }




@Test
   void testObtenerTotalSociosDentro() {
       when(session.createQuery("SELECT COUNT(r) FROM RegistroEntrada r WHERE r.horaSalida IS NULL", Long.class))
           .thenReturn(queryLong);
       when(queryLong.getSingleResult()).thenReturn(10L);

       int totalSociosDentro = registroEntradaDAO.obtenerTotalSociosDentro();

       assertEquals(10, totalSociosDentro);
   }
    @Test
    void testMarcarSalidaAutomatica() {
        List<RegistroEntrada> registrosMock = List.of(crearRegistroEntradaMock());
        when(session.createQuery("FROM RegistroEntrada WHERE horaSalida IS NULL", RegistroEntrada.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(registrosMock);

        registroEntradaDAO.marcarSalidaAutomatica();

        verify(session, times(1)).merge(any(RegistroEntrada.class));
        verify(transaction, times(1)).commit();
    }

    @Test
    void testObtenerSociosFuera() {
        List<Socio> sociosMock = List.of(crearSocioMock());
        when(session.createQuery("FROM Socio s WHERE s.numeroSocio NOT IN (SELECT r.numeroSocio.numeroSocio FROM RegistroEntrada r WHERE r.horaSalida IS NULL)", Socio.class)).thenReturn(socioQuery);
        when(socioQuery.getResultList()).thenReturn(sociosMock);

        List<Socio> socios = registroEntradaDAO.obtenerSociosFuera();

        assertNotNull(socios, "La lista de socios no debería ser nula");
        assertEquals(1, socios.size(), "La lista de socios debería tener un tamaño de 1");
    }

    @Test
    void testObtenerSociosDentro() {
        List<Socio> sociosMock = List.of(crearSocioMock());
        when(session.createQuery("SELECT r.numeroSocio FROM RegistroEntrada r WHERE r.horaSalida IS NULL", Socio.class)).thenReturn(socioQuery);
        when(socioQuery.getResultList()).thenReturn(sociosMock);

        List<Socio> socios = registroEntradaDAO.obtenerSociosDentro();

        assertNotNull(socios, "La lista de socios no debería ser nula");
        assertEquals(1, socios.size(), "La lista de socios debería tener un tamaño de 1");
    }

    @Test
    void testObtenerSocioPorNumero() {
        Socio socioMock = crearSocioMock();
        when(session.createQuery("FROM Socio WHERE numeroSocio = :numeroSocio", Socio.class)).thenReturn(socioQuery);
        when(socioQuery.uniqueResult()).thenReturn(socioMock);

        Socio socio = registroEntradaDAO.obtenerSocioPorNumero("12345");

        assertNotNull(socio, "El socio no debería ser nulo");
        assertEquals("12345", socio.getNumeroSocio(), "El número de socio debería ser 12345");
    }


}