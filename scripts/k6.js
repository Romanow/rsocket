import http from "k6/http";
import {check} from "k6";

export const options = {
    stages: [
        {duration: '30s', target: 50},
        {duration: '30s', target: 100},
        {duration: '1m30s', target: 200},
        {duration: '20s', target: 50},
    ],
};

export default function () {
    let response = http.get("http://localhost:8080/api/v1/generate");
    check(response, {
        "status is 200": (r) => r.status === 200,
        "content is present": (r) => !!r.body,
    });
};