package com.example.spring_boot.model;


import com.example.spring_boot.model.Lecture;
import com.example.spring_boot.model.LectureExtended;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureDetail {

    private Lecture lecture;
    private LectureExtended extended;

}
