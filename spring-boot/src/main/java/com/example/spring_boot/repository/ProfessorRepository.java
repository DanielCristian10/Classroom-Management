package com.example.spring_boot.repository;

import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.Professor;
import com.example.spring_boot.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Page<Professor> findAllByFirstNameAndLastName(String firstName, String lastName, Pageable pageable);

    Page<Professor> findAllByFirstName(String firstName, Pageable pageable);

    Page<Professor> findAllByLastName(String lastName, Pageable pageable);

    Optional<Professor> findByEmail(String email);

}

