import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 1,              // 1명의 가상 유저
    duration: '10s',
    thresholds: {
        http_req_duration: ['p(95)<3000'], // 응답 시간 3000ms 이하
        http_req_failed: ['rate<0.01'],    // 실패율 1% 미만
    },
};

export default function () {
    let url = 'http://host.docker.internal:8080/lottery/submit';

    // 1부터 100까지 반복하면서 각기 다른 유저 정보로 요청
    for (let userId = 1; userId <= 100; userId++) {
        let payload = JSON.stringify({
            name: `User${userId}`,
            phone: `0101234567${String(userId).padStart(2, '0')}`  // 각 유저에 대해 고유한 전화번호
        });

        let params = {
            headers: {
                'Content-Type': 'application/json',
            },
        };

        // POST 요청 보내기
        let response = http.post(url, payload, params);

        check(response, {
            'is success or correct message': (r) =>
                r.status === 200 && typeof r.body === 'string',
        });

        sleep(0.1);  // 각 요청 간 0.1초 대기
    }
}
