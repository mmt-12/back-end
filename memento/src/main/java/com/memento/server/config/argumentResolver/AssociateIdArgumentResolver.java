package com.memento.server.config.argumentResolver;

import static com.memento.server.common.error.ErrorCodes.TOKEN_NOT_VALID;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.memento.server.annotation.AssociateId;
import com.memento.server.api.service.auth.MemberPrincipal;
import com.memento.server.common.exception.MementoException;

public class AssociateIdArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AssociateId.class) &&
			parameter.getParameterType().equals(Long.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
		NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		MemberPrincipal memberPrincipal = (MemberPrincipal)auth.getPrincipal();

		if (memberPrincipal.associateId() == null) {
			throw new MementoException(TOKEN_NOT_VALID);
		}
		return memberPrincipal.associateId();
	}
}
