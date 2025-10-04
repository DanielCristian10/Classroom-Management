package com.example.spring_boot.controller;

import com.example.spring_boot.authorization.JwtUtil;
import com.example.spring_boot.model.*;
import com.example.spring_boot.repository.LectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.expression.ExpressionException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.spring_boot.repository.LectureExtendedRepository;
import org.springframework.web.server.ResponseStatusException;

import java.io.Console;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.spring_boot.authorization.JwtUtil.extractJwtFromRequest;
import static com.example.spring_boot.authorization.JwtUtil.extractRole;

@RestController
@RequestMapping("/api/academia/lectures")
public class LectureController {

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private PagedResourcesAssembler<Lecture> pagedAssembler;

    @Autowired
    private LectureExtendedRepository lectureExtendedRepository;

    @GetMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.OK)
    public PagedModel<EntityModel<Lecture>> getAllLectures(@RequestParam(required = false) String name, @RequestParam(required = false) Integer year, @RequestParam(required = false) Integer credits, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<Lecture> lectures;

            if (name != null && year != null) {
                lectures = lectureRepository.findAllByNameAndYear(name, year, pageable);
            } else if (name != null) {
                lectures = lectureRepository.findAllByName(name, pageable);
            } else if (year != null) {
                lectures = lectureRepository.findAllByYear(year, pageable);
            } else if (credits != null) {
                lectures = lectureRepository.findAllByCredits(credits, pageable);
            } else {
                lectures = lectureRepository.findAll(pageable);
            }

            return pagedAssembler.toModel(lectures, lecture -> EntityModel.of(lecture, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById(lecture.getId())).withSelfRel()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request parameters");
        }
    }

    @GetMapping(params = "id")
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Lecture> getLectureById(@RequestParam long id) {
        Lecture lecture = lectureRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found with id: " + id));

        EntityModel<Lecture> model = EntityModel.of(lecture);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById(id)).withSelfRel();
        Link allLecturesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getAllLectures(null, null, null, 0, 10)).withRel("all-lectures");
        model.add(selfLink, allLecturesLink);
        return model;
    }

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAnyRole('PROFESSOR', 'STUDENT')")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<LectureDetail> getLectureDetails(@PathVariable("id") Long lectureId) {

        String token = extractJwtFromRequest();
        String extractedEmail = JwtUtil.extractEmail(token);
        String extractedRole = JwtUtil.extractRole(token);

        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ExpressionException("Lecture not found with id: " + lectureId));
        boolean isAuthorized = false;
        if (extractedRole.equals("professor")) {
            isAuthorized = lecture.getProfessors().stream().anyMatch(professor -> professor.getEmail() != null && professor.getEmail().equals(extractedEmail));
        } else if (extractedRole.equals("student")) {
            isAuthorized = lecture.getStudents().stream().anyMatch(student -> student.getEmail() != null && student.getEmail().equals(extractedEmail));
        }
        if (!isAuthorized) {
            throw new AccessDeniedException("Not allowed to access this lecture");
        }

        LectureExtended extended = lectureExtendedRepository.findByLectureId(lectureId).orElse(null);

        LectureDetail lectureDetail = new LectureDetail(lecture, extended);

        EntityModel<LectureDetail> model = EntityModel.of(lectureDetail);

        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureDetails(lectureId)).withSelfRel();

        Link allLecturesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getAllLectures(null, null, null, 0, 10)).withRel("all-lectures");
        Link expandedLink = allLecturesLink.expand();
        model.add(selfLink, expandedLink.withRel("all-lectures"));

        return model;
    }

    @GetMapping("/{id}/students")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('PROFESSOR')")
    public CollectionModel<EntityModel<Student>> getLectureEnrolledStudents(@PathVariable("id") Long lectureId) {

        String token = extractJwtFromRequest();
        String extractedEmail = JwtUtil.extractEmail(token);
        String extractedRole = JwtUtil.extractRole(token);

        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found"));
        boolean isAuthorized = false;
        if (extractedRole.equals("professor")) {
            isAuthorized = lecture.getProfessors().stream().anyMatch(professor -> professor.getEmail() != null && professor.getEmail().equals(extractedEmail));
        }
        if (!isAuthorized) {
            throw new AccessDeniedException("Not allowed to access this lecture");
        }

        LectureExtended extended = lectureExtendedRepository.findByLectureId(lectureId).orElse(null);


        Set<Student> students = lecture.getStudents();

        List<EntityModel<Student>> lectureModels = students.stream().map(student -> EntityModel.of(student, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getStudentById((long) student.getId())).withSelfRel())).collect(Collectors.toList());


        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureEnrolledStudents(lectureId)).withSelfRel();

        Link allStudentsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getAllLectures(null, null, null, 0, 10)).withRel("all-lectures");
        Link expandedLink = allStudentsLink.expand();
        return CollectionModel.of(lectureModels, selfLink, expandedLink);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Lecture> createLecture(@RequestBody Lecture lecture) {
        try {

            Lecture savedLecture = lectureRepository.save(lecture);

            EntityModel<Lecture> model = EntityModel.of(savedLecture);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getLectureById(savedLecture.getId())).withSelfRel();
            Link allLecturesLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LectureController.class).getAllLectures(null, null, null, 0, 10)).withRel("all-lectures");
            model.add(selfLink, allLecturesLink);
            return model;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Lecture creation failed due to invalid input");
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAll() {
        try {
            lectureRepository.deleteAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to delete all lectures");
        }
    }
}
