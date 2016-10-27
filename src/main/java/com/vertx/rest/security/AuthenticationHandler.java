package com.vertx.rest.security;

import io.vertx.core.Vertx;
import io.vertx.ext.auth.AuthProvider;
import io.vertx.ext.web.handler.AuthHandler;

public interface AuthenticationHandler {
	public AuthHandler getAuthHandler(Vertx vertx);
}
