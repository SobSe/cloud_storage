package ru.sobse.cloud_storage.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sobse.cloud_storage.entity.UserDAO;

import java.util.Optional;

@Repository
public interface UserRepo extends CrudRepository<UserDAO, Integer> {
    Optional<UserDAO> getUserByLogin(String login);

    Optional<UserDAO> getUserByToken(String token);
}
