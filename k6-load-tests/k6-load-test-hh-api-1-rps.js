import http from 'k6/http';

export const options = {
    scenarios: {
        constant_request_rate: {
            executor: 'constant-arrival-rate',
            rate: 1,
            timeUnit: '1s',
            duration: '1m',
            preAllocatedVUs: 1,
            maxVUs: 10,
        },
    },
};

export default function () {
    http.get('http://localhost:8080/quota/hh-api');
};