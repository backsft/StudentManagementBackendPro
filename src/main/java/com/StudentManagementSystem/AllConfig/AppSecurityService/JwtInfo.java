package com.StudentManagementSystem.AllConfig.AppSecurityService;

public class JwtInfo {
	private String username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public JwtInfo(String username) {
		super();
		this.username = username;
	}

	public JwtInfo() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "JwtInfo [username=" + username + "]";
	}

}