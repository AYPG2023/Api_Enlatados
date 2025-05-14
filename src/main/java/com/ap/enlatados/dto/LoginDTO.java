package com.ap.enlatados.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class LoginDTO {

	 @NotBlank @Email
	    private String email;
	    @NotBlank
	    private String password;

	    public LoginDTO() {}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
	    
	   
}
