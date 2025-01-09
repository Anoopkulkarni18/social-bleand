package com.socialBlend.socialBlend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialBlend.socialBlend.dto.Post;
import com.socialBlend.socialBlend.dto.User;

import java.util.*;
public interface PostRepository extends JpaRepository<Post, Integer> {

	List<Post> findByUser(User user);

}
