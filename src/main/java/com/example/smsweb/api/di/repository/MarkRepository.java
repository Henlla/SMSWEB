package com.example.smsweb.api.di.repository;

import com.example.smsweb.api.generic.GenericRepository;
import com.example.smsweb.models.Mark;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MarkRepository extends GenericRepository<Mark,Integer> {
    Optional<Mark> findFirstByStudentSubjectId(int id);
}
