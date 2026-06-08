package com.lubimyczytac.LubimyCzytac.Services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Map<String, Object> params = ObjectUtils.asMap(
                "folder", "user_avatars",
                "public_id", "user_" + userId + "_" + System.currentTimeMillis()
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    public String uploadBookCover(MultipartFile file, Long bookId) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        Map<String, Object> params = ObjectUtils.asMap(
                "folder", "book_covers",
                "public_id", "book_" + bookId + "_" + System.currentTimeMillis()
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return uploadResult.get("secure_url").toString();
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty() ||
                imageUrl.equals("📷") || !imageUrl.contains("cloudinary.com")) {
            return;
        }

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                System.out.println("Usunięto obraz z Cloudinary: " + publicId);
            }
        } catch (Exception e) {
            System.err.println("Nie udało się usunąć obrazu z Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            if (parts.length < 2) return null;

            int uploadIndex = -1;
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("upload")) {
                    uploadIndex = i;
                    break;
                }
            }

            if (uploadIndex == -1 || uploadIndex + 1 >= parts.length) {
                return null;
            }

            int startIndex = uploadIndex + 1;
            if (startIndex < parts.length && parts[startIndex].matches("v\\d+")) {
                startIndex++;
            }

            if (startIndex >= parts.length) {
                return null;
            }

            StringBuilder publicIdBuilder = new StringBuilder();
            for (int i = startIndex; i < parts.length - 1; i++) {
                if (publicIdBuilder.length() > 0) {
                    publicIdBuilder.append("/");
                }
                publicIdBuilder.append(parts[i]);
            }

            String filename = parts[parts.length - 1];
            String filenameWithoutExt = filename.contains(".") ?
                    filename.substring(0, filename.lastIndexOf(".")) : filename;

            if (publicIdBuilder.length() > 0) {
                publicIdBuilder.append("/");
            }
            publicIdBuilder.append(filenameWithoutExt);

            return publicIdBuilder.toString();
        } catch (Exception e) {
            System.err.println("Błąd podczas wyodrębniania public_id: " + e.getMessage());
            return null;
        }
    }
}