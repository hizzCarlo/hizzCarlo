package co.surveygenerator.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.surveygenerator.entities.UserData;
import co.surveygenerator.repositories.IUserDataRepository;
import co.surveygenerator.security.entities.User;
import co.surveygenerator.security.repositories.IUserRepository;

@Service
public class ImplUserDataService implements IUserDataService {

	@Autowired
	private IUserDataRepository userDataRepository;

	@Autowired
	private IUserRepository userRepository;

	@Override
	public List<UserData> findAll() {
		return userDataRepository.findAll();
	}

	@Override
	@Transactional
	public UserData save(UserData userData) {
		return userDataRepository.save(userData);
	}

	@Override
	@Transactional(readOnly = true)
	public UserData findById(Integer id) {
		return userDataRepository.findById(id).orElse(null);
	}

	@Override
	public Integer getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = authentication.getName();

		User currentUser = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return currentUser.getId();
	}

	@Override
	public LocalDate getCurrentUserCreateAt() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String currentUsername = authentication.getName();

		User currentUser = userRepository.findByUsername(currentUsername)
				.orElseThrow(() -> new RuntimeException("User not found"));
		return currentUser.getCreateAt();
	}
}
