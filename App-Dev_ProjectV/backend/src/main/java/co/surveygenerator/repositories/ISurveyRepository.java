package co.surveygenerator.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.surveygenerator.entities.Survey;

public interface ISurveyRepository extends JpaRepository<Survey, Integer> {

	@Query("SELECT s FROM Survey s WHERE s.userData.id = :userId")
	List<Survey> findAllSurveyByUserId(@Param("userId") Integer userId);
	
	@Query("SELECT s FROM Survey s WHERE s.codeSurvey = :codeSurvey")
	Optional<Survey> findSurveyBycodeSurvey(@Param("codeSurvey") String codeSurvey);
	
}
