package com.fighttracker.fighttracker_backend.repository;

import com.fighttracker.fighttracker_backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
