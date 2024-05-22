package co.surveygenerator.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.surveygenerator.security.entities.User;


public interface IUserRepository extends JpaRepository<User, Integer>{

	Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}

