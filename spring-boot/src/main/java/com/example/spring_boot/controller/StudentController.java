package com.example.spring_boot.controller;

import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.Professor;
import com.example.spring_boot.model.Student;
import com.example.spring_boot.repository.LectureRepository;
import com.example.spring_boot.repository.StudentRepository;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/academia/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private PagedResourcesAssembler<Student> pagedAssembler;

    @GetMapping
    public PagedModel<EntityModel<Student>> getAllStudents(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName, @RequestParam(required = false) String groupName, @RequestParam(required = false) Integer year, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {

            Pageable pageable = PageRequest.of(page, size);
            Page<Student> students;

            if (firstName != null && lastName != null && groupName != null && year != null) {
                students = studentRepository.findAllByFirstNameAndLastNameAndGroupNameAndYear(firstName, lastName, groupName, year, pageable);
            } else if (firstName != null && lastName != null && groupName != null) {
                students = studentRepository.findAllByFirstNameAndLastNameAndGroupName(firstName, lastName, groupName, pageable);
            } else if (firstName != null && lastName != null) {
                students = studentRepository.findAllByFirstNameAndLastName(firstName, lastName, pageable);
            } else if (firstName != null) {
                students = studentRepository.findAllByFirstName(firstName, pageable);
            } else if (lastName != null) {
                students = studentRepository.findAllByLastName(lastName, pageable);
            } else if (groupName != null) {
                students = studentRepository.findAllByGroupName(groupName, pageable);
            } else {
                students = studentRepository.findAll(pageable);
            }

            return pagedAssembler.toModel(students, student -> EntityModel.of(student, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getStudentById(student.getId())).withSelfRel()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error fetching students", e);
        }
    }

    @GetMapping("/by-email")
    public EntityModel<Student> getStudentByEmail(@RequestParam String email) {
        Student student = studentRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with email: " + email));

        EntityModel<Student> model = EntityModel.of(student);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getStudentById(student.getId())).withSelfRel();
        Link allStudentsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getAllStudents(null, null, null, null, 0, 10)).withRel("all-students");
        model.add(selfLink, allStudentsLink);
        return model;
    }

    @GetMapping(params = "id")
    public EntityModel<Student> getStudentById(@RequestParam long id) {
        Student student = studentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id));

        EntityModel<Student> model = EntityModel.of(student);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getStudentById(id)).withSelfRel();
        Link allStudentsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getAllStudents(null, null, null, null, 0, 10)).withRel("all-students");
        model.add(selfLink, allStudentsLink);
        return model;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Student> createStudent(@RequestBody Student student) {
        try {

            student.setEmail(student.getFirstName().toLowerCase() + "." + student.getLastName().toLowerCase() + "@student.com");
            Student savedStudent = studentRepository.save(student);

            EntityModel<Student> model = EntityModel.of(savedStudent);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getStudentById(savedStudent.getId())).withSelfRel();
            Link allStudentsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(StudentController.class).getAllStudents(null, null, null, null, 0, 10)).withRel("all-students");
            model.add(selfLink, allStudentsLink);
            return model;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error creating student", e);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeStudent(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAll() {

        try {
            studentRepository.deleteAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting all students", e);
        }
    }

}
