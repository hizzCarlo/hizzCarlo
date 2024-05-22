package co.surveygenerator.entities;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "surveys")
public class Survey implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(length = 500)
	private String title;

	@Column(length = 1000)
	private String description;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	@JsonIgnore
	private UserData userData;

	@OneToOne
	private Category category;

	@OneToMany(mappedBy = "survey", cascade = CascadeType.ALL)
	@OrderBy("id ASC")
	private List<Question> questions;
	
	@Column(name = "code_survey")
	private String codeSurvey;

	@Column(name = "create_at")
	private LocalDate createAt;

	public Survey() {
	}
	
	public Survey(Integer id) {
		this.id = id;
	}

	public Survey(String title, String description, Category category, List<Question> questions, String codeSurvey) {
		this.title = title;
		this.description = description;
		this.category = category;
		this.questions = questions;
		this.codeSurvey = codeSurvey;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}
	
	public String getCodeSurvey() {
		return codeSurvey;
	}

	public void setCodeSurvey(String codeSurvey) {
		this.codeSurvey = codeSurvey;
	}

	public LocalDate getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createAt = createAt;
	}

	public UserData getUserData() {
		return userData;
	}

	public void setUserData(UserData userData) {
		this.userData = userData;
	}
}
