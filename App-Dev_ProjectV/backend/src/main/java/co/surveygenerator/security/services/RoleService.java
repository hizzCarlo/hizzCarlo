package co.surveygenerator.security.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.surveygenerator.security.entities.Role;
import co.surveygenerator.security.enums.RoleListEnum;
import co.surveygenerator.security.repositories.IRoleRepository;


@Service
@Transactional
public class RoleService {

	@Autowired
	private IRoleRepository roleRepository;

	public Optional<Role> getByRoleName(RoleListEnum roleName) {
		return roleRepository.findByRoleName(roleName);
	}
}