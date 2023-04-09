package com.start.security.authority.url;

import java.util.List;
import java.util.Optional;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UrlRepository extends JpaRepository<Url, Long>{

	List<Url> findAllByRole_idIsNotNullOrderByOrderNumAsc();
	
	List<Url> findAllByRole_idIsNull();
	
	List<Url> findAllByRoleId(Long roleId);
	
	@Transactional
	Optional<Url> findById(Long id);
	
	Optional<Url> findByUrl(String url);
}
