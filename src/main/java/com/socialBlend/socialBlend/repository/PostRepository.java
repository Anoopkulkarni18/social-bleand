package com.socialBlend.socialBlend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.socialBlend.socialBlend.dto.Post;

public interface PostRepository extends JpaRepository<Post, Integer> {

}
