import React, { useState, useEffect } from 'react';
import { getLectureDetails } from '../../apis/api'; // Import the utility function
import {useNavigate, useParams} from 'react-router-dom';

const LectureDetails = () => {
    const { lectureId } = useParams();
    const navigate = useNavigate();
    const [lecture, setLecture] = useState(null);
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null);

    useEffect(() => {
        if (!lectureId) {
            setError('Lecture ID is not defined.');
            setLoading(false);
            return;
        }

        const fetchLectureDetails = async () => {
            try {
                setLoading(true);
                const data = await getLectureDetails(lectureId);
                setLecture(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchLectureDetails();
    }, [lectureId]);

    if (loading) {
        return <p>Loading lecture details...</p>;
    }

    if (error) {
        return <p style={{ color: 'red' }}>{error}</p>;
    }

    return (
        <div>
            <button onClick={() => navigate('/dashboard')}>Back to Dashboard</button>
            <h2>Lecture: {lecture.lecture.name}</h2>
            <p><strong>Year:</strong> {lecture.lecture.year}</p>
            <p><strong>Credits:</strong> {lecture.lecture.credits}</p>

            <h3>Details</h3>
            <p><strong>Information:</strong> {lecture.extended.information}</p>

            <h3>Evaluation Tests</h3>
            <ul>
                {lecture.extended.evaluationTests.map((test, index) => (
                    <li key={index}>
                        {test.testName} - {test.weight}%
                    </li>
                ))}
            </ul>

            <h3>Course Materials</h3>
            {lecture.extended.courseMaterials.singleFile ? (
                <p>File: {lecture.extended.courseMaterials.fileName}</p>
            ) : (
                <p>No single course material file available.</p>
            )}

            <h3>Lab Materials</h3>
            {lecture.extended.labMaterials.singleFile ? (
                <p>File: {lecture.extended.labMaterials.fileName}</p>
            ) : (
                <ul>
                    {lecture.extended.labMaterials.structured.map((file, index) => (
                        <li key={index}>{file}</li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default LectureDetails;
