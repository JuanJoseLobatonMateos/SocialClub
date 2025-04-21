package org.socialclub.socialclub.util;

/**
 * Una clase de utilidad para administrar sesiones de usuario en la aplicación.
 * Esta clase utiliza el patrón Singleton para asegurarse de que solo se cree una instancia de SessionManager.
 */
public class SessionManager {
    /**
     * El ID del empleado autenticado.
     */
    private Integer authenticatedEmployeeId;

    /**
     * Constructor privado para evitar la instanciación desde fuera de la clase.
     */
    private SessionManager() {
    }

    /**
     * Una clase anidada estática para mantener la única instancia de SessionManager.
     */
    private static class SessionManagerHolder {
        /**
         * La única instancia de SessionManager.
         */
        private static final SessionManager INSTANCE = new SessionManager();
    }

    /**
     * Devuelve la única instancia de SessionManager.
     *
     * @return la única instancia de SessionManager
     */
    public static SessionManager getInstance() {
        return SessionManagerHolder.INSTANCE;
    }

    /**
     * Devuelve el ID del empleado autenticado.
     *
     * @return el ID del empleado autenticado, o null si no hay ningún empleado autenticado
     */
    public Integer getAuthenticatedEmployeeId() {
        return authenticatedEmployeeId;
    }

    /**
     * Establece el ID del empleado autenticado.
     *
     * @param authenticatedEmployeeId el ID del empleado autenticado
     */
    public void setAuthenticatedEmployeeId(Integer authenticatedEmployeeId) {
        this.authenticatedEmployeeId = authenticatedEmployeeId;
    }
}