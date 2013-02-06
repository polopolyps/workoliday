package com.atex.ps.workoliday.util;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class WorkolidayMBeanUtil {

    public static final Logger logger = Logger
            .getLogger(WorkolidayMBeanUtil.class.getName());

    public static void registerMBeanIfNotAlreadyRegistered(Object mbean,
            String hostName, String application, String detailLevel,
            String component, String name) {
        String completeName = getCompleteName(hostName, application,
                detailLevel, component, name);
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            if (!mbeanServer.isRegistered(new ObjectName(completeName))) {
                mbeanServer.registerMBean(mbean, new ObjectName(completeName));
            }
        } catch (InstanceAlreadyExistsException e) {
            logger.log(Level.WARNING, "Was not able to register MBean "
                    + completeName, e);
        } catch (MBeanRegistrationException e) {
            logger.log(Level.WARNING, "Was not able to register MBean "
                    + completeName, e);
        } catch (NotCompliantMBeanException e) {
            logger.log(Level.WARNING, "Was not able to create MBean "
                    + completeName, e);
        } catch (MalformedObjectNameException e) {
            logger.log(Level.WARNING, "Was not able to create MBean "
                    + completeName, e);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Was not able to create MBean "
                    + completeName, e);
        }
    }

    public static void unregisterMBean(String hostName, String application,
            String detailLevel, String component, String name) {
        String completeName = getCompleteName(hostName, application,
                detailLevel, component, name);
        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName objectName = new ObjectName(completeName);
            if (mbeanServer.isRegistered(objectName)) {
                mbeanServer.unregisterMBean(objectName);
            }
        } catch (InstanceNotFoundException e) {
            logger.log(Level.WARNING, "Was not able to unregister MBean "
                    + completeName, e);
        } catch (MBeanRegistrationException e) {
            logger.log(Level.WARNING, "Was not able to unregister MBean "
                    + completeName, e);
        } catch (MalformedObjectNameException e) {
            logger.log(Level.WARNING, "Was not able to create MBean "
                    + completeName, e);
        } catch (NullPointerException e) {
            logger.log(Level.WARNING, "Was not able to create MBean "
                    + completeName, e);
        }
    }

    private static String getCompleteName(String hostName, String application,
            String detailLevel, String component, String name) {
        StringBuilder ret = new StringBuilder();
        ret.append("com.polopoly:host=");
        ret.append(hostName);
        ret.append(",application=").append(application);
        ret.append(",module=").append(component);
        ret.append(",detailLevel=").append(detailLevel);
        ret.append(",name=").append(name);
        return ret.toString();
    }
}
