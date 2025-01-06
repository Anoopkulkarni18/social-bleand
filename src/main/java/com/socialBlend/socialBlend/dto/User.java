package com.socialBlend.socialBlend.dto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Entity
public class User {
	@Id
	@GeneratedValue
	private int id;
	@Size(min=3,max=10,message="Characters length must be between 3 to 10")
	private String firstname;
	@Size(min=3,max=15,message="Characters length must be between 3 to 15")
	private String lastname;
	@Size(min=3,max=10,message="Characters length must be between 3 to 10")
	private String username;
	@Email
	@NotEmpty
	private String email;
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",message="It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	private String password;
	@Transient
	@Pattern(regexp = "^.*(?=.{8,})(?=..*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",message="It should contain atleast 8 charecter, one uppercase, one lowercase, one number and one speacial charecter")
	private String confirmpassword;
	@DecimalMin(value="6000000000")
	@DecimalMax(value="9999999999")
	private long mobilenumber;
	@NotNull
	private String gender;
	private int otp;
	private boolean verified;
}
