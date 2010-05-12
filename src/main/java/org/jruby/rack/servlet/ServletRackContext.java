/*
 * Copyright (c) 2010 Engine Yard, Inc.
 * Copyright (c) 2007-2009 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jruby.rack.servlet;

import org.jruby.rack.input.RackRewindableInput;
import org.jruby.rack.*;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.descriptor.JspConfigDescriptor;
import javax.servlet.SessionTrackingMode;
import javax.servlet.SessionCookieConfig;
import javax.servlet.FilterRegistration;
import javax.servlet.Filter;
import javax.servlet.ServletRegistration;

import org.jruby.util.SafePropertyAccessor;
import static java.lang.System.out;

/**
 *
 * @author nicksieger
 */
public class ServletRackContext implements RackContext, ServletContext {
    private ServletContext context;
    private RackLogger logger;

    private class ServletContextLogger implements RackLogger {
        public void log(String message) {
            context.log(message);
        }

        public void log(String message, Throwable ex) {
            context.log(message,ex);
        }
    }

    private static class StandardOutLogger implements RackLogger {
        public void log(String message) {
            out.println(message);
            out.flush();
        }

        public void log(String message, Throwable ex) {
            out.println(message);
            ex.printStackTrace(out);
            out.flush();
        }
    }

    public ServletRackContext(ServletContext context) {
        this.context = context;
        if (SafePropertyAccessor.getProperty("jruby.rack.logging", "servlet_context").equals("servlet_context")) {
            this.logger = new ServletContextLogger();
        } else {
            this.logger = new StandardOutLogger();
        }
        RackRewindableInput.setDefaultThreshold(
                SafePropertyAccessor.getInt("jruby.rack.request.size.threshold.bytes",
                RackRewindableInput.getDefaultThreshold()));
    }

    public String getInitParameter(String key) {
        return context.getInitParameter(key);
    }

    public void log(String message) {
        logger.log(message);

    }

    public void log(String message, Throwable ex) {
        logger.log(message, ex);
    }

    public String getRealPath(String path) {
        String realPath = context.getRealPath(path);
        if (realPath == null) { // some servers don't like getRealPath, e.g. w/o exploded war
            URL u = null;
            try {
                u = context.getResource(path);
            } catch (MalformedURLException ex) {}
            if (u != null) {
                realPath = u.getPath();
            }
        }
        return realPath;
    }

    public RackApplicationFactory getRackFactory() {
        return (RackApplicationFactory) context.getAttribute(RackServletContextListener.FACTORY_KEY);
    }

    public ServletContext getContext(String path) {
        return context.getContext(path);
    }

    public String getContextPath() {
        return context.getContextPath();
    }

    public int getMajorVersion() {
        return context.getMajorVersion();
    }

    public int getMinorVersion() {
        return context.getMinorVersion();
    }

    public String getMimeType(String file) {
        return context.getMimeType(file);
    }

    public Set getResourcePaths(String path) {
        return context.getResourcePaths(path);
    }

    public URL getResource(String path) throws MalformedURLException {
        return context.getResource(path);
    }

    public InputStream getResourceAsStream(String path) {
        return context.getResourceAsStream(path);
    }

    public RequestDispatcher getRequestDispatcher(String path) {
        return context.getRequestDispatcher(path);
    }

    public RequestDispatcher getNamedDispatcher(String name) {
        return context.getNamedDispatcher(name);
    }

    @Deprecated
    public Servlet getServlet(String name) throws ServletException {
        return context.getServlet(name);
    }

    @Deprecated
    public Enumeration getServlets() {
        return context.getServlets();
    }

    @Deprecated
    public Enumeration getServletNames() {
        return context.getServletNames();
    }

    @Deprecated
    public void log(Exception ex, String msg) {
        context.log(ex, msg);
    }

    public String getServerInfo() {
        return context.getServerInfo();
    }

    public Enumeration getInitParameterNames() {
        return context.getInitParameterNames();
    }

