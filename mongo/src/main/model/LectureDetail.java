package model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import model.model.LectureExtended;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LectureDetail {

    private Lecture lecture;
    private LectureExtended extended;

}
