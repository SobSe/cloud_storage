package ru.sobse.cloud_storage.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Getter
public class FileDownloadDTO {
    private MultipartFile file;
}
