package com.example.spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String firstName;
    private String lastName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;
    @ManyToMany(mappedBy = "professors")
    @ToString.Exclude
    private Set<Lecture> lectures = new HashSet<>();

    public void addLecture(Lecture lecture) {
        this.lectures.add(lecture);
        lecture.getProfessors().add(this);
    }


}
