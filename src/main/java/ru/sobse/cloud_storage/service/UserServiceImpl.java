package ru.sobse.cloud_storage.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import ru.sobse.cloud_storage.DTO.TokenDTO;
import ru.sobse.cloud_storage.DTO.UserDTO;
import ru.sobse.cloud_storage.entity.UserDAO;
import ru.sobse.cloud_storage.exeption.TokenNotFound;
import ru.sobse.cloud_storage.repositories.UserRepo;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepo repository;
    private final AuthenticationManager authenticationManager;
    private final TokenGenerator tokenGenerator;

    public UserServiceImpl(UserRepo repository, AuthenticationManager authenticationManager, TokenGenerator tokenGenerator) {
        this.repository = repository;
        this.authenticationManager = authenticationManager;
        this.tokenGenerator = tokenGenerator;
    }

    @Override
    public String login(UserDTO userDTO) {
        Authentication authenticate = authenticationManager
                .authenticate(
                        new UsernamePasswordAuthenticationToken(
                                userDTO.getLogin(), userDTO.getPassword()
                        )
                );

        UserDAO user = (UserDAO) authenticate.getPrincipal();
        String token = tokenGenerator.generate();
        user.setToken(token);
        repository.save(user);
        return token;
    }

    @Override
    public void logout(String token) {
        UserDAO user = repository.getUserByToken(token
                .split(" ")[1])
                .orElseThrow(() -> new TokenNotFound("Token not found"));
        user.setToken(null);
        repository.save(user);
    }

    @Override
    public Optional<UserDAO> findByToken(TokenDTO token) {
        return repository.getUserByToken(token.getAuthToken());
    }
}
