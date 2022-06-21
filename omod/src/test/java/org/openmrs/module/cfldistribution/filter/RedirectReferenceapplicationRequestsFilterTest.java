package org.openmrs.module.cfldistribution.filter;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.api.context.Context;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Context.class})
public class RedirectReferenceapplicationRequestsFilterTest {

  @Mock private MockHttpServletRequest servletRequest;

  @Mock private MockHttpServletResponse servletResponse;

  @Mock private FilterChain filterChain;

  @InjectMocks private RedirectReferenceapplicationRequestsFilter filter;

  @Test
  public void shouldFilterRequestsWhenRequestUriIsValid() throws ServletException, IOException {
    when(servletRequest.getRequestURI())
        .thenReturn("resource/referenceapplication/styles/referenceapplication.css");

    filter.doFilter(servletRequest, servletResponse, filterChain);

    verify(servletResponse).sendRedirect(anyString());
  }

  @Test
  public void shouldFilterRequestsWhenRequestUriIsNotValid() throws ServletException, IOException {
    when(servletRequest.getRequestURI()).thenReturn("anyInvalidUri");

    filter.doFilter(servletRequest, servletResponse, filterChain);

    verify(filterChain).doFilter(servletRequest, servletResponse);
  }
}
