package org.vaadin.presentation;

import org.vaadin.viewportservlet.ViewPortCDIServlet;

import javax.servlet.annotation.WebServlet;

/**
 * Normally with Vaadin CDI, the servlet is automatically introduced. If you
 * need to customize stuff in the servlet or host page generation, you can still
 * do that. In this example we use a servlet implementation that adds a viewport
 * meta tag to the host page. It is essential essential for applications that
 * have designed the content to be suitable for smaller screens as well.
 */
@WebServlet(urlPatterns = "/*")
public class Servlet extends ViewPortCDIServlet {

}
