package com.memento.server.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.memento.server.config.argumentResolver.AssociateIdArgumentResolver;
import com.memento.server.config.argumentResolver.CommunityIdArgumentResolver;
import com.memento.server.config.argumentResolver.MemberIdArgumentResolver;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new MemberIdArgumentResolver());
		resolvers.add(new AssociateIdArgumentResolver());
		resolvers.add(new CommunityIdArgumentResolver());
	}
}
