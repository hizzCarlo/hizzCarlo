package co.surveygenerator.security.controllers;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.surveygenerator.dto.Message;
import co.surveygenerator.entities.UserData;
import co.surveygenerator.security.dto.JwtDto;
import co.surveygenerator.security.dto.LoginUser;
import co.surveygenerator.security.dto.NewUser;
import co.surveygenerator.security.entities.Role;
import co.surveygenerator.security.entities.User;
import co.surveygenerator.security.enums.RoleListEnum;
import co.surveygenerator.security.jwt.JwtProvider;
import co.surveygenerator.security.services.RoleService;
import co.surveygenerator.security.services.UserService;
import co.surveygenerator.services.IUserDataService;

@RestController
@RequestMapping("/surveygenerator/auth")
@CrossOrigin(origins = "*")
public class AuthController {
	
	private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final RoleService roleService;
    private final JwtProvider jwtProvider;
    private final IUserDataService userDataService;
    
    public AuthController(AuthenticationManagerBuilder authenticationManagerBuilder, PasswordEncoder passwordEncoder,
            UserService userService, RoleService roleService, JwtProvider jwtProvider, IUserDataService userDataService) {
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.roleService = roleService;
        this.jwtProvider = jwtProvider;
        this.userDataService = userDataService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Object> login(@Valid @RequestBody LoginUser loginUser, BindingResult bidBindingResult){
        if(bidBindingResult.hasErrors())
            return new ResponseEntity<>(new Message("You must enter your username and password"), HttpStatus.BAD_REQUEST);
        try {
                UsernamePasswordAuthenticationToken authenticationToken= new UsernamePasswordAuthenticationToken(loginUser.getUsername(), loginUser.getPassword());
                Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtProvider.generateToken(authentication);
                JwtDto jwtDto = new JwtDto(jwt);
                return new ResponseEntity<>(jwtDto, HttpStatus.OK);
        } catch (Exception e) {
                return new ResponseEntity<>(new Message("Incorrect username or password."), HttpStatus.BAD_REQUEST);
        }
    }
    
    //@PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody NewUser newUser, BindingResult bindingResult) {
    	Optional<User> usernameAlreadyExists = userService.getByUsername(newUser.getUsername());
    	if(usernameAlreadyExists.isPresent())
            return new ResponseEntity<>(new Message("The username is already registered in the database, please enter another one."), HttpStatus.BAD_REQUEST);       
	
        if (bindingResult.hasErrors())
            return new ResponseEntity<>(new Message("Please review the fields and try again."), HttpStatus.BAD_REQUEST);       
        System.out.println("new_user=" + newUser.getUsername());
        User user = new User(newUser.getUsername(), passwordEncoder.encode(newUser.getPassword()));
        UserData userData = new UserData();        
        Set<Role> roles = new HashSet<>();
        
        roles.add(roleService.getByRoleName(RoleListEnum.ROLE_USER).get());
        if (newUser.getRoles().contains("admin"))
            roles.add(roleService.getByRoleName(RoleListEnum.ROLE_ADMIN).get());
        user.setRoles(roles);
        userService.save(user);
        userData.setUserId(user.getId());
        userDataService.save(userData);
        
        return new ResponseEntity<>(new Message("Successful registration, you can now log into the system."), HttpStatus.CREATED);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        SecurityContextHolder.clearContext();
        return new ResponseEntity<>(new Message("Logged out successfully"), HttpStatus.OK);
    }

}
