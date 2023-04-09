package com.movie.security.role.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.movie.security.role.domain.Role;
import jakarta.transaction.Transactional;

public interface RoleRepository extends JpaRepository<Role, Long> {

	@Transactional
	List<Role> findAllByOrderByLevelAsc();
	
	@Transactional
	Optional<Role> findByRoleName(String roleName);
}
