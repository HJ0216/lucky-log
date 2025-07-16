package com.fortunehub.luckylog.repository;

import com.fortunehub.luckylog.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
