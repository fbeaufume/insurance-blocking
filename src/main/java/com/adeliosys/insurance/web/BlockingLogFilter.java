package com.adeliosys.insurance.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Servlet filter that logs the request duration and adds a request ID in the logs through SLF4J MDC.
 */
@WebFilter(filterName = "accessLogFilter", urlPatterns = {"/api/*"})
public class BlockingLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BlockingLogFilter.class);

    private static final AtomicLong COUNTER = new AtomicLong();

    @Override
    public void init(FilterConfig fConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        long timestamp = System.currentTimeMillis();

        StringBuffer url = req.getRequestURL();
        String queryString = req.getQueryString();
        if (queryString != null) {
            url.append('?').append(queryString);
        }

        try (MDC.MDCCloseable ignored = MDC.putCloseable("requestId", Long.toString(COUNTER.incrementAndGet()))) {
            LOGGER.info("Serving {} '{}'", req.getMethod(), url);

            chain.doFilter(request, response);

            LOGGER.info("Served {} '{}' as {} in {} msec",
                    req.getMethod(), url, res.getStatus(), System.currentTimeMillis() - timestamp);
        }
    }

    @Override
    public void destroy() {
    }
}
