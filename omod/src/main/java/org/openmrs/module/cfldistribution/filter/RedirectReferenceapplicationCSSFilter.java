package org.openmrs.module.cfldistribution.filter;

import org.openmrs.ui.framework.WebConstants;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * The Request Filter which redirects requests for OpenMRS reference application module's
 * referenceapplication.css resource to referenceapplication.css located in this project. For
 * example, the Admin View OWA uses CSS from OpenMRS reference application.
 */
public class RedirectReferenceapplicationCSSFilter implements Filter {
  private static final String REFERENCEAPPLICATION_CSS_RESOURCE =
      "resource/referenceapplication/styles/referenceapplication.css";
  private static final String CFL_STYLE_RESOURCE_CONTEXT_RELATIVE =
      "/ms/uiframework/resource/cfldistribution/styles/referenceapplication.css";

  @Override
  public void init(FilterConfig filterConfig) {}

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {

    if (servletRequest instanceof HttpServletRequest) {
      final HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;

      if (shouldRedirectCSS(httpServletRequest)) {
        final String cflStyleLocation =
            "/" + WebConstants.CONTEXT_PATH + CFL_STYLE_RESOURCE_CONTEXT_RELATIVE;
        final HttpServletResponse resp = (HttpServletResponse) servletResponse;
        resp.sendRedirect(cflStyleLocation);
        return;
      }
    }

    filterChain.doFilter(servletRequest, servletResponse);
  }

  @Override
  public void destroy() {}

  private boolean shouldRedirectCSS(HttpServletRequest httpServletRequest) {
    return URI.create(httpServletRequest.getRequestURI())
        .getPath()
        .endsWith(REFERENCEAPPLICATION_CSS_RESOURCE);
  }
}
