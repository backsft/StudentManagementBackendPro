package com.StudentManagementSystem.AllConfig.AppSecurityService;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.StudentManagementSystem.Models.Student;
import com.StudentManagementSystem.Repositories.StudentRepo;

@Component
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	private StudentRepo repository;

	@Override
	public UserDetails loadUserByUsername(String username) {
		Optional<Student> userInfo = this.repository.findByEmail(username);

		if (userInfo.isPresent()) {
			Student student = userInfo.get();
			if (!student.isActive()) {

				throw new DisabledException("User Disbled/Locked");
			}
			return new UserInfoUserDetails(student);
		} else {

			throw new UsernameNotFoundException("" + username + "Not Registered");
		}
	}

}