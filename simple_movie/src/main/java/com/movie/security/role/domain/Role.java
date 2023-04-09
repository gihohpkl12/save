package com.movie.security.role.domain;

import com.movie.security.role.form.RoleForm;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Role {
    
	@Id @GeneratedValue
	@Column(name = "role_id")
    private Long id;

    @Column(unique = true)
    private String roleName;

    @Column(nullable = false)
    private long level;
    
    public Role(RoleForm roleForm) {
    	setRoleName(roleForm.getRoleName());
    	setLevel(roleForm.getLevel());
    }
    
  
}
