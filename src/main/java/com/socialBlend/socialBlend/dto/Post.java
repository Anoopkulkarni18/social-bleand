package com.socialBlend.socialBlend.dto;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Post {

	@Id
	@GeneratedValue
	private int id;
	private String imageUrl;
	private String caption;
	@CreationTimestamp
	private LocalDateTime postedTime;

	@ManyToOne
	User user;
}
