package ru.sobse.cloud_storage.controller;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import ru.sobse.cloud_storage.DTO.FileDTO;
import ru.sobse.cloud_storage.DTO.FileUpdateDTO;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;

import java.util.List;

public interface CloudStorageController {
    TokenDTO login(UserDTO user);

    void logout(String token);

    String ping();

    List<FileDTO> list(int limit);

    void create(String fileName, MultipartFile file);

    Resource read(String filename);

    void update(String fileName, FileUpdateDTO fileUpdate);

    void delete(String fileName);
}
