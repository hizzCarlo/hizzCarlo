package co.surveygenerator.dto;

import java.time.LocalDate;

import javax.validation.constraints.Email;

public class UserDataDto {

	private String name;

	private String surname;

	private String gender;

	private LocalDate createAt;

	private String photo;

	@Email
	private String email;

	public UserDataDto() {
	}

	public UserDataDto(String name, String surname, String gender, LocalDate createAt, @Email String email, String photo) {
		this.name = name;
		this.surname = surname;
		this.gender = gender;
		this.createAt = createAt;
		this.email = email;
		this.photo = photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public LocalDate getCreateAt() {
		return createAt;
	}

	public void setCreateAt(LocalDate createAt) {
		this.createAt = createAt;
	}
}