    public Object getAttribute(String key) {
        return context.getAttribute(key);
    }

    public Enumeration getAttributeNames() {
        return context.getAttributeNames();
    }

    public void setAttribute(String key, Object val) {
        context.setAttribute(key, val);
    }

    public void removeAttribute(String key) {
        context.removeAttribute(key);
    }

    public String getServletContextName() {
        return context.getServletContextName();
    }
    
    public void declareRoles(String... roles){
      context.declareRoles(roles);
    }
    
    public ClassLoader getClassLoader(){
      return context.getClassLoader();
    }
    
    public JspConfigDescriptor getJspConfigDescriptor() {
      return context.getJspConfigDescriptor();
    }
    
    public <T extends java.util.EventListener>T createListener(java.lang.Class<T> clazz) throws javax.servlet.ServletException{
      return context.createListener(clazz);      
    }
    
    public void addListener(java.lang.Class<? extends java.util.EventListener> listenerClass) {
      context.addListener(listenerClass);
    }
    
    public <T extends java.util.EventListener>void addListener(T t) {
      context.addListener(t);
    }
    
    public void addListener(String listener){
      context.addListener(listener);
    }
    
    public java.util.Set<SessionTrackingMode>  getEffectiveSessionTrackingModes(){
      return context.getEffectiveSessionTrackingModes();
    }
    
    public java.util.Set<SessionTrackingMode> getDefaultSessionTrackingModes(){
      return context.getDefaultSessionTrackingModes();
    }
    
    public void setSessionTrackingModes(java.util.Set<javax.servlet.SessionTrackingMode> modes){
      context.setSessionTrackingModes(modes);
    }
        
    public SessionCookieConfig getSessionCookieConfig(){
      return context.getSessionCookieConfig();
    }
    public FilterRegistration getFilterRegistration(String registration){
      return getFilterRegistration(registration);
    }
    
    public java.util.Map<java.lang.String,? extends FilterRegistration> getFilterRegistrations(){
      return context.getFilterRegistrations();
    }
    
    public <T extends Filter>T createFilter(java.lang.Class<T> clazz) throws javax.servlet.ServletException{
      return context.createFilter(clazz);
    }
    
    public FilterRegistration.Dynamic addFilter(String name, Class<? extends javax.servlet.Filter> filterClass) {
      return context.addFilter(name, filterClass);
    }
    
    public  FilterRegistration.Dynamic addFilter(String filterName, Filter filter) {
      return context.addFilter(filterName, filter);
    }
    
    public FilterRegistration.Dynamic addFilter(String filterName, String className) {
      return context.addFilter(filterName, className);
    }
    
    public ServletRegistration 	getServletRegistration(java.lang.String servletName) {
      return context.getServletRegistration(servletName);
    }
    
    public java.util.Map<java.lang.String,? extends ServletRegistration> 	getServletRegistrations() {
      return context.getServletRegistrations();
    }
    
    public <T extends Servlet> T createServlet(Class<T> clazz) throws javax.servlet.ServletException{
    	  return context.createServlet(clazz);
    }
    
    public ServletRegistration.Dynamic 	addServlet(String servletName, Class<? extends Servlet> servletClass) {
      return context.addServlet(servletName, servletClass);
    }
    
    public ServletRegistration.Dynamic 	addServlet(java.lang.String servletName, Servlet servlet) {
      return context.addServlet(servletName, servlet);
    }
    
    public  ServletRegistration.Dynamic 	addServlet(java.lang.String servletName, java.lang.String className) {
      return context.addServlet(servletName, className);
    }
    
    public boolean setInitParameter(String name, String value){
      return context.setInitParameter(name,value);
    }
    
    public int 	getEffectiveMinorVersion() {
      return context.getEffectiveMinorVersion();
    }
    
    public int 	getEffectiveMajorVersion() {
      return context.getEffectiveMajorVersion();
    }
    
}
