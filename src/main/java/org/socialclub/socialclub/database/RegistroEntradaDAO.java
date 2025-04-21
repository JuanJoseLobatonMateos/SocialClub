package org.socialclub.socialclub.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.model.RegistroEntrada;
import org.socialclub.socialclub.model.Socio;
import org.socialclub.socialclub.util.HibernateUtil;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.socialclub.socialclub.util.HibernateUtil.getSessionFactory;

/**
 * Data Access Object (DAO) para administrar las entidades {@link RegistroEntrada} en la base de datos.
 * Esta clase proporciona métodos para crear, actualizar, recuperar y eliminar registros de {@link RegistroEntrada}.
 */
public class RegistroEntradaDAO {
    private static final Logger logger = LoggerFactory.getLogger(RegistroEntradaDAO.class);

    private final SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    /**
     * Crea un nuevo registro de {@link RegistroEntrada} en la base de datos.
     *
     * @param registroEntrada El objeto {@link RegistroEntrada} que se va a crear.
     */
    public void crearRegistroEntrada(RegistroEntrada registroEntrada) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.persist(registroEntrada);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al crear el registro de entrada", e);
        }
    }    /**
     * Actualiza un registro de {@link RegistroEntrada} existente en la base de datos.
     *
     * @param registroEntrada El objeto {@link RegistroEntrada} que se va a actualizar.
     */
    public void actualizarRegistroEntrada(RegistroEntrada registroEntrada) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(registroEntrada);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al actualizar el registro de entrada", e);
        }
    }

    /**
     * Recupera una lista de registros de {@link RegistroEntrada} de la base de datos según la fecha dada.
     *
     * @param fecha La fecha para la que se van a recuperar los registros de {@link RegistroEntrada}.
     * @return Una lista de registros de {@link RegistroEntrada} que coinciden con la fecha dada.
     */
public List<RegistroEntrada> obtenerRegistrosEntradaPorFecha(LocalDate fecha) {
        Transaction transaction = null;
        List<RegistroEntrada> registros = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<RegistroEntrada> query = session.createQuery("FROM RegistroEntrada WHERE fecha = :fecha", RegistroEntrada.class);
            query.setParameter("fecha", fecha);
            registros = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener los registros de entrada por fecha", e);
        }
        return registros;
    }
    /**
     * Recupera el registro de {@link RegistroEntrada} más reciente para un determinado {@link Socio} sin una hora de salida correspondiente.
     *
     * @param socio El {@link Socio} para el que se va a recuperar el registro de {@link RegistroEntrada}.
     * @return El registro de {@link RegistroEntrada} más reciente para el {@link Socio} dado sin una hora de salida correspondiente.
     */
    public RegistroEntrada obtenerRegistroEntradaSinSalida(Socio socio) {
        Session session = getSessionFactory().openSession();
        RegistroEntrada registroEntrada = null;
        try {
            session.beginTransaction();
            Query<RegistroEntrada> query = session.createQuery("FROM RegistroEntrada WHERE numeroSocio.idSocio = :idSocio AND horaSalida IS NULL ORDER BY fecha DESC, horaEntrada DESC", RegistroEntrada.class);
            query.setParameter("idSocio", socio.getIdSocio());
            registroEntrada = query.setMaxResults(1).uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener el registro de entrada sin hora de salida", e);
        } finally {
            session.close();
        }
        return registroEntrada;
    }

    /**
     * Recupera el número total de registros de {@link Socio} en la base de datos.
     *
     * @return El número total de registros de {@link Socio}.
     */
    public int obtenerTotalSocios() {
        Session session = getSessionFactory().openSession();
        int totalSocios = 0;
        try {
            session.beginTransaction();
            String query = "SELECT COUNT(s) FROM Socio s";
            totalSocios = (session.createQuery(query, Long.class).getSingleResult()).intValue();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener el total de socios", e);
            if (session.getTransaction() != null) {
                session.getTransaction().rollback();
            }
        } finally {
            session.close();
        }
        return totalSocios;
    }

    /**
     * Recupera el número de registros de {@link Socio} actualmente presentes en el club.
     *
     * @return El número de registros de {@link Socio} con una hora de entrada pero sin una hora de salida.
     */
    public int obtenerTotalSociosDentro() {
        Session session = getSessionFactory().openSession();
        Transaction transaction = null;
        int sociosDentro = 0;
        try {
            transaction = session.beginTransaction();
            String query = "SELECT COUNT(r) FROM RegistroEntrada r WHERE r.horaSalida IS NULL";
            sociosDentro = session.createQuery(query, Long.class).getSingleResult().intValue();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener el total de socios dentro", e);
        } finally {
            session.close();
        }
        return sociosDentro;
    }
    /**
     * Marca la hora de salida de todos los registros de {@link RegistroEntrada} sin una hora de salida correspondiente como medianoche.
     */
    public void marcarSalidaAutomatica() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            String query = "FROM RegistroEntrada WHERE horaSalida IS NULL";
            List<RegistroEntrada> registros = session.createQuery(query, RegistroEntrada.class).getResultList();
            for (RegistroEntrada registro : registros) {
                registro.setHoraSalida(LocalTime.MIDNIGHT);
                session.merge(registro);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al marcar la salida automática", e);
        }
    }

    /**
     * Recupera una lista de {@link Socio}s que actualmente no están presentes en el club.
     * La lista incluye socios que tienen un {@link RegistroEntrada} correspondiente con una {@link RegistroEntrada#getHoraSalida()} nula.
     *
     * @return Una lista de {@link Socio}s que están fuera del club.
     */
