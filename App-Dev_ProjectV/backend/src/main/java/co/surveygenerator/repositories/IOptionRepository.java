package co.surveygenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.surveygenerator.entities.Option;

public interface IOptionRepository extends JpaRepository<Option, Integer> {

}