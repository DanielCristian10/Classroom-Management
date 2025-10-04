package model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)

@NoArgsConstructor
@AllArgsConstructor

@Entity
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include

    private int id;
    private String name;
    private int year;
    private int credits;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "professor_lecture", joinColumns = @JoinColumn(name = "lecture_id"), inverseJoinColumns = @JoinColumn(name = "professor_id"))
    @ToString.Exclude
    @JsonIgnore
    private Set<Professor> professors = new HashSet<>();

    public void addProfessor(Professor professor) {
        this.professors.add(professor);
        professor.getLectures().add(this);
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "student_lecture", joinColumns = @JoinColumn(name = "lecture_id"), inverseJoinColumns = @JoinColumn(name = "student_id"))
    @ToString.Exclude
    @JsonIgnore
    private Set<Student> students = new HashSet<>();

    public void addStudent(Student student) {
        this.students.add(student);
        student.getLectures().add(this);
    }

}
