package com.example.smsweb.api.di.repository;

import com.example.smsweb.models.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile,Integer> {
    Optional<Profile> findProfileByAccountId(Integer id);

    Profile findProfileByIdentityCard(String card);
}
