package com.StudentManagementSystem.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.StudentManagementSystem.Models.Student;

public interface StudentRepo extends JpaRepository<Student, Long> {

	Optional<Student> findByEmail(String email);

	// Check if a student with a given email already exists
	boolean existsByEmail(String email);

	// Check if a student with a given mobile number already exists
	boolean existsByMobileNo(String mobileNo);

}
