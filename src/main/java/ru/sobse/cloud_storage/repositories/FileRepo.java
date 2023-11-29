package ru.sobse.cloud_storage.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.sobse.cloud_storage.entity.FileDAO;
import ru.sobse.cloud_storage.entity.UserDAO;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepo extends CrudRepository<FileDAO, Long> {
    List<FileDAO> findByUser(UserDAO userDAO, Pageable pageable);

    Optional<FileDAO> findByFileNameAndUser(String fileName, UserDAO user);
}
