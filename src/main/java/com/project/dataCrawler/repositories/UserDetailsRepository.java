package com.project.dataCrawler.repositories;

import com.project.dataCrawler.domain.entities.UserDetailsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDetailsRepository extends JpaRepository<UserDetailsEntity, Long> {
    
    boolean existsByRegNo(@Param("regNo") String regNo);
    
}
