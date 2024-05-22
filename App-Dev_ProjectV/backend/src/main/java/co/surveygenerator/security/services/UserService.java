package co.surveygenerator.security.services;

import java.util.Optional;

import javax.transaction.Transactional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.surveygenerator.security.entities.User;
import co.surveygenerator.security.repositories.IUserRepository;

@Service
@Transactional
public class UserService {
	
	@Autowired
    private IUserRepository userRepository;

    public Optional<User> getByUsername(String username){
        return userRepository.findByUsername(username);
    }
    
    public boolean existByUsername(String username){
        return userRepository.existsByUsername(username);
    }
    
    public void save(User user){
        userRepository.save(user);
    }
    
}
