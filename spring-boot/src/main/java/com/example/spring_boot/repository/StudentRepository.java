package com.example.spring_boot.repository;

import com.example.spring_boot.model.Professor;
import com.example.spring_boot.model.Student;
import jakarta.persistence.Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.hateoas.EntityModel;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Page<Student> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    Page<Student> findAllByFirstName(String firstName, Pageable pageable);

    Page<Student> findAllByLastName(String lastName, Pageable pageable);

    Page<Student> findAllByGroupName(String groupName, Pageable pageable);

    Page<Student> findAllByFirstNameAndLastNameAndGroupName(String firstName, String lastName, String groupName, Pageable pageable);

    Page<Student> findAllByFirstNameAndLastNameAndGroupNameAndYear(String firstName, String lastName, String groupName, int year, Pageable pageable);

    Optional<Student> findByEmail(String email);
}
