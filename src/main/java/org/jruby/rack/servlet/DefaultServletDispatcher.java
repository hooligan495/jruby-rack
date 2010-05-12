/*
 * Copyright (c) 2010 Engine Yard, Inc.
 * Copyright (c) 2007-2009 Sun Microsystems, Inc.
 * This source code is available under the MIT license.
 * See the file LICENSE.txt for details.
 */

package org.jruby.rack.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.AsyncContext;

import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.rack.RackApplication;
import org.jruby.rack.RackApplicationFactory;
import org.jruby.rack.RackContext;
import org.jruby.rack.RackEnvironment;
import org.jruby.rack.RackResponse;



/**
 *
 * @author nicksieger
 */
public class DefaultServletDispatcher implements ServletDispatcher {
    private RackContext context;

    public DefaultServletDispatcher(RackContext servletContext) {
        this.context = servletContext;
    }
    
    public void process(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        final RackApplicationFactory rackFactory = context.getRackFactory();
        RackApplication app = null;
        try {
            app = rackFactory.getApplication();            
            RackEnvironment env = new ServletRackEnvironment(request);
            RackResponse rack_resp = app.call(env);
            Boolean isAsync = (Boolean)env.getAttribute("async");
            System.out.println("isAsync " + isAsync);
            if ((isAsync == null) || (isAsync.booleanValue() == false)) {
              rack_resp.respond(new ServletRackResponseEnvironment(response));
            } else {
              // mark the request asynchronous and properly clean up.
              //if (request.isAsyncSupported() == true){
                 AsyncContext ctx = request.startAsync();
              //} else {
                System.out.println("Here3");
                //we were asked to do async processing when we don't support it.
                //throw new Exception("Request is within the scope of a filter or servlet that does not support asynchronous operations");
              //}              
            }
        } catch (Exception re) {
            handleException(re, rackFactory, request, response);
        } finally {
            if (app != null) {
                rackFactory.finishedWithApplication(app);
            }
        }
    }    

    private void handleException(Exception re, RackApplicationFactory rackFactory,
            HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (response.isCommitted()) {
            context.log("Error: Couldn't handle error: response committed", re);
            return;
        }
        response.reset();
        context.log("Application Error", re);

        try {
            RackApplication errorApp = rackFactory.getErrorApplication();
            request.setAttribute(RackEnvironment.EXCEPTION, re);
            errorApp.call(new ServletRackEnvironment(request)).respond(new ServletRackResponseEnvironment(response));
        } catch (Exception e) {
            context.log("Error: Couldn't handle error", e);
            response.sendError(500);
        }
    }
}
