package co.surveygenerator.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.surveygenerator.entities.Survey;
import co.surveygenerator.repositories.ISurveyRepository;

@Service
public class ImplSurveyService implements ISurveyService {

	@Autowired
	private ISurveyRepository surveyRepository;
	
	@Override
	@Transactional
	public Survey save(Survey survey) {
		return surveyRepository.save(survey);
	}

	@Override
	public List<Survey> findAllSurveyByUserId(Integer userId) {
		return surveyRepository.findAllSurveyByUserId(userId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Survey> findSurveyBycodeSurvey(String codeSurvey) {
		return surveyRepository.findSurveyBycodeSurvey(codeSurvey);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Survey> findAll() {
		return surveyRepository.findAll();
	}


	@Override
	public Survey findById(Integer id) {
		return surveyRepository.findById(id).orElse(null);
	}


	@Override
	public void delete(Integer id) {
		surveyRepository.deleteById(id);
	}
}
