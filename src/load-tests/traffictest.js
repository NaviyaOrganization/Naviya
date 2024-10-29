import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    stages: [
        { duration: '5s', target: 50 },  // 5초 동안 50 VUs까지 점진적으로 증가
        { duration: '5s', target: 100 }, // 5초 동안 100 VUs 유지
        { duration: '10s', target: 500 } // 이후 10초 동안 500 VUs 유지
    ],
    thresholds: {
        http_req_duration: ['p(95)<3000'], // 응답 시간 3000ms 이하
        http_req_failed: ['rate<0.01'],    // 실패율 1% 미만
    },
};

// 프로그램 목록 정의
const programs = ['창의력', '논리력', '사회성', '신체'];

export default function () {
    let userId = __VU;  // 각 가상 유저별 고유 ID 사용
    let url = 'http://host.docker.internal:8080/lottery/submit';

    // VU별로 고정된 프로그램 선택 (부하 테스트의 일관성을 위해)
    let selectedProgram = programs[userId % programs.length];

    let payload = JSON.stringify({
        name: `User${userId}`,
        phone: `0101234567${String(userId).padStart(2, '0')}`,
        selectedProgram: selectedProgram
    });

    let params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    sleep(Math.random() * 2); // 각 유저가 요청을 보내기 전에 최대 2초까지 대기

    // 가상 유저가 1초에 10번씩 요청을 보내기 위해 반복
    for (let i = 0; i < 200; i++) {
        let response = http.post(url, payload, params);

        // 요청 성공 여부 확인
        check(response, {
            'is success or correct message': (r) =>
                r.status === 200 && typeof r.body === 'string',
        });
        sleep(0.1); // 각 요청 간격을 0.1초로 설정
    }
}