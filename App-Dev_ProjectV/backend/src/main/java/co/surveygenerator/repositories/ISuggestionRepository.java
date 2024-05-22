package co.surveygenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.surveygenerator.entities.Suggestion;

public interface ISuggestionRepository extends JpaRepository<Suggestion, Integer>{

}
