package co.surveygenerator.services;

import java.time.LocalDate;
import java.util.List;

import co.surveygenerator.entities.UserData;

public interface IUserDataService {

	public List<UserData> findAll();
	
	public UserData findById(Integer id);
		
	public UserData save(UserData userData);
	
	public Integer getCurrentUserId();
	
	public LocalDate getCurrentUserCreateAt();
	
}
