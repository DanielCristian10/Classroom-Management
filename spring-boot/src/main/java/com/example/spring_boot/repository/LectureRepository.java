package com.example.spring_boot.repository;

import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.Professor;
import com.example.spring_boot.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LectureRepository extends JpaRepository<Lecture, Long> {
    Page<Lecture> findAllByName(String name, Pageable pageable);

    Page<Lecture> findAllByYear(int year, Pageable pageable);

    Page<Lecture> findAllByNameAndYear(String name, int year, Pageable pageable);

    Page<Lecture> findAllByCredits(int credits, Pageable pageable);

}