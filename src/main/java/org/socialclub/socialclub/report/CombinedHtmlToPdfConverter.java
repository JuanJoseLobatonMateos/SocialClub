package org.socialclub.socialclub.report;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Clase que combina reportes HTML de pruebas y documentación Javadoc en un único archivo PDF.
 */
public class CombinedHtmlToPdfConverter {

    /**
     * Método principal que genera el PDF combinado.
     *
     * @param args Argumentos de la línea de comandos.
     * @throws Exception Si ocurre algún error durante la generación del PDF.
     */
    public static void main(String[] args) throws Exception {
        String unitTestHtmlPath = "build/reports/tests/test/index.html";
        String integrationTestHtmlPath = "build/reports/tests/integrationTest/index.html";
        String pdfPath = "build/reports/tests/test/combined-test-report.pdf";

        String unitTestHtml = Files.readString(Paths.get(unitTestHtmlPath));
        String integrationTestHtml = Files.readString(Paths.get(integrationTestHtmlPath));

        String testMethodsDoc = TestMethodsDocExtractor.extractJavadocFromTestClasses("src/test/java");

        String combinedHtml = "<html>"
                + "<head>"
                + "<meta http-equiv=\\\"Content-Type\\\" content=\\\"text/html; charset=utf-8\\\"/>"
                + "<title>Combined Test Report</title>"
                + "<style>"
                + "body { font-family: Arial, sans-serif; margin: 20px; }"
                + "h1 { color: #2E86C1; border-bottom: 2px solid #2E86C1; padding-bottom: 5px; }"
                + "h2 { color: #2874A6; }"
                + "h3 { color: #1F618D; margin-bottom: 5px; }"
                + "p { font-size: 14px; line-height: 1.5; }"
                + "table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }"
                + "table, th, td { border: 1px solid #ddd; }"
                + "th, td { padding: 8px; text-align: left; }"
                + "tr:nth-child(even) { background-color: #f9f9f9; }"
                + ".test-doc { margin-bottom: 30px; padding: 10px; border: 1px solid #ccc; border-radius: 5px; }"
                + ".method-doc { margin-left: 20px; margin-bottom: 10px; }"
                + ".test-result.pass { color: green; font-weight: bold; }"
                + ".test-result.fail { color: red; font-weight: bold; }"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<h1>Reporte de Pruebas Unitarias</h1>"
                + unitTestHtml
                + "<h1>Reporte de Pruebas de Integración</h1>"
                + integrationTestHtml
                + "<h1>Documentación de Clases Test</h1>"
                + testMethodsDoc
                + "</body>"
                + "</html>";

        Document parsedDoc = Jsoup.parse(combinedHtml);
        parsedDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String cleanHtml = parsedDoc.html();

        File baseDir = new File("build/reports/tests/test");
        String baseUri = baseDir.toURI().toString();

        try (OutputStream os = new FileOutputStream(pdfPath)) {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(cleanHtml, baseUri);
            builder.toStream(os);
            builder.run();
        }
    }
}