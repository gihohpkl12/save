package com.start.security.authority.role;

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
}
