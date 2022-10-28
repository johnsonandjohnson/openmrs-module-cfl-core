package org.openmrs.module.cfl.api.service.impl;

import org.openmrs.api.context.Context;
import org.openmrs.module.cfl.CfldistributionGlobalParameterConstants;
import org.openmrs.module.cfl.api.service.CaptchaService;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

public class CaptchaServiceImpl implements CaptchaService {
	
	private RestTemplate restTemplate = new RestTemplate();
	
	private ReCaptchaAttemptService reCaptchaAttemptService;
	
	private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CaptchaService.class);
	
	protected static final String RECAPTCHA_URL_TEMPLATE = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s";
	
	@Override
	public void processResponse(HttpServletRequest request) {
		
		String response = request.getParameter("g-recaptcha-response");
		
		securityCheck(response, request);
		final URI verifyUri = URI.create(String.format(RECAPTCHA_URL_TEMPLATE, getReCaptchaSecret(), response, getClientIP(request)));
		try {
			final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
			LOGGER.debug("Google's response: {} ", googleResponse.toString());
			
			if (!googleResponse.isSuccess()) {
				if (googleResponse.hasClientError()) {
					reCaptchaAttemptService.reCaptchaFailed(getClientIP(request));
				}
				throw new ReCaptchaInvalidException("reCaptcha was not successfully validated");
			}
		} catch (RestClientException rce) {
			throw new ReCaptchaInvalidException("Login unavailable at this time.  Please try again later.", rce);
		}
		reCaptchaAttemptService.reCaptchaSucceeded(getClientIP(request));
	}
	
	private String getClientIP(HttpServletRequest request) {
		final String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}
	
	public String getReCaptchaSecret() {
		return Context.getAdministrationService().getGlobalProperty(CfldistributionGlobalParameterConstants.GOOGLE_RECAPTCHA_SECRET_KEY);
	}
	
	private boolean responseSanityCheck(String response) {
		return StringUtils.hasLength(response) && RESPONSE_PATTERN.matcher(response).matches();
	}
	
	public void setReCaptchaAttemptService(ReCaptchaAttemptService reCaptchaAttemptService) {
		this.reCaptchaAttemptService = reCaptchaAttemptService;
	}
	
	private void securityCheck(String response, HttpServletRequest request) {
		if (!responseSanityCheck(response)) {
			throw new ReCaptchaInvalidException("Response contains invalid characters");
		}
		if (reCaptchaAttemptService.isBlocked(getClientIP(request))) {
			throw new ReCaptchaInvalidException("Client exceeded maximum number of failed attempts");
		}
	}
}