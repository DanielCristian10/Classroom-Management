package repository;

import model.LectureExtended;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureExtendedRepository extends MongoRepository<LectureExtended, String> {
    Optional<LectureExtended> findByLectureId(Long lectureId);
}
