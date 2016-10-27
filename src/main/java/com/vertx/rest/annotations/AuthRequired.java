package com.vertx.rest.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * If a service is annotated as @AuthRequired then, Authentication will be enabled for that route.
 * By default this framework will use JWT authentication, authentication mechanism can be changed to by registering custom
 * Authentication handler in Application class
 * 
 * @author Huthesh
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AuthRequired {

}
