package co.surveygenerator.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;

@Entity
@Table(name = "survey_response")
public class SurveyResponse implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@Column(name = "respondent_name")
	private String respondentName;
	
	private String codeSurvey;
		
	@ElementCollection
	@CollectionTable(name = "options_responses", joinColumns = @JoinColumn(name = "survey_response_id"))
	@Column(name = "code_option")
	private List<String> codeOption;
	
	private String email;
	
	@Column(name = "create_at")
	private LocalDate createAt;
	
	public SurveyResponse() {}

	public SurveyResponse(String respondentName, String codeSurvey, List<String> codeOption,	String email) {
		this.respondentName = respondentName;
		this.codeSurvey = codeSurvey;
		this.codeOption = codeOption;
		this.email = email;
	}
	
	@PrePersist
	public void actualDate() {
		this.createAt = LocalDate.now();
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getRespondentName() {
		return respondentName;
	}

	public void setRespondentName(String respondentName) {
		this.respondentName = respondentName;
	}
	
	public String getCodeSurvey() {
		return codeSurvey;
	}

	public void setCodeSurvey(String codeSurvey) {
		this.codeSurvey = codeSurvey;
	}

	public List<String> getCodeOption() {
		return codeOption;
	}

	public void setCodeOption(List<String> codeOption) {
		this.codeOption = codeOption;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDate getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createAt = createAt;
	}
	
}
