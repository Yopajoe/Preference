package org.etsntesla.gava.configurations;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.config.TaskExecutorFactoryBean;

import java.util.function.Supplier;

@Configuration
public class ThreadPoolConfig {

    @Bean
    public TaskExecutorFactoryBean taskExecutorFactoryBean() {
        TaskExecutorFactoryBean factoryBean = new TaskExecutorFactoryBean();
        factoryBean.setPoolSize("50");
        factoryBean.setQueueCapacity(100);
        return factoryBean;
    }
}
