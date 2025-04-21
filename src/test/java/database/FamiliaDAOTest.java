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
import org.socialclub.socialclub.database.FamiliaDAO;
import org.socialclub.socialclub.model.Familia;
import org.socialclub.socialclub.util.HibernateUtil;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

    /**
     * Clase de prueba para FamiliaDAO.
     */
    @SuppressWarnings("ALL")
    class FamiliaDAOTest {

        @Mock
        private SessionFactory sessionFactory;
        @Mock
        private Session session;
        @Mock
        private Transaction transaction;
        @Mock
        private Query<Familia> query;

        private FamiliaDAO familiaDAO;
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
            familiaDAO = new FamiliaDAO();
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
         * Crea un objeto mock de Familia.
         *
         * @return un objeto mock de Familia.
         */
        private Familia crearFamiliaMock() {
            Familia familia = new Familia();
            familia.setId(1);
            familia.setNombreTitular("Nombre Titular");
            familia.setApellidosTitular("Apellidos Titular");
            return familia;
        }

        /**
         * Prueba para obtener todas las familias.
         * Verifica que la lista de familias no sea nula y tenga el tamaño esperado.
         */
        @Test
        void testObtenerTodasFamilias() {
            List<Familia> familiasMock = List.of(crearFamiliaMock());
            when(session.createQuery("from Familia", Familia.class)).thenReturn(query);
            when(query.getResultList()).thenReturn(familiasMock);

            List<Familia> familias = familiaDAO.obtenerTodasFamilias();

            assertNotNull(familias, "La lista de familias no debería ser nula");
            assertEquals(1, familias.size(), "La lista de familias debería tener un tamaño de 1");
        }

    }