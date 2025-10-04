package com.example.spring_boot.controller;

import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.Student;
import com.example.spring_boot.repository.LectureRepository;
import com.example.spring_boot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/academia/students/{id}/lectures")
public class StudentWithLectureController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping
    public CollectionModel<EntityModel<Lecture>> getLecturesByStudent(@PathVariable long id) {
        try {

            Student student = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));

            Set<Lecture> lectures = student.getLectures();

            List<EntityModel<Lecture>> lectureModels = lectures.stream().map(lecture -> EntityModel.of(lecture, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById(lecture.getId())).withSelfRel())).toList();

            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getLecturesByStudent(id)).withSelfRel();


            return CollectionModel.of(lectureModels, selfLink);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving lectures for the student", e);
        }
    }


    @PostMapping("/{lectureId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Student> assignExistingLectureToStudent(@PathVariable long id, @PathVariable long lectureId) {
        try {

            Student student = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));

            Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found with id: " + lectureId));

            student.addLecture(lecture);
            studentRepository.save(student);

            EntityModel<Student> model = EntityModel.of(student);

            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentWithLectureController.class).assignExistingLectureToStudent(id, lectureId)).withSelfRel();

            Link parentLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentWithLectureController.class).getLecturesByStudent(id)).withRel("parent");

            Link lectureLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById(lectureId)).withRel("lecture-details");

            model.add(selfLink, parentLink, lectureLink);

            return model;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error assigning lecture to student", e);
        }
    }


    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAllLecturesFromStudent(@PathVariable long id) {
        try {

            Student student = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));

            Set<Lecture> lectures = student.getLectures();
            lectures.forEach(lecture -> lecture.setStudents(null));
            lectureRepository.saveAll(lectures);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error removing lectures from the student", e);
        }
    }
}
