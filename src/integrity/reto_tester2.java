package integrity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class reto_tester2 {
    
   public static void main(String[] args) {
        try {
            Hasher.checkIntegrityFile("testFolder", "integrityCheckerFile.txt");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

}
