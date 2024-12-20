package com.StudentManagementSystem.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

import com.StudentManagementSystem.Models.Student;
import com.StudentManagementSystem.Repositories.StudentRepo;

@RestController
@RequestMapping("/api/v1")
public class StudentController {

    @Autowired
    private StudentRepo studentRepo;

    @PostMapping("/addStudent")
    public ResponseEntity<String> addStudent(@Validated @RequestBody Student student, BindingResult bindingResult) {

        // If there are validation errors, return the first error message
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError().getDefaultMessage();
            return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
        }

        // Check if the email already exists
        if (studentRepo.existsByEmail(student.getEmail())) {
            return new ResponseEntity<>("Email is already taken.", HttpStatus.BAD_REQUEST);
        }

        // Check if the mobile number already exists
        if (studentRepo.existsByMobileNo(student.getMobileNo())) {
            return new ResponseEntity<>("Mobile number is already taken.", HttpStatus.BAD_REQUEST);
        }

        // Save the student if no duplicates
        studentRepo.save(student);

        return new ResponseEntity<>("Student account created successfully", HttpStatus.CREATED);
    }
}