public List<Socio> obtenerSociosFuera() {
        List<Socio> sociosFuera = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Usando el Lenguaje de Consultas de Hibernate (HQL) para recuperar socios que no están presentes en el club
            String hql = "FROM Socio s WHERE s.numeroSocio NOT IN (SELECT r.numeroSocio.numeroSocio FROM RegistroEntrada r WHERE r.horaSalida IS NULL)";
            Query<Socio> query = session.createQuery(hql, Socio.class);
            sociosFuera = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener los socios fuera", e);
        }
        return sociosFuera;
    }
    /**
     * Obtiene una lista de {@link Socio}s que actualmente están presentes en el club.
     * La lista incluye socios que tienen un {@link RegistroEntrada} correspondiente con una {@link RegistroEntrada#getHoraSalida()} nula.
     *
     * @return Una lista de {@link Socio}s que están dentro del club.
     */
public List<Socio> obtenerSociosDentro() {
        List<Socio> sociosDentro = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Utilizando el Lenguaje de Consultas de Hibernate (HQL) para recuperar socios que están presentes en el club
            String hql = "SELECT r.numeroSocio FROM RegistroEntrada r WHERE r.horaSalida IS NULL";
            Query<Socio> query = session.createQuery(hql, Socio.class);
            sociosDentro = query.getResultList();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener los socios dentro", e);
        }
        return sociosDentro;
    }
    /**
     * Obtiene un {@link Socio} de la base de datos según su número de socio.
     *
     * @param numeroSocio El número de socio del {@link Socio} que se va a recuperar.
     * @return El {@link Socio} con el número de socio dado, o {@code null} si no se encuentra ninguno.
     */
public Socio obtenerSocioPorNumero(String numeroSocio) {
        Socio socio = null;
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Query<Socio> query = session.createQuery("FROM Socio WHERE numeroSocio = :numeroSocio", Socio.class);
            query.setParameter("numeroSocio", numeroSocio);
            socio = query.uniqueResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error al obtener el socio por número", e);
        }
        return socio;
    }
    /**
     * Obtiene el registro de entrada más reciente de la base de datos.
     *
     * @return El registro de entrada más reciente, o {@code null} si no hay ninguno.
     */
    public RegistroEntrada obtenerUltimaEntrada() {
        RegistroEntrada ultimaEntrada = null;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<RegistroEntrada> query = session.createQuery(
                    "FROM RegistroEntrada ORDER BY fecha DESC, horaEntrada DESC",
                    RegistroEntrada.class
            );
            ultimaEntrada = query.setMaxResults(1).uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener la última entrada", e);
        }
        return ultimaEntrada;
    }

    /**
     * Obtiene el registro de salida más reciente de la base de datos.
     *
     * @return El registro de salida más reciente, o {@code null} si no hay ninguno.
     */
    public RegistroEntrada obtenerUltimaSalida() {
        RegistroEntrada ultimaSalida = null;
        try (Session session = getSessionFactory().openSession()) {
            session.beginTransaction();
            Query<RegistroEntrada> query = session.createQuery(
                    "FROM RegistroEntrada WHERE horaSalida IS NOT NULL ORDER BY fecha DESC, horaSalida DESC",
                    RegistroEntrada.class
            );
            ultimaSalida = query.setMaxResults(1).uniqueResult();
            session.getTransaction().commit();
        } catch (Exception e) {
            logger.error("Error al obtener la última salida", e);
        }
        return ultimaSalida;
    }

    /**
     * Obtiene información completa de aforo con una sola consulta
     */
    public AforoInfo obtenerInfoAforo() {
        int sociosDentro = obtenerTotalSociosDentro();
        int totalSocios = obtenerTotalSocios();
        return new AforoInfo(sociosDentro, totalSocios);
    }

    /**
     * Clase para retornar información de aforo en una sola llamada
     */
    public static class AforoInfo {
        private final int sociosDentro;
        private final int sociosFuera;
        private final int totalSocios;

        public AforoInfo(int sociosDentro, int totalSocios) {
            this.sociosDentro = sociosDentro;
            this.totalSocios = totalSocios;
            this.sociosFuera = totalSocios - sociosDentro;
        }

        // Getters
        public int getSociosDentro() { return sociosDentro; }
        public int getSociosFuera() { return sociosFuera; }
        public int getTotalSocios() { return totalSocios; }
    }

}