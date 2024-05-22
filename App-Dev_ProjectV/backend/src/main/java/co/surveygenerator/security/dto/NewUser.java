package co.surveygenerator.security.dto;

import java.util.HashSet;
import java.util.Set;

public class NewUser {
	
	private String username;
		
	private String password;
	
	private Set<String> roles = new HashSet<>();

	public NewUser() {
	}

	public NewUser(String username, String password, Set<String> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Set<String> getRoles() {
		return roles;
	}

	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
}
