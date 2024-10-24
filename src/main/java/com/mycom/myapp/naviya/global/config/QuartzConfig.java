package com.mycom.myapp.naviya.global.config;

import com.mycom.myapp.naviya.global.scheduler.ChildDeletionJob;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.scheduling.quartz.SpringBeanJobFactory;

import javax.sql.DataSource;

@Configuration
public class QuartzConfig {

    @Value("${spring.quartz.properties.org.quartz.scheduler.instanceName}")
    private String instanceName;

    private final ApplicationContext applicationContext;

    public QuartzConfig(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource) {
        SchedulerFactoryBean schedulerFactoryBean = new SchedulerFactoryBean();

        // 데이터 소스를 설정하여 Quartz가 데이터베이스에 접근하도록 설정
        schedulerFactoryBean.setDataSource(dataSource);

        // 스프링 빈 주입을 지원하는 JobFactory 설정
        SpringBeanJobFactory jobFactory = springBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        schedulerFactoryBean.setJobFactory(jobFactory);

        // 스케줄러 이름 설정
        schedulerFactoryBean.setSchedulerName(instanceName);
        return schedulerFactoryBean;
    }

    @Bean
    public SpringBeanJobFactory springBeanJobFactory() {
        return new SpringBeanJobFactory();
    }

    @Bean
    public JobDetail childDeletionJobDetail() {
        // ChildDeletionJob을 정의하고, 영구적으로 저장되도록 설정
        return JobBuilder.newJob(ChildDeletionJob.class)
                .withIdentity("childDeletionJob")
                .storeDurably() // 트리거 없이도 유지되도록 설정
                .build();
    }

    @Bean
    public Trigger childDeletionTrigger(JobDetail jobDetail) {
        // JobDetail과 연결된 트리거를 생성, 즉시 시작되도록 설정
        return TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("childDeletionTrigger")
                .startNow() // 스케줄링 시점을 필요에 따라 변경 가능
                .build();
    }
}
