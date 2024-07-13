package integrity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import util.Util;


public class Hasher {

public static String getHash(String input, String algorithm) throws Exception {

byte[] inputBA= input.getBytes();

MessageDigest hasher = MessageDigest.getInstance(algorithm);
hasher.update(inputBA);

return Util.byteArrayToHexString(hasher.digest(), "");

}

public static String getHashFile(String filename, String algorithm) throws Exception {
    MessageDigest hasher = MessageDigest.getInstance(algorithm);

    FileInputStream fis = new FileInputStream(filename);
    byte[] buffer =  new byte[1024];

    int in;
    while((in= fis.read(buffer)) != -1 ){
        hasher.update(buffer, 0, in);
    }
    fis.close();
    return Util.byteArrayToHexString(hasher.digest(), "");
}


//reto programacion

   public static void generateIntegrityCheckerFile(String folderName, String outputFileName) throws IOException, NoSuchAlgorithmException {
        File folder = new File(folderName);
        if (!folder.exists() || !folder.isDirectory()) { //verifica si la carpeta existe
            throw new IllegalArgumentException("The folder does not exist or is not a directory.");
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) { //verifica si el archivo existe
            throw new IllegalArgumentException("The folder is empty.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName))) {
            for (File file : files) {
                if (file.isFile()) {
                    String hash = calculateHash(file);
                    writer.write(hash + " *" + file.getName()); //agrega el espacio y el asterisco despues del hash y posteriormente el nombre
                    writer.newLine();
                }
            }
        }
    }

     private static String calculateHash(File file) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int n;
            while ((n = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, n);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

     public static void checkIntegrityFile(String folderName, String integrityFileName) throws IOException, NoSuchAlgorithmException {
        File integrityFile = new File(integrityFileName);
        if (!integrityFile.exists()) {
            throw new IllegalArgumentException("The integrity file does not exist.");
        }

        Map<String, String> expectedHashes = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(integrityFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ", 2);
                if (parts.length == 2) {
                    String hash = parts[0];
                    String fileName = parts[1].substring(1); // Remove leading space or '*'
                    expectedHashes.put(fileName, hash);
                }
            }
        }

        File folder = new File(folderName);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("The folder does not exist or is not a directory.");
        }

        int missingFiles = 0;
        int mismatchedFiles = 0;

        for (Map.Entry<String, String> entry : expectedHashes.entrySet()) {
            String fileName = entry.getKey();
            String expectedHash = entry.getValue();

            File file = new File(folder, fileName);
            if (file.exists()) {
                String actualHash = calculateHash(file);
                if (actualHash.equals(expectedHash)) {
                    System.out.println(fileName + ": OK");
                } else {
                    System.out.println(fileName + ": FAILED");
                    mismatchedFiles++;
                }
            } else {
                System.out.println(fileName + ": MISSING");
                missingFiles++;
            }
        }

        if (missingFiles > 0 || mismatchedFiles > 0) {
            System.out.println("Advertencia: hubo errores de verificación de integridad.");
        } else {
            System.out.println("Todos los archivos pasaron la verificación de integridad.");
        }
    }

}
