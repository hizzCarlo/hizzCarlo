package co.surveygenerator.services;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImplUploadFileService implements IUploadFileService {
	
	private final Logger log = LoggerFactory.getLogger(ImplUploadFileService.class);
	
	private final static String UPLOAD_PATH = "uploads/photos/";
	//https:\\drive.google.com\\drive\\folders\\1ptEOTOQY5IWaFxGQIDAEe16-MK4B0JlH?usp=share_link
	
	@Override
	public Path getPath(String namePhoto) {
		return Paths.get(UPLOAD_PATH).resolve(namePhoto).toAbsolutePath();
	}

	@Override
	public Resource load(String namePhoto) throws MalformedURLException {

		Path pathFile = getPath(namePhoto);
		log.info(pathFile.toString());
		
		Resource resource = new UrlResource(pathFile.toUri());
	
		if(!resource.exists() && !resource.isReadable()) {
			pathFile = Paths.get("src/main/resources/static/img").resolve("nullphoto.png").toAbsolutePath();
		
			resource = new UrlResource(pathFile.toUri());
		
			log.error("no se pudo cargar la imagen: " + namePhoto);			
		}
		
		return resource;
	}

	@Override
	public String copy(MultipartFile file) throws IOException {
		String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replace(" ", "");
		Path filePath = getPath(fileName);
		log.info(filePath.toString());
		
		Files.copy(file.getInputStream(), filePath);
		
		return fileName;
	}

	@Override
	public boolean delete(String namePhoto) {
		if(namePhoto != null && namePhoto.length() > 0) {
			Path filePathPhoto = Paths.get(UPLOAD_PATH).resolve(namePhoto).toAbsolutePath();
			File actualPhoto = filePathPhoto.toFile();
			
			if(actualPhoto.exists() && actualPhoto.canRead()) {
				actualPhoto.delete();
				return true;
			}
		}
		return false;
	}

	

}
