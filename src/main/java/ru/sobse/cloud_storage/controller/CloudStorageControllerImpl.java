package ru.sobse.cloud_storage.controller;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.sobse.cloud_storage.DTO.FileDTO;
import ru.sobse.cloud_storage.DTO.FileUpdateDTO;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;
import ru.sobse.cloud_storage.service.FileService;
import ru.sobse.cloud_storage.service.UserService;

import java.util.List;

@RestController
public class CloudStorageControllerImpl implements CloudStorageController {
    private final UserService userService;
    private final FileService fileService;

    public CloudStorageControllerImpl(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostMapping("/login")
    @Override
    public TokenDTO login(@RequestBody UserDTO user) {
        return new TokenDTO(userService.login(user));
    }

    @PostMapping("/logout")
    @Override
    public void logout(@RequestHeader(name = "auth-token") String token) {
        userService.logout(token);
    }

    @GetMapping("/ping")
    @Override
    public String ping() {
        return "OK";
    }

    @GetMapping("/list")
    @Override
    public List<FileDTO> list(@RequestParam("limit") int limit) {
        return fileService.list(PageRequest.of(0, limit));
    }

    @PostMapping("/file")
    @Override
    public void create(@RequestParam("filename") String fileName, @RequestParam("file") MultipartFile file) {
        fileService.create(fileName, file);
    }

    @GetMapping("/file")
    @Override
    public Resource read(String filename) {
        return fileService.read(filename);
    }

    @PutMapping("/file")
    @Override
    public void update(@RequestParam("filename") String fileName, @RequestBody FileUpdateDTO fileUpdate) {
        fileService.update(fileName, fileUpdate);
    }

    @DeleteMapping("/file")
    @Override
    public void delete(@RequestParam("filename") String fileName) {
        fileService.delete(fileName);
    }
}
