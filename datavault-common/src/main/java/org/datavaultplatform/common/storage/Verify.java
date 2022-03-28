package org.datavaultplatform.common.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

public class Verify {
    
    private static final String algorithm = "SHA-1";
    
    public enum Method {LOCAL_ONLY, COPY_BACK, CLOUD};
    
    public static String getDigest(File file) throws Exception {

        MessageDigest sha1 = MessageDigest.getInstance(algorithm);
        
        try (InputStream is = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int len = is.read(buffer);

            while (len != -1) {
                sha1.update(buffer, 0, len);
                len = is.read(buffer);
            }
            
            return new HexBinaryAdapter().marshal(sha1.digest());
        }
    }
    
    public static String getAlgorithm() {
        return algorithm;
    }

}
