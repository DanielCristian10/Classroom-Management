import api from './api';

export const getProfessorByEmail = async (email) => {
    const response = await api.get(`http://localhost:8083/api/academia/professors/by-email?email=${email}`);
    return response.data;
};

export const getLecturesByProfessor = async (professorId) => {
    const response = await api.get(`http://localhost:8083/api/academia/professors/${professorId}/lectures`);
    return response.data._embedded.lectureList;
};

export const getAllLectures = async () => {
    const response = await api.get('http://localhost:8084/api/academia/lectures');
    return response.data._embedded.lectureList;
};

export const getStudentsInLecture = async (lectureId) => {
    const response = await api.get(`http://localhost:8083/api/academia/lectures/${lectureId}/students`);
    return response.data._embedded.studentList;
};

export const assignLectureToProfessor = async (professorId, lectureId) => {
    await api.post(`/api/academia/professors/${professorId}/lectures/${lectureId}`);
};

export const createOrUpdateLectureDetails = async (lectureId, lectureDetails) => {
    await api.put('http://localhost:8083/api/mongo/lectures', {lectureId, ...lectureDetails});
};
