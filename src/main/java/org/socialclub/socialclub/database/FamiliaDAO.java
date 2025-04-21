package org.socialclub.socialclub.database;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.Familia;

import java.util.List;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

                /**
                 * Esta clase proporciona operaciones de acceso a datos para la entidad {@link Familia}.
                 */
                public class FamiliaDAO {
                    private static final Logger logger = LoggerFactory.getLogger(FamiliaDAO.class);

                    /**
                     * Recupera todas las entidades {@link Familia} de la base de datos.
                     *
                     * @return Una lista de objetos {@link Familia}. Si no se encuentran familias, se devuelve una lista vacía.
                     */
                    public List<Familia> obtenerTodasFamilias() {
                        List<Familia> familias = null;
                        Transaction transaction = null;
                        try (Session session = getSessionFactory().openSession()) {
                            transaction = session.beginTransaction();
                            Query<Familia> query = session.createQuery("from Familia", Familia.class);
                            familias = query.getResultList();
                            transaction.commit();
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            logger.error("Error al obtener todas las familias", e);
                        }
                        return familias;
                    }
                }