package co.surveygenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import co.surveygenerator.entities.Question;

public interface IQuestionRepository extends JpaRepository<Question, Integer> {

}
