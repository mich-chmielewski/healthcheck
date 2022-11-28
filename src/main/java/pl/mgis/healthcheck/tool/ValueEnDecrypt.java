package pl.mgis.healthcheck.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class ValueEnDecrypt {

    //private static final String MY_KEY = "jabadabadoo";
    private static SecretKeySpec secretKey;
    private static byte[] key;
    private static final Logger logger = LoggerFactory.getLogger(ValueEnDecrypt.class);

    public static void setKey(final String myKey) {
        MessageDigest sha;
        try {
            key = myKey.getBytes(StandardCharsets.UTF_8);
            sha = MessageDigest.getInstance("SHA-1");
            key = sha.digest(key);
            key = Arrays.copyOf(key, 16);
            secretKey = new SecretKeySpec(key, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static String encrypt(final String strToEncrypt, String keyCrypt) {
        try {
            setKey(keyCrypt);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            return Base64.getEncoder()
                    .encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            logger.info("Error while encrypting: " + e);
        }
        return null;
    }

    public static String decrypt(final String strToDecrypt, String keyCrypt) {
        try {
            setKey(keyCrypt);
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            return new String(cipher.doFinal(Base64.getDecoder()
                    .decode(strToDecrypt)));
        } catch (Exception e) {
            logger.info("Error while decrypting: " + e);
        }
        return null;
    }
}
