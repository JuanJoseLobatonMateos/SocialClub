package org.socialclub.socialclub.util;
import org.mindrot.jbcrypt.BCrypt;
/**
 * Clase de utilidad para el cifrado y verificación de contraseñas utilizando BCrypt.
 * Esta clase proporciona métodos para cifrar una contraseña en plano y verificar una contraseña en plano contra una contraseña cifrada.
 */
public class EncriptadorUtil {

    private EncriptadorUtil() {
        throw new UnsupportedOperationException("Clase de utilidad");
    }

    /**z
     * Cifra una contraseña en plano utilizando BCrypt.
     *
     * @param plainPassword La contraseña en plano a cifrar.
     * @return La contraseña cifrada.
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifica una contraseña en plano contra una contraseña cifrada utilizando BCrypt.
     *
     * @param plainPassword La contraseña en plano a verificar.
     * @param hashedPassword La contraseña cifrada para verificar contra.
     * @return Verdadero si la contraseña en plano coincide con la contraseña cifrada, falso en caso contrario.
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}