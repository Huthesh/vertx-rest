package com.vertx.rest.security.impl;

import java.util.Objects;

import com.vertx.rest.security.AuthenticationHandler;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.web.handler.AuthHandler;
import io.vertx.ext.web.handler.JWTAuthHandler;


public class JWTAuthenticationHandler implements AuthenticationHandler{

	private static JsonObject config = new JsonObject().put("keyStore", new JsonObject()
		    .put("path", "/usr/verticles/keystore.jceks")
		    .put("type", "jceks")
		    .put("password", "secret"));

	@Override
	public AuthHandler getAuthHandler(Vertx vertx) {
		Objects.requireNonNull(vertx);
		return JWTAuthHandler.create(JWTAuth.create(vertx, config));
	}
}
