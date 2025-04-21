package org.socialclub.socialclub.util;

import org.socialclub.socialclub.database.RegistroEntradaDAO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase que administra la tarea programada para marcar la salida automática de los usuarios.
 * Utiliza un {@link ScheduledExecutorService} para ejecutar la tarea cada día a las 00:00.
 */
public class SalidaAutomaticaScheduler {
    private static final Logger LOGGER = Logger.getLogger(SalidaAutomaticaScheduler.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    /**
     * Inicia la tarea programada para marcar la salida automática de los usuarios.
     * La tarea se ejecuta cada día a las 00:00, con un retraso inicial calculado para alinearse con la próxima medianoche.
     */
    public void iniciar() {
        long initialDelay = calcularRetrasoInicial();
        long period = 24L * 60 * 60; // Conversión explícita de la multiplicación a "long"

        scheduler.scheduleAtFixedRate(() -> {
            try {
                RegistroEntradaDAO registroDAO = new RegistroEntradaDAO();
                registroDAO.marcarSalidaAutomatica();
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Error al ejecutar marcarSalidaAutomatica()", e);
            }
        }, initialDelay, period, TimeUnit.SECONDS);

        // Agregar un shutdown hook para cerrar correctamente el scheduler
        Runtime.getRuntime().addShutdownHook(new Thread(this::detener));
    }

    /**
     * Calcula el retraso inicial en segundos para alinearse con la próxima medianoche.
     *
     * @return Retraso inicial en segundos.
     */
    private long calcularRetrasoInicial() {
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime medianoche = ahora.plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        return Duration.between(ahora, medianoche).getSeconds();
    }

    /**
     * Detiene la tarea programada y cierra él {@link ScheduledExecutorService}.
     * Si la tarea no termina en 5 segundos, se fuerza su cierre.
     */
    public void detener() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.log(Level.WARNING, "Interrupción al cerrar el scheduler", e);
            // Reinterrumpimos el hilo actual
            Thread.currentThread().interrupt();
        }
    }
}
