package org.vaadin.presentation;

import net.sf.ehcache.constructs.web.filter.GzipFilter;

import javax.servlet.annotation.WebFilter;

/**
 * It is a good idea to use GZIP compression for client-server traffic. This can
 * be done in front proxy or with an application specific solution like here.
 * This is optional though.
 *
 */
@WebFilter("/UIDL/*")
public class CompressionFilter extends GzipFilter {

    public CompressionFilter() {

    }
}
