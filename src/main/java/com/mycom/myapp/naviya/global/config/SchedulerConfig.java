package com.mycom.myapp.naviya.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);  // 스레드 풀 크기 설정
        scheduler.setThreadNamePrefix("TaskScheduler-");  // 스레드 이름 설정
        return scheduler;
    }
}
