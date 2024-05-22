package co.surveygenerator.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.surveygenerator.entities.SurveyResponse;


public interface ISurveyResponse extends JpaRepository<SurveyResponse, Integer> {
	
	@Query("SELECT sr FROM SurveyResponse sr WHERE sr.codeSurvey = :codeSurvey")
	List<SurveyResponse> findAllSurveyResponses(@Param("codeSurvey") String codeSurvey);
	
	
}
