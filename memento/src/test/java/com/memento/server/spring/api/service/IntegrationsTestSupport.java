package com.memento.server.spring.api.service;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.memento.server.api.service.minio.MinioService;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationsTestSupport {

	@MockitoBean
	protected MinioService minioService;
}
