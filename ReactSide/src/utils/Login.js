import React, {useState} from 'react';
import {AuthRequest} from '../mproto/idm_pb';
import {IdmServiceClient} from '../Components/IdmServiceClientPb.ts';
import {saveToken} from './jwt';

const client = new IdmServiceClient('http://localhost:9100');

const LoginForm = ({onLogin}) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);

    const handleLogin = () => {
        const request = new AuthRequest();
        request.setUsername(username);
        request.setPassword(password);

        client.authenticate(request, {}, (err, response) => {
            if (err) {
                setError('Authentication failed.');
                console.error(err);
                return;
            }

            const token = response.getTokenvalue();
            saveToken(token);
            onLogin(token);
        });
    };

    return (<div>
            <h2>Login</h2>
            {error && <p style={{color: 'red'}}>{error}</p>}
            <input
                type="text"
                placeholder="Email"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
            />
            <br/>

            <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
            />
            <br/>
            <button onClick={handleLogin}>Login</button>
        </div>);
};

export default LoginForm;
