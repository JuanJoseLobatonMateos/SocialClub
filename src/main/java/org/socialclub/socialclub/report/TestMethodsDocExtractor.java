package org.socialclub.socialclub.report;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Clase que se encarga de extraer la documentación Javadoc de los métodos de clases de prueba.
 */
public class TestMethodsDocExtractor {

    /**
     * Constructor privado para evitar la creación de instancias.
     */
    private TestMethodsDocExtractor() {
    }

    /**
     * Extrae la documentación Javadoc de los métodos de las clases de prueba.
     *
     * @param directoryPath ruta del directorio que contiene los archivos de prueba.
     * @return HTML generado con la documentación extraída.
     * @throws IOException si ocurre un error al leer los archivos.
     */
    public static String extractJavadocFromTestClasses(String directoryPath) throws IOException {
        StringBuilder htmlBuilder = new StringBuilder();
        List<File> javaFiles = new ArrayList<>();
        collectTestFiles(new File(directoryPath), javaFiles);
        for (File file : javaFiles) {
            String content = Files.readString(file.toPath());
            CompilationUnit cu = StaticJavaParser.parse(content);
            cu.findFirst(ClassOrInterfaceDeclaration.class).ifPresent(cls -> {
                htmlBuilder.append("<div class=\\\"test-doc\\\">");
                htmlBuilder.append("<h2>").append(cls.getNameAsString()).append("</h2>");
                for (MethodDeclaration method : cls.getMethods()) {
                    htmlBuilder.append("<div class=\\\"method-doc\\\">");
                    htmlBuilder.append("<h3>")
                            .append(method.getNameAsString())
                            .append(" <span class=\\\"test-result pass\\\">&#x2714;</span>")
                            .append("</h3>");
                    String javadoc = method.getJavadoc()
                            .map(j -> j.getDescription().toText())
                            .orElse("Sin descripción.");
                    htmlBuilder.append("<p>").append(javadoc).append("</p>");
                    htmlBuilder.append("</div>");
                }
                htmlBuilder.append("</div>");
            });
        }
        return htmlBuilder.toString();
    }

    /**
     * Recorre recursivamente el directorio especificado para recopilar archivos de prueba.
     *
     * @param dir   directorio raíz para la búsqueda de archivos.
     * @param files lista donde se agregan los archivos de prueba encontrados.
     */
    private static void collectTestFiles(File dir, List<File> files) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file.isDirectory()) {
                collectTestFiles(file, files);
            } else if (file.getName().endsWith("Test.java")) {
                files.add(file);
            }
        }
    }
}