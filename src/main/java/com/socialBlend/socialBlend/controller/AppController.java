package com.socialBlend.socialBlend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.socialBlend.socialBlend.dto.User;

import jakarta.validation.Valid;
import lombok.Data;

@Data
@Controller
public class AppController {

	@GetMapping("/login")
	public String loadLogin() {
		return "login.html";
	}
	@GetMapping("/register")
	public String loadRegister(ModelMap map,User user) {
		map.put("user", user);
		return "register.html";
	}
	@PostMapping("/register")
	public String register(@Valid User user,BindingResult result) {
		if(!user.getPassword().equals(user.getConfirmpassword())){
			result.rejectValue("confirmpassword","error.confirmpassword","password not matching");
		}
		if(result.hasErrors()) {
			return "register.html";
		}else {
//			System.out.println(user);
			return "redirect:https://www.youtube.com";
		}
	}
}
