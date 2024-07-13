package util;

public class Util {
    
public static String byteArrayToHexString(byte[] bytes, String separator){

    String result = "";
    
    for (int i =0; i<bytes.length; i++){
        result += String.format("%02x", bytes[i]) + separator;
    }

return result.toString();    
}


}
