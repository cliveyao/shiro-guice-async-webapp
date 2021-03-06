package com.pampanet.sample.rest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;

import com.google.inject.Inject;

import org.jboss.resteasy.plugins.guice.RequestScoped;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

/**
 * Sample Secured RESTful Web Service<br>
 * 
 * The constructor has Guice injections to enable also Shiro AOP annotations
 * 
 * @see com.pampanet.sample.servlet.modules.BootstrapServletModule
 * @see com.pampanet.sample.shiro.modules.ShiroAnnotationsModule
 * @author pablo.biagioli
 *
 */
@RequestScoped
@Path("/rest/stuff")
public class SampleSecuredRESTWebService {

	private final XLogger logger = XLoggerFactory.getXLogger(getClass());
	
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	
	@Inject
	public SampleSecuredRESTWebService(HttpServletRequest request, HttpServletResponse response) {
		logger.entry();
		this.request = request;
		this.response = response;
		logger.exit();
	}
	
	@GET
	@Path("/allowed")
	@RequiresPermissions("lightsaber:allowed")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	public Response sayHelloToUser(){
		logger.entry(getRequest(), getResponse());
		Response result = Response.ok("Hello "+SecurityUtils.getSubject().getPrincipal()+"!").build();
		logger.exit(result);
		return result;
	}
	
	@GET
	@Path("/forbidden")
	@RequiresPermissions("forbiddenForAllExceptRoot")
	@Produces(MediaType.APPLICATION_JSON)
	public Response forbiddenToAll(){
		logger.entry(getRequest(), getResponse());
		Response result = Response.ok("Got "+SecurityUtils.getSubject().getPrincipal()+"!").build();
		logger.exit(result);
		return result;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
	
}
