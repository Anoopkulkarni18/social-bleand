package com.socialBlend.socialBlend.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;

import com.socialBlend.socialBlend.dto.User;
import com.socialBlend.socialBlend.helper.AES;
import com.socialBlend.socialBlend.helper.EmailSender;
import com.socialBlend.socialBlend.repository.UserRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Service
public class UserService {

	@Autowired
	UserRepository repository;

	@Autowired
	EmailSender emailSender;

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
			session.setAttribute("", session);
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

	public String login(String username, String password,HttpSession session) {
		User user=repository.findByUsername(username);
		if(user==null) {
			session.setAttribute("fail", "User not found");
			return "redirect:/login";
		}
		else {
			if(AES.decrypt(user.getPassword()).equals(password)) {
				if(user.isVerified()) {
					return "redirect:/home";
				}else {
					int otp = new Random().nextInt(100000, 1000000);
					emailSender.sendOtp(user.getEmail(), user.getFirstname(), otp);
					System.err.println(otp);
					user.setOtp(otp);
					repository.save(user);
					session.setAttribute("", session);
					return "redirect:/otp/" + user.getId();
				}
			}else {
				session.setAttribute("fail", "Incorrect password");
				return "redirect:/login";
			}
		}
	}

	public String loadHome(HttpSession session) {
		User user = (User) session.getAttribute("user");
		if(user==null) {
			session.setAttribute("fail", "Invalid sesssion");
			return "redirect:/login";
		}else {
			return "redirect:/home";
		}
	}

	public String logout(HttpSession session) {
		session.removeAttribute("user");
		session.setAttribute("pass", "Logout success");
		return "redirect:/login";
	}
	

}