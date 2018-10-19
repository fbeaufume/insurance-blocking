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
 * Servlet filter used to check the behavior of servlet filters in reactive applications.
 *
 * This filter add a request ID in the SLF4J MDC and log some HTTP info.
 *
 * Sample log of a blocking request (everything is fine) :
 * 09:39:26.334  INFO [requestId=1] 13320 --- [nio-8080-exec-1] c.a.insurance.web.AccessLogFilter        : Serving GET 'http://localhost:8080/blocking/pause/1000'
 * 09:39:26.363  INFO [requestId=1] 13320 --- [nio-8080-exec-1] c.a.i.web.BlockingQuoteController        : Pausing for 1000 msec
 * 09:39:27.365  INFO [requestId=1] 13320 --- [nio-8080-exec-1] c.a.i.web.BlockingQuoteController        : Paused for 1000 msec
 * 09:39:27.395  INFO [requestId=1] 13320 --- [nio-8080-exec-1] c.a.insurance.web.AccessLogFilter        : Served GET 'http://localhost:8080/blocking/pause/1000' as 200 in 1061 msec
 *
 * Sample log of a reactive request (issues here) :
 * 09:40:21.816  INFO [requestId=2] 13320 --- [nio-8080-exec-2] c.a.insurance.web.AccessLogFilter        : Serving GET 'http://localhost:8080/reactive/pause/1000'
 * 09:40:21.820  INFO [requestId=2] 13320 --- [nio-8080-exec-2] c.a.i.web.ReactiveQuoteController        : Pausing for 1000 msec
 * 09:40:21.852  INFO [requestId=2] 13320 --- [nio-8080-exec-2] c.a.insurance.web.AccessLogFilter        : Served GET 'http://localhost:8080/reactive/pause/1000' as 200 in 36 msec
 * 09:40:22.860  INFO [requestId=] 13320 --- [     parallel-1] c.a.i.web.ReactiveQuoteController        : Paused for 1000 msec
 *
 * This shows that the filter does not work well in reactive mode:
 * - The filter being executed with the first request thread, it ends too soon
 * - So the response status can be incorrect
 * - So the request duration is incorrect
 * - Etc, things that depends on the end the the request are incorrect
 * - The MDC is not propagated to the worker threads (but this is not a filter related issue)
 */
@WebFilter(filterName = "accessLogFilter", urlPatterns = {"/api/*"})
public class AccessLogFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessLogFilter.class);

    private static final AtomicLong COUNTER = new AtomicLong();

    @Override
    public void init(FilterConfig fConfig) {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        long duration = -System.currentTimeMillis();

        StringBuffer url = req.getRequestURL();
        String queryString = req.getQueryString();
        if (queryString != null) {
            url.append('?').append(queryString);
        }

        try (MDC.MDCCloseable closeable = MDC.putCloseable("requestId", Long.toString(COUNTER.incrementAndGet()))) {
            LOGGER.info("Serving {} '{}'", req.getMethod(), url);

            chain.doFilter(request, response);

            int status = res.getStatus();
            duration += System.currentTimeMillis();
            LOGGER.info("Served {} '{}' as {} in {} msec", req.getMethod(), url, status, duration);
        }
    }

    @Override
    public void destroy() {
    }
}
