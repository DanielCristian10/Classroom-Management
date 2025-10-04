package com.example.spring_boot.controller;

import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.Professor;
import com.example.spring_boot.repository.ProfessorRepository;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/academia/professors")
public class ProfessorController {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private PagedResourcesAssembler<Professor> pagedAssembler;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping
    public PagedModel<EntityModel<Professor>> getAllProfessors(@RequestParam(required = false) String firstName, @RequestParam(required = false) String lastName, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Professor> professors;

            if (firstName != null && lastName != null) {
                professors = professorRepository.findAllByFirstNameAndLastName(firstName, lastName, pageable);
            } else if (firstName != null) {
                professors = professorRepository.findAllByFirstName(firstName, pageable);
            } else if (lastName != null) {
                professors = professorRepository.findAllByLastName(lastName, pageable);
            } else {
                professors = professorRepository.findAll(pageable);
            }

            return pagedAssembler.toModel(professors, professor -> EntityModel.of(professor, WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getProfessorById(professor.getId())).withSelfRel()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid request parameters", e);
        }
    }

    @GetMapping("/by-email")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Professor> getProfessorByEmail(@RequestParam String email) {
        Professor professor = professorRepository.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found with email: " + email));

        EntityModel<Professor> model = EntityModel.of(professor);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getProfessorById(professor.getId())).withSelfRel();
        Link allProfessorsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getAllProfessors(null, null, 0, 10)).withRel("all-professors");
        model.add(selfLink, allProfessorsLink);
        return model;
    }

    @GetMapping(params = "id")
    @ResponseStatus(HttpStatus.OK)
    public EntityModel<Professor> getProfessorById(@RequestParam long id) {
        Professor professor = professorRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Professor not found with id: " + id));

        EntityModel<Professor> model = EntityModel.of(professor);
        Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getProfessorById(id)).withSelfRel();
        Link parentLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getAllProfessors(null, null, 0, 10)).withRel("parent");
        model.add(selfLink, parentLink);
        return model;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<Professor> createProfessor(@RequestBody Professor professor) {
        try {

            professor.setEmail(professor.getFirstName().toLowerCase() + "." + professor.getLastName().toLowerCase() + "@academic.com");

            Professor savedProfessor = professorRepository.save(professor);

            EntityModel<Professor> model = EntityModel.of(savedProfessor);
            Link selfLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getProfessorById(savedProfessor.getId())).withSelfRel();
            Link allProfessorsLink = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ProfessorController.class).getAllProfessors(null, null, 0, 10)).withRel("all-professors");
            model.add(selfLink, allProfessorsLink);
            return model;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error saving professor", e);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAll() {
        try {
            professorRepository.deleteAll();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error deleting all professors", e);
        }
    }


}
