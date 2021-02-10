package com.sq.sec.web.repository;

import com.sq.sec.web.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByEmail(String username);

}
