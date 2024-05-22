package co.surveygenerator.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import co.surveygenerator.entities.Category;

public interface ICategoryRepository extends JpaRepository<Category, Integer>{

	
}
