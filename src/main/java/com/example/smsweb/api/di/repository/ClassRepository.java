package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Classses;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassRepository extends GenericRepository<Classses, Integer> {
    @Query("SELECT c FROM Classses c WHERE c.classCode = :classCode")
    Optional<Classses> findClasssesByClassCode(String classCode);

    @Query("SELECT c.classCode FROM Classses c WHERE c.classCode LIKE :classCode")
    List<String> searchClasssesByClassCode(String classCode);
}
