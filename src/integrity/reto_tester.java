package integrity;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class reto_tester {
    public static void main(String[] args) {
        try {
            Hasher.generateIntegrityCheckerFile("testFolder", "integrityCheckerFile.txt");
            System.out.println("Integrity checker file generated successfully.");
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
