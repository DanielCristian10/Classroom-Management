package com.example.spring_boot.controller;

import com.example.spring_boot.authorization.JwtUtil;
import com.example.spring_boot.repository.LectureExtendedRepository;
import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.LectureExtended;
import com.example.spring_boot.model.Student;
import com.example.spring_boot.repository.LectureRepository;
import com.example.spring_boot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import static com.example.spring_boot.authorization.JwtUtil.extractJwtFromRequest;

@RestController
@RequestMapping("/api/mongo/lectures")
public class LectureExtendedController {

    @Autowired
    private LectureExtendedRepository lectureExtendedRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PutMapping
    @PreAuthorize("hasRole('PROFESSOR')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<LectureExtended> createOrUpdateLectureExtended(@RequestBody LectureExtended lectureExtendedRequest) {
        Long mySqlLectureId = lectureExtendedRequest.getLectureId();

        lectureRepository.findById(mySqlLectureId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lecture not found in mySQL with id: " + mySqlLectureId));


        LectureExtended lectureExtended = lectureExtendedRepository.findByLectureId(mySqlLectureId).orElse(new LectureExtended());

        String token = extractJwtFromRequest();
        String extractedEmail = JwtUtil.extractEmail(token);
        String extractedRole = JwtUtil.extractRole(token);

        Lecture lecture = lectureRepository.findById(mySqlLectureId).orElseThrow(() -> new ExpressionException("Lecture not found with id: " + mySqlLectureId));

        boolean isAuthorized = false;
        if (extractedRole.equals("professor")) {
            isAuthorized = lecture.getProfessors().stream().anyMatch(professor -> professor.getEmail() != null && professor.getEmail().equals(extractedEmail));
        }
        if (!isAuthorized) {
            throw new AccessDeniedException("Not allowed to access this lecture");
        }

        lectureExtended.setLectureId(mySqlLectureId);
        lectureExtended.setInformation(lectureExtendedRequest.getInformation());
        lectureExtended.setEvaluationTests(lectureExtendedRequest.getEvaluationTests());
        lectureExtended.setCourseMaterials(lectureExtendedRequest.getCourseMaterials());
        lectureExtended.setLabMaterials(lectureExtendedRequest.getLabMaterials());

        lectureExtended.validateWeights();

        LectureExtended saved = lectureExtendedRepository.save(lectureExtended);

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    @DeleteMapping("/{lectureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<Void> deleteLectureExtended(@PathVariable Long lectureId) {
        LectureExtended lectureExtended = lectureExtendedRepository.findByLectureId(lectureId).orElseThrow(() -> new ExpressionException("No extended data found for lecture id: " + lectureId));

        String token = extractJwtFromRequest();
        String extractedEmail = JwtUtil.extractEmail(token);
        String extractedRole = JwtUtil.extractRole(token);

        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow(() -> new ExpressionException("Lecture not found with id: " + lectureId));

        boolean isAuthorized = false;
        if (extractedRole.equals("professor")) {
            isAuthorized = lecture.getProfessors().stream().anyMatch(professor -> professor.getEmail() != null && professor.getEmail().equals(extractedEmail));
        }
        if (!isAuthorized) {
            throw new AccessDeniedException("Not allowed to access this lecture");
        }

        lectureExtendedRepository.delete(lectureExtended);
        return ResponseEntity.noContent().build();
    }
}
