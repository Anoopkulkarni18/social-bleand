package com.socialBlend.socialBlend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialBlend.socialBlend.dto.User;

public interface UserRepository extends JpaRepository<User, Integer>{

	boolean existsByEmail(String email);

	boolean existsByMobilenumber(long mobilenumber);

	boolean existsByUsername(String username);

	User findByUsername(String username);

}
