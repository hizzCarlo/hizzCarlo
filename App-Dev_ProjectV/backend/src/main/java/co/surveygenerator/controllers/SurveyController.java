package co.surveygenerator.controllers;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.surveygenerator.dto.Message;
import co.surveygenerator.dto.SurveyDto;
import co.surveygenerator.entities.Category;
import co.surveygenerator.entities.Option;
import co.surveygenerator.entities.Question;
import co.surveygenerator.entities.Suggestion;
import co.surveygenerator.entities.Survey;
import co.surveygenerator.entities.SurveyResponse;
import co.surveygenerator.repositories.ICategoryRepository;
import co.surveygenerator.repositories.IOptionRepository;
import co.surveygenerator.repositories.IQuestionRepository;
import co.surveygenerator.repositories.ISuggestionRepository;
import co.surveygenerator.repositories.ISurveyResponse;
import co.surveygenerator.services.ISurveyService;
import co.surveygenerator.services.IUserDataService;

@RestController
@RequestMapping("/surveygenerator/surveys")
@CrossOrigin(origins = "*")
public class SurveyController {

	@Autowired
	private IUserDataService userDataService;

	@Autowired
	private ISurveyService surveyService;

	@Autowired
	private IQuestionRepository questionRepository;

	@Autowired
	private IOptionRepository optionRepository;

	@Autowired
	private ICategoryRepository categoryRepository;

	@Autowired
	private ISurveyResponse surveyResponse;
	
