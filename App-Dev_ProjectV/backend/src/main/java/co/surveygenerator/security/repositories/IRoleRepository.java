package co.surveygenerator.security.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.surveygenerator.security.entities.Role;
import co.surveygenerator.security.enums.RoleListEnum;

public interface IRoleRepository extends JpaRepository<Role, Integer>{

	 Optional<Role> findByRoleName(RoleListEnum roleName);
}