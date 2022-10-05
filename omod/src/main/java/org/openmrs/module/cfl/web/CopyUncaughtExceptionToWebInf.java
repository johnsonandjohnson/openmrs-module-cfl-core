package org.openmrs.module.cfl.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/** Copies cflUncaughtException.jsp to root WEB-INF. */
public class CopyUncaughtExceptionToWebInf implements ServletContextAware {

  private static final Log LOGGER = LogFactory.getLog(CopyUncaughtExceptionToWebInf.class);
  private static final Path MODULE_ROOT_DIR = Paths.get("WEB-INF", "view", "module", "cfl");
  private static final Path CFL_UNCAUGHT_EXCEPTION_FILE = Paths.get("cflUncaughtException.jsp");
  private static final Path WEB_INF_VIEW_DIR = Paths.get("WEB-INF", "view");

  @Override
  public void setServletContext(ServletContext servletContext) {
    Path basePath = Paths.get(servletContext.getRealPath(""));
    try {
      Path srcFile = basePath.resolve(MODULE_ROOT_DIR).resolve(CFL_UNCAUGHT_EXCEPTION_FILE);
      Path destFile =
          basePath
              .resolve(WEB_INF_VIEW_DIR)
              .resolve(CFL_UNCAUGHT_EXCEPTION_FILE);
      Files.copy(srcFile, destFile, StandardCopyOption.REPLACE_EXISTING);
    } catch (Exception e) {
      LOGGER.error("Failed to copy cflUncaughtException.jsp", e);
    }
  }
}
