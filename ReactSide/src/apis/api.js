import {IdmServiceClient} from '../Components/IdmServiceClientPb.ts';
import {AuthRequest, TokenRequest} from '../mproto/idm_pb';
import {getToken} from '../utils/jwt';
import axios from 'axios';

const client = new IdmServiceClient('http://localhost:9100');
const api = axios.create({
    baseURL: 'http://localhost:8083/api/academia', headers: {
        'Content-Type': 'application/json',
    },
});

export const getLectureDetails = async (lectureId) => {
    try {
        const response = await api.get(`/lectures/${lectureId}/details`);
        return response.data;
    } catch (err) {
        console.error('Error fetching lecture details:', err);
        throw new Error(err.response?.data?.error || 'Failed to fetch lecture details');
    }
};

api.interceptors.request.use((config) => {
    const token = getToken();
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, (error) => Promise.reject(error));

export default api;
export const authenticate = (username, password) => {
    return new Promise((resolve, reject) => {
        const request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);

        client.authenticate(request, {}, (err, response) => {
            if (err) {
                reject(err);
                return;
            }
            resolve(response.getTokenvalue());
        });
    });
};
export const getStudentIdByEmail = async (email) => {
    try {
        const response = await api.get(`/students/by-email?email=${email}`);
        return response.data.id;
    } catch (err) {
        console.error('Error fetching student ID:', err);
        throw new Error(err.response?.data?.error || 'Failed to fetch student ID');
    }
};
export const getProfessorByEmail = async (email) => {
    try {
        const response = await api.get(`/professors/by-email?email=${email}`);
        return response.data;
    } catch (err) {
        console.error('Error fetching professor by email:', err);
        throw new Error(err.response?.data?.error || 'Failed to fetch professor by email');
    }
};
export const validateToken = (token) => {
    return new Promise((resolve, reject) => {
        const request = new TokenRequest();
        request.setTokenvalue(token);

        client.validateToken(request, {}, (err, response) => {
            if (err) {
                reject(err);
                return;
            }
            resolve(response.getValid());
        });
    });
};
