package com.atex.ps.workoliday.servlet;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.FinderException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.polopoly.cm.client.CMException;

@SuppressWarnings("serial")
public class ExternalCacheUpdaterServlet extends HttpServlet {

    private static final String UPDATE_INTERVAL_SECONDS = "updateIntervalSeconds";
    private static final String CLASS = ExternalCacheUpdaterServlet.class
            .getName();
    private static Logger logger = Logger.getLogger(CLASS);
    private String username;
    private String password;
    private Timer timer;
    private int seconds;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        // Get the value of an initialization parameter
        String updateIntervalSeconds = config
                .getInitParameter(UPDATE_INTERVAL_SECONDS);
        this.seconds = Integer.valueOf(updateIntervalSeconds);
        this.username = config.getInitParameter("username");
        this.password = config.getInitParameter("password");
    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        boolean running = (timer != null) ? true : false;

        String work = request.getParameter("action");
        if ("start".equals(work)) {
            if (timer != null) {
                cancelTimer();
            }
            // get a calendar to initialize the start time
            Calendar calendar = Calendar.getInstance();
            Date startTime = calendar.getTime();
            timer = new Timer("ExternalCacheUpdaterTimer");
            ExternalCacheTimerTask externalCacheTimerTask = null;
            try {
                externalCacheTimerTask = new ExternalCacheTimerTask(
                        getServletContext(), seconds, username, password);
                timer.schedule(externalCacheTimerTask, startTime,
                        seconds * 1000L);
                running = true;
            } catch (CMException e) {
                logger.logp(Level.WARNING, CLASS, "doGet",
                        "Failed to initialize the ExternalCacheTimerTask", e);
                running = false;
            } catch (FinderException e) {
                logger.logp(Level.WARNING, CLASS, "doGet",
                        "Failed to initialize the ExternalCacheTimerTask", e);
                running = false;
            }

            logger.info("Workoliday is started...");
        } else if ("stop".equals(work)) {
            cancelTimer();
            running = false;
            logger.info("Workoliday is stopped...");
        }

        request.setAttribute("status", (running) ? "online" : "offline");
        RequestDispatcher dispatcher = request
                .getRequestDispatcher("/status.jsp");
        if (dispatcher != null)
            dispatcher.forward(request, response);
    }

    private void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        cancelTimer();
    }
}
