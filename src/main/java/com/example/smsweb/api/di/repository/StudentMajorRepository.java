package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.MajorStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentMajorRepository extends JpaRepository<MajorStudent,Integer> {
}
