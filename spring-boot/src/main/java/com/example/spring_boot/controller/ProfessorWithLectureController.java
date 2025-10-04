package com.example.spring_boot.controller;

import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.Professor;
import com.example.spring_boot.repository.LectureRepository;
import com.example.spring_boot.repository.ProfessorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/academia/professors/{id}/lectures")
public class ProfessorWithLectureController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @GetMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    public CollectionModel<EntityModel<Lecture>> getLecturesByProfessor(@PathVariable long id) {
        try {

            Professor professor = professorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found with id: " + id));

            Set<Lecture> lectures = professor.getLectures();

            List<EntityModel<Lecture>> lectureModels = lectures.stream().map(lecture -> EntityModel.of(lecture, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById((long) lecture.getId())).withSelfRel())).collect(Collectors.toList());

            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).getLecturesByProfessor(id)).withSelfRel();
            Link parentLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getProfessorById(id)).withRel("parent");


            return CollectionModel.of(lectureModels, selfLink, parentLink);
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error retrieving lectures", e);
        }
    }

    @PostMapping("/{lectureId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Professor> assignExistingLectureToProfessor(@PathVariable long id, @PathVariable long lectureId) {
        Professor professor = professorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found with id: " + id));

        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found with id: " + lectureId));

        professor.addLecture(lecture);
        professorRepository.save(professor);

        EntityModel<Professor> model = EntityModel.of(professor);

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorWithLectureController.class).assignExistingLectureToProfessor(id, lectureId)).withSelfRel();
        Link parentLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorWithLectureController.class).getLecturesByProfessor(id)).withRel("parent");
        Link lectureLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById(lectureId)).withRel("lecture-details");

        model.add(selfLink, parentLink, lectureLink);
        return model;
    }
}
