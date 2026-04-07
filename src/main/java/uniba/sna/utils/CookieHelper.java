package uniba.sna.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public class CookieHelper {
    
    private static final String ALGORITHM = "AES";
    
    // Variabile statica per mantenere la chiave in memoria finché il server è acceso
    private static SecretKeySpec skeySpec;

    // essendo "static" viene eseguito una sola volta quando l'applicazione si avvia
    static {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance(ALGORITHM);
            kgen.init(128); // 192 and 256 bits may be unavailable
            
            SecretKey skey = kgen.generateKey();
            byte[] raw = skey.getEncoded();
            
            skeySpec = new SecretKeySpec(raw, ALGORITHM);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Errore critico: Impossibile generare la chiave AES");
        }
    }

    /**
     * Cifra una stringa usando AES e la chiave generata dinamicamente.
     */
    public static String encrypt(String strToBeEncrypted) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        
        // Encode bytes as UTF8; strToBeEncrypted contains the
        // input string that is to be encrypted
        byte[] encoded = strToBeEncrypted.getBytes("UTF8");
        
        // Perform encryption
        byte[] encrypted = cipher.doFinal(encoded);
        
        // Convertiamo i byte cifrati in una stringa Base64 sicura per i cookie
        return Base64.getEncoder().encodeToString(encrypted); 
    }

    /**
     * Decifra la stringa dal cookie per recuperare il valore originale.
     */
    public static String decrypt(String encryptedBase64) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        
        // Decode bytes in Base64
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedBase64);
        // Perform decryption
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        
        return new String(decryptedBytes, "UTF8");
    }
}