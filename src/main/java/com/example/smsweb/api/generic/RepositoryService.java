package com.example.smsweb.api.generic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface RepositoryService<T,Y> extends JpaRepository<T,Y> {
}
