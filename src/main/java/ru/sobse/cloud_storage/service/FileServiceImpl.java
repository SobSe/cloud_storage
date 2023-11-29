package ru.sobse.cloud_storage.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.sobse.cloud_storage.DTO.FileDTO;
import ru.sobse.cloud_storage.DTO.FileUpdateDTO;
import ru.sobse.cloud_storage.entity.FileDAO;
import ru.sobse.cloud_storage.entity.UserDAO;
import ru.sobse.cloud_storage.exeption.FileNotFound;
import ru.sobse.cloud_storage.repositories.FileRepo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class FileServiceImpl implements FileService {
    private final FileRepo repository;

    public FileServiceImpl(FileRepo repository) {
        this.repository = repository;
    }

    @Override
    public List<FileDTO> list(Pageable paging) {
        List<FileDAO> files = repository.findByUser(getAuthenticatedUser(), paging);
        List<FileDTO> filesDTO = new ArrayList<>(files.size());
        for (FileDAO file : files) {
            filesDTO.add(new FileDTO(file.getFileName(), file.getSize()));
        }
        return filesDTO;
    }

    @Override
    public void create(String fileName, MultipartFile file) {
        try {
            byte[] date = file.getBytes();
            long size = file.getSize();
            repository.save(new FileDAO(0
                    , fileName
                    , date
                    , size
                    , getAuthenticatedUser()));
        } catch (IOException e) {
            throw new RuntimeException("Error to store file.");
        }
    }

    @Override
    public Resource read(String fileName) {
        FileDAO fileDAO = repository
                .findByFileNameAndUser(fileName, getAuthenticatedUser())
                .orElseThrow(() -> new FileNotFound("File not found"));
        return new ByteArrayResource(fileDAO.getData());
    }

    @Override
    public void update(String fileName, FileUpdateDTO fileUpdateDTO) {
        FileDAO fileDAO = repository
                .findByFileNameAndUser(fileName, getAuthenticatedUser())
                .orElseThrow(() -> new FileNotFound("File not found"));
        fileDAO.setFileName(fileUpdateDTO.getFileName());
        repository.save(fileDAO);
    }

    @Override
    public void delete(String fileName) {
        FileDAO fileDAO = repository
                .findByFileNameAndUser(fileName, getAuthenticatedUser())
                .orElseThrow(() -> new FileNotFound("File not found"));
        repository.delete(fileDAO);
    }

    private UserDAO getAuthenticatedUser() {
        return (UserDAO) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
