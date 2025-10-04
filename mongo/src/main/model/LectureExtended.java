package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lectures_extended")
public class LectureExtended {

    @Id
    private String id;

    private Long lectureId;

    private String information;

    private List<EvaluationTest> evaluationTests = new ArrayList<>();

    private Materials courseMaterials;
    private Materials labMaterials;


    public void validateWeights() {
        double totalWeight = this.evaluationTests.stream().mapToDouble(EvaluationTest::getWeight).sum();

        if (totalWeight > 100.0) {
            throw new IllegalArgumentException("Total weights > 100%");
        }
    }
}