	@Autowired
	private ISuggestionRepository suggestionRepository;

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/findall")
	public List<Survey> findAllByIdUser() {
		return surveyService.findAllSurveyByUserId(userDataService.getCurrentUserId());
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/findbyid/{id}")
	public Survey findById(@PathVariable Integer id) {
		return surveyService.findById(id);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/findbycategory/{id}")
	public List<Survey> findByCategory(@PathVariable Integer id) {
		Optional<Category> found = categoryRepository.findById(id);
		List<Survey> survey = surveyService.findAllSurveyByUserId(userDataService.getCurrentUserId());

		if (found.isPresent()) {
			Category category = found.get();
			return survey.stream().filter(data -> data.getCategory().equals(category)).toList();

		} else {
			return survey;
		}
	}

	@GetMapping("/findbycodesurvey/{codeSurvey}")
	public Survey findByCodeSurvey(@PathVariable String codeSurvey) {
		Optional<Survey> found = surveyService.findSurveyBycodeSurvey(codeSurvey);
		Survey survey = found.get();

		if (found.isPresent()) {
			return survey;
		}

		return survey;
	}

	@PostMapping("/surveyresponse/{codeSurvey}")
	public void saveResponsesUser(@PathVariable String codeSurvey, @RequestBody SurveyResponse jsonResponse) {
		SurveyResponse newResponses = new SurveyResponse();
		newResponses.setRespondentName(jsonResponse.getRespondentName());
		newResponses.setCodeSurvey(codeSurvey);
		newResponses.setEmail(jsonResponse.getEmail());
		newResponses.setCodeOption(jsonResponse.getCodeOption());

		surveyResponse.save(newResponses);

	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/create")
	public ResponseEntity<Message> create(@RequestBody SurveyDto surveyDto) {
		Survey newSurvey = new Survey();
		newSurvey.setTitle(surveyDto.getTitle());
		// newSurvey.setSubTitle(surveyDto.getSubTitle());
		newSurvey.setDescription(surveyDto.getDescription());
		newSurvey.setUserData(userDataService.findById(userDataService.getCurrentUserId()));
		newSurvey.setCodeSurvey("CS" + codeGeneration());
		newSurvey.setCategory(surveyDto.getCategory());
		surveyService.save(newSurvey);

		return new ResponseEntity<Message>(new Message("Survey Created."), HttpStatus.OK);

	}

	@PreAuthorize("hasRole('USER')")
	@PostMapping("/create/surveyform/{id}")
	public void createSurveyForm(@RequestBody List<Question> questions, @PathVariable Integer id) {
		List<Question> questionList = new ArrayList<>();
		List<Option> optionList = new ArrayList<>();

		Survey survey = surveyService.findById(id);

		if (survey != null) {
			for (Question question : questions) {
				Question newQuestion = new Question();
				newQuestion.setDescription(question.getDescription());
				newQuestion.setSurvey(survey);
				newQuestion.setQuestionCode(survey.getCodeSurvey() + "QC" + codeGeneration());
				questionList.add(newQuestion);

				for (Option option : question.getOptions()) {
					Option newOption = new Option();
					newOption.setDescription(option.getDescription());
					newOption.setCodeOption(newQuestion.getQuestionCode() + "OC" + codeGeneration());
					newOption.setQuestion(newQuestion);
					optionList.add(newOption);
				}
			}
		}

		questionRepository.saveAll(questionList);
		optionRepository.saveAll(optionList);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/edit/{id}")
	public void editSurvey(@PathVariable Integer id, @RequestBody Survey jsonSurvey) {
		// obtener las preguntas-opciones desde la peticion, obtener las
		// preguntas-opciones desde la bd
		Survey getSurvey = surveyService.findById(id);
		List<Question> questionsJn = jsonSurvey.getQuestions();
		List<Question> questionsBd = getSurvey.getQuestions();

		getSurvey.setTitle(jsonSurvey.getTitle());
		getSurvey.setDescription(jsonSurvey.getDescription());
		// comparar si cada pregunta desde el json es la misma que la bd, si es asi se
		// actualiza si no se crea

		for (Question questionJn : questionsJn) {
			boolean qFound = false;
			for (Question questionBd : questionsBd) {
				if (questionJn.getQuestionCode() != null
						&& questionJn.getQuestionCode().equals(questionBd.getQuestionCode())) {
					questionBd.setDescription(questionJn.getDescription());
					// questionBd.setQuestionCode(questionJn.getQuestionCode());
					// questionBd.setSurvey(surveyService.findById(id));
					questionRepository.save(questionBd);

					for (Option optionJn : questionJn.getOptions()) {
						boolean oFound = false;
						for (Option optionBd : questionBd.getOptions()) {
							if (optionJn.getCodeOption() != null
									&& optionJn.getCodeOption().equals(optionBd.getCodeOption())) {
								optionBd.setDescription(optionJn.getDescription());
								// optionBd.setCodeOption(optionJn.getCodeOption());
								// optionBd.setQuestion(questionBd);
								optionRepository.save(optionBd);

								oFound = true;
								break;
							}
						}
						if (!oFound) {
							Option newOption = new Option();
							newOption.setDescription(optionJn.getDescription());
							newOption.setCodeOption(questionBd.getQuestionCode() + "OC" + codeGeneration());
							newOption.setQuestion(questionBd);
							optionRepository.save(newOption);
						}
					}

					qFound = true;
					break;
				}
			}
			if (!qFound) {
				Question newQuestion = new Question();
				newQuestion.setDescription(questionJn.getDescription());
				newQuestion.setQuestionCode(getSurvey.getCodeSurvey() + "QC" + codeGeneration());
				newQuestion.setSurvey(surveyService.findById(id));
				questionRepository.save(newQuestion);
				for (Option option : questionJn.getOptions()) {
					Option newOption = new Option();
					newOption.setDescription(option.getDescription());
					newOption.setCodeOption(newQuestion.getQuestionCode() + "OC" + codeGeneration());
					newOption.setQuestion(newQuestion);
					optionRepository.save(newOption);
				}

			}
		}
	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/delete_Questions_Options/{id}")
	public void deleteFormSurvey(@RequestBody List<Question> questionsJson, @PathVariable Integer id) {
		Survey currentSurvey = surveyService.findById(id);
		List<Question> questionsToDelete = new ArrayList<>();

		List<Option> optionsJson = questionsJson.stream().flatMap(question -> question.getOptions().stream())
				.collect(Collectors.toList());
		List<Option> optionsBd = currentSurvey.getQuestions().stream()
				.flatMap(question -> question.getOptions().stream()).collect(Collectors.toList());
		List<Option> optionsToDelete = new ArrayList<>();

		for (Option optionBd : optionsBd) {
			boolean foundMatch = false;
			for (Option optionJson : optionsJson) {
				if (optionJson.getCodeOption() != null && optionJson.getCodeOption().equals(optionBd.getCodeOption())) {
					foundMatch = true;
					break;
				}
			}
			if (!foundMatch) {
				optionsToDelete.add(optionBd);
			}
		}

		for (Question questionBd : currentSurvey.getQuestions()) {
			boolean foundQuestion = false;
			for (Question questionJson : questionsJson) {
				if (questionJson.getQuestionCode() != null
						&& questionJson.getQuestionCode().equals(questionBd.getQuestionCode())) {
					foundQuestion = true;
					break;
				}

			}
			if (!foundQuestion) {
				questionsToDelete.add(questionBd);
			}
			if (questionBd.getOptions().isEmpty()) {
				questionsToDelete.add(questionBd);
			}
		}
		optionRepository.deleteAllInBatch(optionsToDelete);
		questionRepository.deleteAllInBatch(questionsToDelete);

	}

	@PreAuthorize("hasRole('USER')")
	@DeleteMapping("/delete/{id}")
	public void deleteById(@PathVariable Integer id) {
		surveyResponse.deleteAll(surveyResponse.findAllSurveyResponses(surveyService.findById(id).getCodeSurvey()));
		surveyService.delete(id);
	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/categories")
	public List<Category> findAllCategories() {
		return categoryRepository.findAll();
	}

	public String codeGeneration() {
		final String ALLOWED_CHARACTERS = "0123456789abcdefghijklmnopqrstuvwxyz";
		final int STRING_LENGTH = 8;

		SecureRandom random = new SecureRandom();
		StringBuilder sb = new StringBuilder(STRING_LENGTH);

		for (int i = 0; i < STRING_LENGTH; i++) {
			int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
			char randomChar = ALLOWED_CHARACTERS.charAt(randomIndex);
			sb.append(randomChar);
		}

		return sb.toString();

	}

	@PreAuthorize("hasRole('USER')")
	@GetMapping("/tabulate/{codeSurvey}")
	public List<Map<String, Object>> tabulateSurvey(@PathVariable String codeSurvey) {
		List<SurveyResponse> responses = surveyResponse.findAllSurveyResponses(codeSurvey);
		List<Question> questions = questionRepository.findAll().stream().filter(
				data->data.getQuestionCode().substring(0,10).equals(codeSurvey)).toList();
		List<Map<String, Object>> tabulateData = new ArrayList<>();

		for(Question question : questions) {
			List<Map<String, Object>> optionsList = new ArrayList<>();
			System.out.println(question.getDescription());
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("question", question.getDescription());
			responseData.put("codeQuestion", question.getQuestionCode());
			tabulateData.add(responseData);
			for(Option option : question.getOptions()) {
				//System.out.println(option.getDescription());
				Map<String, Object> optionData = new HashMap<>();
				optionData.put("description", option.getDescription());
				int count = 0;
				for(SurveyResponse response : responses) {
					for(String code : response.getCodeOption()) {
						if(code.equals(option.getCodeOption())) {
							count++;
						}
					}
				}
	            optionData.put("count", count);
				optionsList.add(optionData);
				responseData.put("options", optionsList);

			}
		}
		return tabulateData;
	}
	
	@PostMapping("/suggestion")
	public void saveSuggestion(@RequestBody Suggestion suggestion) {
		suggestionRepository.save(suggestion);
	}
}
