package helpers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class EnvReader {
    private static Properties props = null;

    public static String readEnv(String key) {
        if (props == null) {
            props = new Properties();
            
            String[] possiblePaths = {".env", "../.env", "Program/.env"};

            String activePath = null;

            for (String path : possiblePaths) {
                File file = new File(path);
                if (file.exists() && !file.isDirectory()) { activePath = path; break; }
            }

            if (activePath != null) {
                try (FileInputStream fis = new FileInputStream(activePath)) { props.load(fis); }
                catch (IOException e) { System.out.println("Gagal baca file .env: " + e.getMessage()); }
            } else System.out.println("Peringatan: File .env tidak ditemukan.");
        }

        return props.getProperty(key);
    }
}