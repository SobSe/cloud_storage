package ru.sobse.cloud_storage.service;

import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;
import ru.sobse.cloud_storage.entity.UserDAO;

import java.util.Optional;

public interface UserService {

    String login(UserDTO user);

    Optional<UserDAO> findByToken(TokenDTO token);

    void logout(String token);
}
