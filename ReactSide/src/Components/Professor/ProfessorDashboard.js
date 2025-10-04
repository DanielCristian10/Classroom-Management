import React, {useEffect, useState} from 'react';
import {
    getLecturesByProfessor, createOrUpdateLectureDetails, getStudentsInLecture
} from '../../apis/professorApi';
import {
    getLectureDetails
} from '../../apis/api';

const ProfessorDashboard = ({professorId, onLogout}) => {
    const [assignedLectures, setAssignedLectures] = useState([]);
    const [selectedLectureDetails, setSelectedLectureDetails] = useState(null);
    const [students, setStudents] = useState([]);
    const [formData, setFormData] = useState({
        information: '',
        evaluationTests: [],
        courseMaterials: {singleFile: false, fileName: '', structured: []},
        labMaterials: {singleFile: false, fileName: '', structured: []},
    });
    const [isEditing, setIsEditing] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchAssignedLectures = async () => {
            try {
                setLoading(true);
                const assigned = await getLecturesByProfessor(professorId);
                setAssignedLectures(assigned);
            } catch (err) {
                console.error('Error fetching assigned lectures:', err);
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchAssignedLectures();
    }, [professorId]);

    const handleViewDetails = async (lectureId) => {
        try {
            setLoading(true);

            const details = await getLectureDetails(lectureId);
            setSelectedLectureDetails(details);

            const enrolledStudents = await getStudentsInLecture(lectureId);
            setStudents(enrolledStudents);

            setFormData({
                information: details.extended?.information || '',
                evaluationTests: details.extended?.evaluationTests || [],
                courseMaterials: details.extended?.courseMaterials || {singleFile: false, fileName: '', structured: []},
                labMaterials: details.extended?.labMaterials || {singleFile: false, fileName: '', structured: []},
            });
            setIsEditing(false);
        } catch (err) {
            console.error('Error fetching lecture details or students:', err);
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const handleFormChange = (e) => {
        const {name, value} = e.target;
        setFormData((prev) => ({...prev, [name]: value}));
    };

    const handleSaveChanges = async () => {
        try {
            if (!selectedLectureDetails) return;

            const updatedDetails = {
                lectureId: selectedLectureDetails.lecture.id, ...formData,
            };

            await createOrUpdateLectureDetails(selectedLectureDetails.lecture.id, updatedDetails);

            const updatedDetailsFromApi = await getLectureDetails(selectedLectureDetails.lecture.id);
            setSelectedLectureDetails(updatedDetailsFromApi);

            setIsEditing(false);
        } catch (err) {
            console.error('Error saving lecture details:', err);
            setError(err.message);
        }
    };

    if (loading) {
        return <p>Loading professor data...</p>;
    }

    if (error) {
        return <p style={{color: 'red'}}>{error}</p>;
    }

    return (<div className="container">
        <h2>Professor Dashboard</h2>
        <button onClick={onLogout} className="logout-button">
            Logout
        </button>

        <h3>Assigned Courses</h3>
        {assignedLectures.length > 0 ? (<ul>
            {assignedLectures.map((lecture) => (<li key={lecture.id}>
                            <span>
                                {lecture.name} ({lecture.credits} credits)
                            </span>
                <button onClick={() => handleViewDetails(lecture.id)}>View Details</button>
            </li>))}
        </ul>) : (<p>No courses assigned yet.</p>)}

        {selectedLectureDetails && (<div className="lecture-details">
            <h3>Details for Lecture: {selectedLectureDetails.lecture.name}</h3>
            <p>
                <strong>Year:</strong> {selectedLectureDetails.lecture.year}
            </p>
            <p>
                <strong>Credits:</strong> {selectedLectureDetails.lecture.credits}
            </p>

            <h4>Information</h4>
            {isEditing ? (<textarea
                name="information"
                value={formData.information}
                onChange={handleFormChange}
            />) : (<p>{selectedLectureDetails.extended?.information || 'N/A'}</p>)}

            <h4>Evaluation Tests</h4>
            {isEditing ? (<textarea
                name="evaluationTests"
                value={JSON.stringify(formData.evaluationTests, null, 2)}
                onChange={(e) => setFormData((prev) => ({
                    ...prev, evaluationTests: JSON.parse(e.target.value),
                }))}
            />) : (<ul>
                {selectedLectureDetails.extended?.evaluationTests.map((test, index) => (<li key={index}>
                    {test.testName} - {test.weight}%
                </li>))}
            </ul>)}

            <h4>Course Materials</h4>
            <p>
                {selectedLectureDetails.extended?.courseMaterials.singleFile ? `File: ${selectedLectureDetails.extended.courseMaterials.fileName}` : 'Multiple files available'}
            </p>

            <h4>Lab Materials</h4>
            <ul>
                {selectedLectureDetails.extended?.labMaterials.structured?.map((file, index) => (
                    <li key={index}>{file}</li>))}
            </ul>

            <h4>Enrolled Students</h4>
            {students.length > 0 ? (<ul>
                {students.map((student) => (<li key={student.id}>
                    {student.firstName} {student.lastName} - {student.email}
                </li>))}
            </ul>) : (<p>No students enrolled yet.</p>)}

            {isEditing ? (<>
                <button onClick={handleSaveChanges}>Save Changes</button>
                <button onClick={() => setIsEditing(false)}>Cancel</button>
            </>) : (<button onClick={() => setIsEditing(true)}>Edit Lecture</button>)}
        </div>)}
    </div>);
};

export default ProfessorDashboard;
