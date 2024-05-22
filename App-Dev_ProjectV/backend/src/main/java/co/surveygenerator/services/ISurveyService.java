package co.surveygenerator.services;

import java.util.List;
import java.util.Optional;

import co.surveygenerator.entities.Survey;

public interface ISurveyService {
	
	public List<Survey> findAll();

	public Survey findById(Integer id);
	
	public Optional<Survey> findSurveyBycodeSurvey(String codeSurvey);
		
	public List<Survey> findAllSurveyByUserId(Integer integer);
	
	public Survey save(Survey survey);
	
	public void delete(Integer id);
	
	
}
