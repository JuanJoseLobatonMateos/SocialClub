package org.socialclub.socialclub.util;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.socialclub.socialclub.database.EmpleadoDAO;
import org.socialclub.socialclub.model.Empleado;
import org.socialclub.socialclub.model.Socio;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase utilitaria para generar y mostrar reportes utilizando JasperReports.
 * Proporciona métodos para imprimir diferentes tipos de reportes, como carnets de socios,
 * reportes de empleados, reportes de socios, reportes de registros y reportes de reservas.
 */
public class Reportes {
    private static final String LOGO_PATH = "/images/logo.png";
    private static final String LOGO_NOT_FOUND_MSG = "No se pudo encontrar la imagen en el classpath.";
    private static final String RUTA_IMAGEN_PARAM = "RUTA_IMAGEN";
    private static final Logger logger = LoggerFactory.getLogger(Reportes.class);

    private Reportes() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**
     * Genera un reporte utilizando el archivo Jasper especificado, el origen de datos y los parámetros.
     *
     * @param reportPath Ruta al archivo Jasper.
     * @param dataSource Origen de datos para el reporte.
     * @param parametros Parámetros para el reporte.
     */
    private static void generarReporte(String reportPath, JRBeanCollectionDataSource dataSource, Map<String, Object> parametros) {
        try {
            InputStream inputStream = Reportes.class.getResourceAsStream(reportPath);
            if (inputStream == null) {
                throw new IllegalArgumentException("No se pudo encontrar el archivo '" + reportPath + "' en el classpath.");
            }
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            viewer.setVisible(true);
        } catch (Exception e) {
            logger.error("Ocurrió un error al generar el reporte", e);
            Throwable cause = e.getCause();
            while (cause != null) {
                logger.error("Causa: {}", cause.getMessage());
                cause = cause.getCause();
            }
        }
    }

    /**
     * Obtiene una imagen desde el classpath.
     *
     * @param path Ruta a la imagen en el classpath.
     * @return La imagen obtenida.
     * @throws ImagenNoEncontradaException Si la imagen no se encuentra en el classpath.
     */
    private static BufferedImage obtenerImagen(String path) throws ImagenNoEncontradaException {
        try {
            InputStream imagenInputStream = Reportes.class.getResourceAsStream(path);
            if (imagenInputStream == null) {
                throw new ImagenNoEncontradaException(LOGO_NOT_FOUND_MSG);
            }
            return ImageIO.read(imagenInputStream);
        } catch (Exception e) {
            throw new ImagenNoEncontradaException("Error al cargar la imagen: " + e.getMessage());
        }
    }

    /**
     * Imprime carnets de socios utilizando el reporte Jasper "carnetsSocios.jasper".
     *
     * @param sociosList Lista de socios para imprimir en los carnets.
     */
    public static void imprimirCarnets(List<Socio> sociosList) {
        try {
            BufferedImage imagen1 = obtenerImagen(LOGO_PATH);
            BufferedImage imagen2 = obtenerImagen("/images/logo_carnet.png");
            BufferedImage imagenConOpacidad = aplicarOpacidad(imagen2);

            HashMap<String, Object> param = new HashMap<>();
            param.put(RUTA_IMAGEN_PARAM, imagen1);
            param.put("RUTA_IMAGEN_CARNET", imagenConOpacidad);

            generarReporte("/reports/carnetsSocios.jasper", new JRBeanCollectionDataSource(sociosList), param);
        } catch (Exception e) {
            logger.error("Error al imprimir carnets", e);
        }
    }

    /**
     * Imprime un reporte de empleados utilizando el reporte Jasper "reporteEmpleados.jasper".
     */
    public static void imprimirReporteEmpleados() {
        try {
            EmpleadoDAO empleadoDAO = new EmpleadoDAO();
            List<Empleado> empleadosList = empleadoDAO.obtenerTodosEmpleados();
            if (empleadosList == null || empleadosList.isEmpty()) {
                logger.warn("La lista de empleados está vacía.");
                return;
            }

            HashMap<String, Object> param = new HashMap<>();
            param.put(RUTA_IMAGEN_PARAM, obtenerImagen(LOGO_PATH));

            generarReporte("/reports/reporteEmpleados.jasper", new JRBeanCollectionDataSource(empleadosList), param);
        } catch (Exception e) {
            logger.error("Error al imprimir reporte de empleados", e);
        }
    }

    /**
     * Imprime un reporte de socios utilizando el reporte Jasper "reporteSocios.jasper".
     *
     * @param sociosList Lista de socios para imprimir en el reporte.
     */
    public static void imprimirReporteSocios(List<Socio> sociosList) {
        try {
            HashMap<String, Object> param = new HashMap<>();
            param.put(RUTA_IMAGEN_PARAM, obtenerImagen(LOGO_PATH));

            generarReporte("/reports/reporteSocios.jasper", new JRBeanCollectionDataSource(sociosList), param);
        } catch (Exception e) {
            logger.error("Error al imprimir reporte de socios", e);
        }
    }

    /**
     * Imprime un reporte de registros utilizando el reporte Jasper "reporteRegistros.jasper".
     *
     * @param registroMapList Lista de mapas con los datos de los registros para imprimir en el reporte.
     * @param parametros Parámetros adicionales para el reporte.
     */
    public static void imprimirReporteRegistros(List<Map<String, Object>> registroMapList, Map<String, Object> parametros) {
        try {
            parametros.put(RUTA_IMAGEN_PARAM, obtenerImagen(LOGO_PATH));

            generarReporte("/reports/reporteRegistros.jasper", new JRBeanCollectionDataSource(registroMapList), parametros);
        } catch (Exception e) {
            logger.error("Error al imprimir reporte de registros", e);
        }
    }

    /**
     * Imprime un reporte de reservas utilizando el reporte Jasper "reporteReservas.jasper".
     *
     * @param reservasMap Lista de mapas con los datos de las reservas para imprimir en el reporte.
     * @param parametros Parámetros adicionales para el reporte.
     */
    public static void imprimirReporteReservas(List<Map<String, Object>> reservasMap, Map<String, Object> parametros) {
        try {
            parametros.put(RUTA_IMAGEN_PARAM, obtenerImagen(LOGO_PATH));

            generarReporte("/reports/reporteReservas.jasper", new JRBeanCollectionDataSource(reservasMap), parametros);
        } catch (Exception e) {
            logger.error("Error al imprimir reporte de reservas", e);
        }
    }

    /**
     * Aplica una opacidad a la imagen original.
     *
     * @param imagenOriginal Imagen original.
     * @return La imagen con opacidad aplicada.
     */
    private static BufferedImage aplicarOpacidad(BufferedImage imagenOriginal) {
        BufferedImage imagenConOpacidad = new BufferedImage(
                imagenOriginal.getWidth(),
                imagenOriginal.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g2d = imagenConOpacidad.createGraphics();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        g2d.drawImage(imagenOriginal, 0, 0, null);
        g2d.dispose();
        return imagenConOpacidad;
    }

    /**
     * Excepción lanzada cuando no se encuentra una imagen en el classpath.
     */
    public static class ImagenNoEncontradaException extends Exception {
        public ImagenNoEncontradaException(String message) {
            super(message);
        }
    }
}
