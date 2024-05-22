package co.surveygenerator.security.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import co.surveygenerator.security.enums.RoleListEnum;

@Entity
@Table(name = "roles")
public class Role implements Serializable{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Enumerated(EnumType.STRING)
	@Column(name = "role_name")
    private RoleListEnum roleName;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
		
	public RoleListEnum getRoleName() {
		return roleName;
	}

	public void setRoleName(RoleListEnum roleName) {
		this.roleName = roleName;
	}

	private static final long serialVersionUID = 1L;

}
