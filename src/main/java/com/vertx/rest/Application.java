package com.vertx.rest;

import java.util.Set;

public interface Application {
	
	default String baseUrl(){
		return "/";
	}
	
	public Set<Class<?>> getServiceClasses();
}
