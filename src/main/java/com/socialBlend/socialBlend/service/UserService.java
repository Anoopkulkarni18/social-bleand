package com.socialBlend.socialBlend.service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import com.socialBlend.socialBlend.dto.Post;
import com.socialBlend.socialBlend.dto.User;
import com.socialBlend.socialBlend.helper.AES;
import com.socialBlend.socialBlend.helper.CloudinaryHelper;
import com.socialBlend.socialBlend.helper.EmailSender;
import com.socialBlend.socialBlend.repository.PostRepository;
import com.socialBlend.socialBlend.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UserService {

	@Autowired
	UserRepository repository;

	@Autowired
	EmailSender emailSender;

	@Autowired
	CloudinaryHelper cloudinaryHelper;
	@Autowired
	PostRepository postRepository;

	public String loadRegister(ModelMap map, User user) {
		map.put("user", user);
		return "register.html";
	}

	public String register(@Valid User user, BindingResult result, HttpSession session) {
		if (!user.getPassword().equals(user.getConfirmpassword())) {
			result.rejectValue("confirmpassword", "error.confirmpassword", "password not matching");
		}

		if (repository.existsByEmail(user.getEmail())) {
			result.rejectValue("email", "error.email", "Email already exists");
		}

		if (repository.existsByMobilenumber(user.getMobilenumber())) {
			result.rejectValue("mobilenumber", "error.mobilenumber", "mobileNumber already exists");
		}

		if (repository.existsByUsername(user.getUsername())) {
			result.rejectValue("username", "error.username", "username already exists");
		}

		if (result.hasErrors()) {
			return "register.html";
		} else {
			user.setPassword(AES.encrypt(user.getPassword()));
			int otp = new Random().nextInt(100000, 1000000);
			emailSender.sendOtp(user.getEmail(), user.getFirstname(), otp);
			System.err.println(otp);
			user.setOtp(otp);
			repository.save(user);
			session.setAttribute("pass", "registered successfully");
			return "redirect:/otp/" + user.getId();
		}
	}

	public String verifyOtp(int otp, int id, HttpSession session) {
		Optional<User> optional = repository.findById(id);
		if (!optional.isEmpty()) {
			User user = optional.get();
			if (user.getOtp() == otp) {
				user.setVerified(true);
				user.setOtp(0);
				repository.save(user);
				session.setAttribute("pass", "Account created successfully");
				return "redirect:/login";
			} else {
				session.setAttribute("fail", "Otp mismatch try again!!!");
				return "redirect:/otp/" + id;

			}
		} else {
			return "redirect:/otp/" + id;
		}
	}

	public String resendOtp(int id, HttpSession session) {
		User user = repository.findById(id).get();
		int otp = new Random().nextInt(100000, 1000000);
		System.err.println(otp);
		user.setOtp(otp);
		emailSender.sendOtp(user.getEmail(), user.getFirstname(), otp);
		repository.save(user);
		session.setAttribute("pass", "Otp Re-Sent Success");
		return "redirect:/otp/" + user.getId();
	}

	public String login(String username, String password, HttpSession session) {
		User user = repository.findByUsername(username);

		if (user == null) {
			session.setAttribute("fail", "User not found");
			return "redirect:/login";
		} else {
			if (AES.decrypt(user.getPassword()).equals(password)) {
				if (user.isVerified()) {
					session.setAttribute("user", user);
					session.setAttribute("pass", "login success");
					return "redirect:/home";
				} else {
					int otp = new Random().nextInt(100000, 1000000);
					emailSender.sendOtp(user.getEmail(), user.getFirstname(), otp);
					System.err.println(otp);
					user.setOtp(otp);
					repository.save(user);
					session.setAttribute("", session);
					return "redirect:/otp/" + user.getId();
				}
			} else {
				session.setAttribute("fail", "Incorrect password");
				return "redirect:/login";
			}
		}
	}

	public String loadHome(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		} else {
			return "home.html";
		}
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout success");
		return "redirect:/login";
	}

	public String loadProfile(HttpSession session, ModelMap map) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		} else {
			List<Post> posts = postRepository.findByUser(user);
			if (!posts.isEmpty()) {
				map.put("posts", posts);
			}
			return "profile.html";
		}
	}

	public String editProfile(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		} else {
			return "edit-profile.html";
		}
	}

	public String updateProfile(MultipartFile image, String bio, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		} else {
			user.setBio(bio);
			user.setImageUrl(cloudinaryHelper.saveImg(image));
			repository.save(user);
			return "redirect:/profile";
		}
	}

	public String loadAddNewPost(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		} else {
			return "add-new-post.html";
		}
	}

	public String addPost(MultipartFile image, Post post, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		} else {
			post.setImageUrl(cloudinaryHelper.updateImg(image));
			System.err.println(post.getImageUrl());
			post.setUser(user);
			postRepository.save(post);
			session.setAttribute("pass", "Added Post Successfully");
			return "redirect:/profile";
		}
	}

	public String deletePost( int pid, HttpSession session,ModelMap map) {
		User user=(User) session.getAttribute("user");
		if(user==null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		}else {
			Post post=postRepository.findById(pid).get();            
			postRepository.delete(post);
			List<Post> posts = postRepository.findByUser(user);
			if (!posts.isEmpty()) {
				map.put("posts", posts);
			}
			return "profile.html"; 
		}
		
	}

}
