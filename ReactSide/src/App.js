import LoginForm from './utils/Login.js';
import ProfessorDashboard from './Components/Professor/ProfessorDashboard';
import StudentDashboard from './Components/Student/StudentDashboard';
import React, {useState, useEffect} from 'react';
import {jwtDecode} from 'jwt-decode';
import {getToken, clearToken} from './utils/jwt';
import {BrowserRouter as Router, Route, Routes, Navigate} from 'react-router-dom';
import LectureDetails from "./Components/LectureDetails/LectureDetails";
import {getStudentIdByEmail, getProfessorByEmail} from './apis/api';

import './styles/styles.css';

const App = () => {
    const [token, setToken] = useState(null);
    const [role, setRole] = useState(null);
    const [userId, setUserId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        const storedToken = getToken();
        if (storedToken) {
            setToken(storedToken);
            try {
                const decoded = jwtDecode(storedToken);
                console.log('Decoded Token:', decoded);
                setRole(decoded.role);

                if (decoded.role === 'student') {
                    setLoading(true);
                    getStudentIdByEmail(decoded.email)
                        .then((id) => {
                            setUserId(id);
                        })
                        .catch((err) => {
                            console.error('Error fetching student ID:', err);
                            setError(err.message);
                        })
                        .finally(() => {
                            setLoading(false);
                        });
                } else if (decoded.role === 'professor') {
                    setLoading(true);
                    getProfessorByEmail(decoded.email)
                        .then((professor) => {
                            setUserId(professor.id);
                        })
                        .catch((err) => {
                            console.error('Error fetching professor ID:', err);
                            setError(err.message);
                        })
                        .finally(() => {
                            setLoading(false);
                        });
                } else {
                    throw new Error('Invalid role in token.');
                }
            } catch (err) {
                console.error('Error decoding JWT:', err);
                clearToken();
            }
        }
    }, []);

    const handleLogin = (newToken) => {
        setToken(newToken);
        try {
            const decoded = jwtDecode(newToken);
            setRole(decoded.role);

            if (decoded.role === 'student') {
                setLoading(true);
                getStudentIdByEmail(decoded.email)
                    .then((id) => {
                        setUserId(id);
                    })
                    .catch((err) => {
                        console.error('Error fetching student ID:', err);
                        setError(err.message);
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            } else if (decoded.role === 'professor') {
                setLoading(true);
                getProfessorByEmail(decoded.email)
                    .then((professor) => {
                        setUserId(professor.id);
                    })
                    .catch((err) => {
                        console.error('Error fetching professor ID:', err);
                        setError(err.message);
                    })
                    .finally(() => {
                        setLoading(false);
                    });
            }
        } catch (err) {
            console.error('Error decoding JWT:', err);
        }
    };

    const handleLogout = () => {
        clearToken();
        setToken(null);
        setRole(null);
        setUserId(null);
    };

    if (!token) {
        return (<Router>
            <Routes>
                <Route path="*" element={<LoginForm onLogin={handleLogin}/>}/>
            </Routes>
        </Router>);
    }

    if (loading) {
        return <p>Loading user information...</p>;
    }

    if (error) {
        return <p style={{color: 'red'}}>{error}</p>;
    }

    return (<Router>
        <Routes>
            <Route path="/lectures/:lectureId" element={<LectureDetails/>}/>
            {role === 'professor' ? (<Route
                path="/dashboard"
                element={<ProfessorDashboard professorId={userId} onLogout={handleLogout}/>}
            />) : (<Route
                path="/dashboard"
                element={userId ? (<StudentDashboard studentId={userId} onLogout={handleLogout}/>) : (
                    <p>Loading student dashboard...</p>)}
            />)}
            <Route path="*" element={<Navigate to="/dashboard"/>}/>
        </Routes>
    </Router>);
};

export default App;