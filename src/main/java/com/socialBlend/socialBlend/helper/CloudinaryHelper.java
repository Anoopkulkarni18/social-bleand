package com.socialBlend.socialBlend.helper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Component
public class CloudinaryHelper {

    @Value("${CLOUDINARY_URL}")
    private String url; // Keep field injection

    public String saveImg(MultipartFile file) {
        Cloudinary cloudinary = new Cloudinary(url);
        Map<String, Object> map = null;
        try {
            map = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", "socialBlend"));
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return map != null ? (String) map.get("url") : null; 
    }
    public String updateImg(MultipartFile file) {
        Cloudinary cloudinary = new Cloudinary(url);
        Map<String, Object> map = null;
        try {
            map = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("post", "socialBlend"));
        } catch (Exception e) {
            e.printStackTrace(); // Consider using a logging framework
        }
        return map != null ? (String) map.get("url") : null; // Use the correct key to retrieve the URL
    }
  
}
