package com.exception.aop;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Aspect
@Configuration

/**
 * The easy way to do it is to rely on RequestContextHolder. In every request,
 * the DispatcherServlet binds the current HttpServletRequest to a static
 * ThreadLocal object in the RequestContextHolder. You can retrieve it when
 * executing within the same Thread with
 * 
 * HttpServletRequest request = ((ServletRequestAttributes)
 * RequestContextHolder.currentRequestAttributes()).getRequest();
 * 
 * @author akash
 *
 */
public class CommonServiceAspect {

	private static final Logger LOG = LoggerFactory.getLogger(CommonServiceAspect.class);

	@Around("execution(* com.microservice.controller.*.*(..))")
	public Object logServiceFlow(ProceedingJoinPoint joinPoint) throws Throwable {
		LOG.info("Calling logServiceFlow() method");
		// Instant start = Instant.now();
		Object serviceResponse = printServiceRequestResponse(joinPoint);
		// Instant end = Instant.now();
		// LOG.debug("Total Response Time in millis :: " + Duration.between(start,
		// end).getSeconds() + "ms");
		LOG.info("Exit logServiceFlow() method");
		return serviceResponse;
	}

	private Object printServiceRequestResponse(ProceedingJoinPoint joinPoint)
			throws JsonProcessingException, Throwable {
		String serviceUrl = null;
		// String methodName = joinPoint.getSignature().getName();
		ObjectMapper mapper = new ObjectMapper();
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
				.getRequest();
		if (request != null) {
			serviceUrl = request.getRequestURL().toString();
			LOG.info("REQUEST_ENTRY : " + serviceUrl);
		}
		Object[] signature = joinPoint.getArgs();
		if (signature != null && signature.length > 0) {
			LOG.debug("Request Body : " + mapper.writeValueAsString(signature[0]));

		}
		Object serviceResponse = joinPoint.proceed();
		if (serviceResponse != null) {
			LOG.debug("Response Body : " + mapper.writeValueAsString(serviceResponse));

		}
		LOG.info("REQUEST_EXIT :" + serviceUrl);
		return serviceResponse;
	}

}
