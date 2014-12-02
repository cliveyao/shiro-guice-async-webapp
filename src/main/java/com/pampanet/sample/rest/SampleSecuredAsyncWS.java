package com.pampanet.sample.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.jboss.resteasy.plugins.guice.RequestScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

@RequestScoped
@Path("/rest/async")
public class SampleSecuredAsyncWS {
	
	private Logger logger = LoggerFactory.getLogger(SampleSecuredAsyncWS.class);
	
	@Inject
	@Named("hello.world.string")
	private String helloWorldString;
	
	@GET
	@Path("basic")
	@Produces(MediaType.APPLICATION_JSON)
	public void getBasic(final @Suspended AsyncResponse response)
			throws Exception {
		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					response.resume(Response.ok(helloWorldString).type(MediaType.APPLICATION_JSON).build());
				} catch (Exception e) {
					logger.error(e.getMessage(), e);
				}
			}
		};
		t.start();
	}
	
	@GET
	@Path("/allowed")
	@RequiresPermissions("lightsaber:allowed")
	@RequiresAuthentication
	@Produces(MediaType.APPLICATION_JSON)
	public void sayHelloToUser(final @Suspended AsyncResponse response) throws Exception{
		//return Response.ok("Hello "+SecurityUtils.getSubject().getPrincipal()+"!").build();
		Thread t = new Thread(){
			@Override
			public void run() {
				super.run();
				response.resume(Response.ok("Hello "+SecurityUtils.getSubject().getPrincipal()+"!").build());
			}
		};
		t.start();
	}
	
	@GET
	@Path("/forbidden")
	@RequiresPermissions("forbiddenForAllExceptRoot")
	@Produces(MediaType.APPLICATION_JSON)
	public void forbiddenToAll(final @Suspended AsyncResponse response) throws Exception{
		//return Response.ok("Got Root!").build();
		Thread t = new Thread(){
			@Override
			public void run() {
				super.run();
				response.resume(Response.ok("Got "+SecurityUtils.getSubject().getPrincipal()+"!").build());
			}
		};
		t.start();
	}
	
}
