// src/main/java/org/socialclub/socialclub/util/EmailUtils.java
package org.socialclub.socialclub.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * Esta clase contiene métodos de utilidad para el manejo de correos electrónicos.
 */
public class EmailUtils {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtils.class);

    /**
     * Constructor privado para evitar la instanciación de la clase.
     */
    private EmailUtils() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad y no debe ser instanciada.");
    }

    /**
     * Envía un correo electrónico al destinatario con las credenciales de acceso.
     *
     * @param to       La dirección de correo electrónico del destinatario.
     * @param password La contraseña del destinatario.
     */
    public static void sendEmail(String to, String password) {
        final String username = "avenidadearcosclubsocial@gmail.com";
        final String passwordEmail = "jhkj apwj jbkd latc";
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true");
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, passwordEmail);
            }
        });
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject("Bienvenido al Club Social Avenida de Arcos");
            message.setText("Estimado socio,\n\n" +
                    "Bienvenido al Club Social Avenida de Arcos.\n\n" +
                    "Sus credenciales de acceso son:\n" +
                    "Usuario: " + to + "\n" +
                    "Contraseña: " + password + "\n\n" +
                    "Saludos,\n" +
                    "El equipo de Club Social Avenida de Arcos");

            Transport.send(message);

        } catch (MessagingException e) {
            logger.error("Error al enviar el correo electrónico", e);
        }
    }
}