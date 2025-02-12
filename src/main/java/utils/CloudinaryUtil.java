package utils;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.cloudinary.utils.StringUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class CloudinaryUtil {
    private static final String CLOUD_NAME = "djq1slhwp";
    private static final String API_KEY = "291691227825547";
    private static final String API_SECRET = "EznEyZx1XyoHk_zgbbEUNpG8BHA";
    private static final Cloudinary cloudinary;
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    
    static {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", CLOUD_NAME);
        config.put("api_key", API_KEY);
        config.put("api_secret", API_SECRET);
        
        cloudinary = new Cloudinary(config);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    private static String generateSignature(Map<String, Object> params) throws Exception {
        // Sort parameters alphabetically
        TreeMap<String, Object> sortedParams = new TreeMap<>(params);
        
        // Build string to sign according to Cloudinary's specification
        StringBuilder stringToSign = new StringBuilder();
        for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
            if (entry.getValue() == null) continue;
            if (stringToSign.length() > 0) stringToSign.append('&');
            stringToSign.append(entry.getKey()).append('=').append(entry.getValue().toString());
        }
        
        System.out.println("String to sign: " + stringToSign.toString());
        
        // Generate signature using SHA-256
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(API_SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        
        byte[] hash = sha256_HMAC.doFinal(stringToSign.toString().getBytes(StandardCharsets.UTF_8));
        // Use hex encoding instead of Base64
        return bytesToHex(hash);
    }
    
    public static String uploadImage(byte[] imageData, String folderName) throws IOException {
        if (imageData == null || imageData.length == 0) {
            return null;
        }
        
        try {
            long timestamp = System.currentTimeMillis() / 1000L;
            
            Map<String, Object> params = new HashMap<>();
            params.put("timestamp", timestamp);
            params.put("folder", folderName);
            
            String signature = generateSignature(params);
            
            Map<String, Object> uploadParams = new HashMap<>();
            uploadParams.put("timestamp", timestamp);
            uploadParams.put("folder", folderName);
            uploadParams.put("signature", signature);
            uploadParams.put("api_key", API_KEY);
            
            System.out.println("Upload params: " + uploadParams);
            
            Map result = cloudinary.uploader().upload(imageData, uploadParams);
            return (String) result.get("secure_url");
            
        } catch (Exception e) {
            System.out.println("Upload error: " + e.getMessage());
            e.printStackTrace();
            throw new IOException("Failed to upload image: " + e.getMessage());
        }
    }
    
    public static void deleteImage(String publicId) throws IOException {
        if (publicId != null && !publicId.isEmpty()) {
            try {
                Map<String, Object> deleteParams = new HashMap<>();
                long timestamp = System.currentTimeMillis() / 1000L;
                deleteParams.put("timestamp", timestamp);
                
                String signature = generateSignature(deleteParams);
                deleteParams.put("signature", signature);
                deleteParams.put("api_key", API_KEY);
                
                cloudinary.uploader().destroy(publicId, deleteParams);
            } catch (Exception e) {
                System.out.println("Delete error: " + e.getMessage());
                e.printStackTrace();
                throw new IOException("Failed to delete image: " + e.getMessage());
            }
        }
    }
    
    public static String getPublicIdFromUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        try {
            String[] urlParts = url.split("/");
            String fileName = urlParts[urlParts.length - 1];
            String folder = urlParts[urlParts.length - 2];
            
            if (fileName.contains(".")) {
                fileName = fileName.substring(0, fileName.lastIndexOf('.'));
            }
            
            return folder + "/" + fileName;
        } catch (Exception e) {
            return null;
        }
    }
}