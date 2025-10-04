import React, {useEffect, useState} from 'react';
import {getToken} from '../../utils/jwt';
import {useParams} from 'react-router-dom';
import {useNavigate} from 'react-router-dom';

const StudentDashboard = ({studentId, onLogout}) => {
    const [student, setStudent] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (!studentId) {
            setError('Student ID is not defined.');
            setLoading(false);
            return;
        }

        const fetchStudentData = async () => {
            try {
                setLoading(true);
                const token = getToken();

                if (!token) {
                    throw new Error('Authentication token not found. Please log in again.');
                }

                const response = await fetch(`http://localhost:8082/api/academia/students?id=${studentId}`, {
                    method: 'GET', headers: {
                        'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json',
                    },
                });

                if (!response.ok) {
                    const errorResponse = await response.text();
                    throw new Error(`Failed to fetch student data (${response.status}): ${errorResponse || response.statusText}`);
                }

                const data = await response.json();
                setStudent(data);
            } catch (err) {
                setError(err.message);
            } finally {
                setLoading(false);
            }
        };

        fetchStudentData();
    }, [studentId]);

    if (loading) {
        return <p>Loading student data...</p>;
    }

    if (error) {
        return <p style={{color: 'red'}}>{error}</p>;
    }

    return (<div>
        <button onClick={onLogout}>Logout</button>
        <h2>Welcome, {student.firstName} {student.lastName}</h2>

        <h3>My Lectures</h3>
        {student.lectures && student.lectures.length > 0 ? (<ul>
            {student.lectures.map((lecture) => (<li key={lecture.id}>
                <button onClick={() => navigate(`/lectures/${lecture.id}`)}>
                    {lecture.name}
                </button>
            </li>))}
        </ul>) : (<p>No lectures assigned yet.</p>)}
    </div>);
};

export default StudentDashboard;