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
import org.socialclub.socialclub.database.InstalacionDAO;
import org.socialclub.socialclub.model.Instalacion;
import org.socialclub.socialclub.util.HibernateUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Clase de prueba para InstalacionDAO.
 */
class InstalacionDAOTest {

    @Mock
    private SessionFactory sessionFactory;
    @Mock
    private Session session;
    @Mock
    private Transaction transaction;
    @Mock
    private Query<Instalacion> query;

    private InstalacionDAO instalacionDAO;
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
        instalacionDAO = new InstalacionDAO();
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
     * Crea un objeto mock de Instalacion.
     *
     * @return un objeto mock de Instalacion.
     */
    private Instalacion crearInstalacionMock() {
        Instalacion instalacion = new Instalacion();
        instalacion.setId(1);
        instalacion.setNombre("Instalacion Test");
        instalacion.setTipo("Tipo Test");
        instalacion.setCapacidad(100);
        return instalacion;
    }

    /**
     * Prueba para crear una instalación.
     * Verifica que la instalación se cree correctamente.
     */
    @Test
    void testCrearInstalacion() {
        Instalacion instalacionMock = crearInstalacionMock();

        instalacionDAO.crearInstalacion(instalacionMock);

        verify(session, times(1)).persist(instalacionMock);
        verify(transaction, times(1)).commit();
    }

    /**
     * Prueba para actualizar una instalación.
     * Verifica que la instalación se actualice correctamente.
     */
    @Test
    void testUpdateInstalacion() {
        Instalacion instalacionMock = crearInstalacionMock();

        instalacionDAO.updateInstalacion(instalacionMock);

        verify(session, times(1)).merge(instalacionMock);
        verify(transaction, times(1)).commit();
    }

    /**
     * Prueba para eliminar una instalación.
     * Verifica que la instalación se elimine correctamente.
     */
    @Test
    void testDeleteInstalacion() {
        Instalacion instalacionMock = crearInstalacionMock();
        when(session.get(Instalacion.class, 1)).thenReturn(instalacionMock);

        instalacionDAO.deleteInstalacion(1);

        verify(session, times(1)).remove(instalacionMock);
        verify(transaction, times(1)).commit();
    }

    /**
     * Prueba para obtener todas las instalaciones.
     * Verifica que la lista de instalaciones no sea nula y tenga el tamaño esperado.
     */
    @Test
    void testObtenerInstalaciones() {
        List<Instalacion> instalacionesMock = List.of(crearInstalacionMock());
        when(session.createQuery("from Instalacion", Instalacion.class)).thenReturn(query);
        when(query.getResultList()).thenReturn(instalacionesMock);

        List<Instalacion> instalaciones = instalacionDAO.obtenerInstalaciones();

        assertNotNull(instalaciones, "La lista de instalaciones no debería ser nula");
        assertEquals(1, instalaciones.size(), "La lista de instalaciones debería tener un tamaño de 1");
    }

}