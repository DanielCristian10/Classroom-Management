package com.example.spring_boot.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String groupName;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String email;
    private int year;

    @ManyToMany(mappedBy = "students")
    @ToString.Exclude
    private Set<Lecture> lectures = new HashSet<>();

    public void addLecture(Lecture lecture) {
        this.lectures.add(lecture);
        lecture.getStudents().add(this);
    }
}
