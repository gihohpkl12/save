package com.start.security.authority.role;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;

public interface RoleRepository extends JpaRepository<Role, Long> {

	@Transactional
	List<Role> findAllByOrderByLevelAsc();
	
	@Transactional
	Optional<Role> findByRoleName(String roleName);
}
