import {IdmServiceClient} from '../kproto/idm_grpc_web_pb';
import * as idm from "./idm_pb";

const grpc = require('@grpc/grpc-js');

const client = new IdmServiceClient('http://localhost:9100', null, null);

export function authenticate(username, password) {
    return new Promise((resolve, reject) => {
        const request = new idm.AuthRequest();

        request.setUsername(username);
        request.setPassword(password);

        client.authenticate(request, {}, (err, response) => {
            if (err) {
                return reject(err);
            }
            return resolve(response);
        });
    });
}

