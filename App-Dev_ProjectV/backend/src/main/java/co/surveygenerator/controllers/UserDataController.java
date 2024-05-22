package co.surveygenerator.controllers;

import java.io.IOException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import co.surveygenerator.dto.Message;
import co.surveygenerator.dto.UserDataDto;
import co.surveygenerator.entities.UserData;
import co.surveygenerator.services.IUploadFileService;
import co.surveygenerator.services.IUserDataService;

@RestController
@RequestMapping("/surveygenerator/userdata")

@CrossOrigin(origins = "*")
public class UserDataController {

	@Autowired
	private IUserDataService userDataService;
	
	@Autowired
	private IUploadFileService uploadService;

	@GetMapping("/list")
	public List<UserData> findAll() {
		return userDataService.findAll();
	}

	@GetMapping("/profile")
	public UserDataDto findUserDataById() {
		UserData userProfile = userDataService.findById(userDataService.getCurrentUserId());

		UserDataDto profileDto = new UserDataDto(userProfile.getName(), userProfile.getSurname(),
				userProfile.getGender(), userDataService.getCurrentUserCreateAt(), userProfile.getEmail(), userProfile.getPhoto());

		return profileDto;
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/edit")
	public ResponseEntity<Message> edit(@Valid @RequestBody UserDataDto userForm, BindingResult result) {
		if (result.hasErrors())
			return new ResponseEntity<Message>(new Message("The fields are empty or a valid email was not provided."),
					HttpStatus.BAD_REQUEST);

		UserData userProfile = userDataService.findById(userDataService.getCurrentUserId());
		userProfile.setName(userForm.getName());
		userProfile.setSurname(userForm.getSurname());
		userProfile.setEmail(userForm.getEmail());
		userProfile.setGender(userForm.getGender());
		userDataService.save(userProfile);

		return new ResponseEntity<Message>(new Message("the user information has been saved."), HttpStatus.OK);
	}

	@PreAuthorize("hasRole('USER')")
	@PutMapping("/photo")
	public void uriPhoto(@RequestParam("photo") MultipartFile photo) {
		UserData userProfile = userDataService.findById(userDataService.getCurrentUserId());

		if (!photo.isEmpty()) {

			String nameFile = null;

			try {
				nameFile = uploadService.copy(photo);
				System.out.println("photo_name = " + nameFile);
			} catch (IOException e) {
				//return new ResponseEntity<Message>(new Message("Error al subir la foto del cliente en la Base de datos."), HttpStatus.INTERNAL_SERVER_ERROR);

			}

			// comprobar si ya hay una foto asosiada al cliente para eliminar y reemplazarla
			// por una nueva
			String userPhoto = userProfile.getPhoto();

			uploadService.delete(userPhoto);

			userProfile.setPhoto(nameFile);
			userDataService.save(userProfile);

			//return new ResponseEntity<Message>(new Message("the user photo was upload sussesfully."), HttpStatus.OK);

		}

		//return new ResponseEntity<Message>(new Message(""), HttpStatus.OK);
	}

}
