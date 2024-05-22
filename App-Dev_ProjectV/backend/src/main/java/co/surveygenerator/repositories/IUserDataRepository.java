package co.surveygenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.surveygenerator.entities.UserData;

public interface IUserDataRepository extends JpaRepository<UserData, Integer> {

}
