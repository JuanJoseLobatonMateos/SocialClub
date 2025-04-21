package org.socialclub.socialclub.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Clase de utilidad para administrar instancias de SessionFactory de Hibernate.
 * Esta clase está diseñada como singleton, proporcionando un punto de acceso único a la SessionFactory.
 * También proporciona un método para cerrar la SessionFactory cuando la aplicación se está apagando.
 */
public class HibernateUtil {

    /**
     * Instancia de SessionFactory estática que se inicializa de manera perezosa.
     */
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Constructor privado para evitar la instanciación de la clase.
     * Lanza una UnsupportedOperationException para indicar que esta clase no está diseñada para ser instanciada.
     */
    private HibernateUtil() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Construye e inicializa una instancia de SessionFactory utilizando el archivo de configuración "hibernate.cfg.xml".
     *
     * @return La instancia de SessionFactory inicializada.
     * @throws ExceptionInInitializerError Si se produce un error durante la creación de la SessionFactory.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            return new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Exception ex) {
            throw new ExceptionInInitializerError("Error en la creación inicial de la SessionFactory." + ex);
        }
    }

    /**
     * Devuelve la instancia singleton de SessionFactory.
     *
     * @return La instancia de SessionFactory.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Cierra la instancia de SessionFactory cuando la aplicación se está apagando.
     * Este método debe llamarse al final del ciclo de vida de la aplicación.
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}