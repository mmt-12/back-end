package com.memento.server.config.async;

import java.util.Arrays;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.memento.server.common.exception.MementoException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

	@Bean("appExecutor")
	public ThreadPoolTaskExecutor appExecutor(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(50);

		executor.setThreadNamePrefix("fcm-async-");
		executor.initialize();
		return executor;
	}

	@Bean("achievement")
	public ThreadPoolTaskExecutor achievement(){
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(10);
		executor.setMaxPoolSize(20);
		executor.setQueueCapacity(50);

		executor.setThreadNamePrefix("achievement-async-");
		executor.initialize();
		return executor;
	}

	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return (ex, method, params) -> {
			String methodName = method.getDeclaringClass().getSimpleName() + "." + method.getName();

			if (ex instanceof MementoException) {
				MementoException mementoEx = (MementoException) ex;
				log.warn("FCM Async 비즈니스 예외 발생 - Method: {}, ErrorCode: {}, Message: {}",
					methodName, mementoEx.getErrorCode(), ex.getMessage());
			} else {
				log.error("FCM Async 시스템 예외 발생 - Method: {}, Parameters: {}, Exception: {}",
					methodName, Arrays.toString(params), ex.getMessage(), ex);
			}
		};
	}
}
