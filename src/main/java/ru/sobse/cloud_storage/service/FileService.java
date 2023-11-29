package ru.sobse.cloud_storage.service;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import ru.sobse.cloud_storage.DTO.FileDTO;
import ru.sobse.cloud_storage.DTO.FileUpdateDTO;

import java.util.List;

public interface FileService {
    List<FileDTO> list(Pageable paging);

    void create(String fileName, MultipartFile file);

    Resource read(String fileName);

    void update(String fileName, FileUpdateDTO fileUpdateDTO);

    void delete(String fileName);
}
