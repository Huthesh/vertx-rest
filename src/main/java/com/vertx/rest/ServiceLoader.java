/**
 * 
 */
package com.vertx.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

import com.vertx.rest.annotations.AuthRequired;
import com.vertx.rest.annotations.Consumes;
import com.vertx.rest.annotations.Delete;
import com.vertx.rest.annotations.Get;
import com.vertx.rest.annotations.Path;
import com.vertx.rest.annotations.Post;
import com.vertx.rest.annotations.Produces;
import com.vertx.rest.annotations.Put;
import com.vertx.rest.constants.ContentType;
import com.vertx.rest.security.AuthenticationHandler;
import com.vertx.rest.security.impl.JWTAuthenticationHandler;

import io.vertx.core.Vertx;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;

/**
 * 
 * This class is the entry point for loading all services. Its singleton class,
 * and loads all the services registered in {@link Application}
 * 
 * @author Huthesh
 *
 */
public class ServiceLoader {
	
	private static Logger log=LoggerFactory.getLogger(ServiceLoader.class);
	
	private Vertx vertx;
	private AuthenticationHandler authenticationHandler=null;
	private static ServiceLoader serviceLoader=null;
	
	private ServiceLoader(Vertx vertx){
		this.vertx=vertx;
	}
	
	/**
	 * Synchronized method to get the Singleton instance of ServiceLoader
	 * 
	 * @param vertx
	 * @return
	 */
	public static synchronized  ServiceLoader getInstance(Vertx vertx){
		if(serviceLoader==null){
			serviceLoader=new ServiceLoader(vertx);
			log.debug("ServiceLoader is initialized");
		}
		return serviceLoader;
	}
	
	/**
	 * Register AuthProvider, By default JWTAuthProvider will be used, if any service is annotated as @AuthRequired
	 * @param authenticationProvider
	 * @return
	 */
	public synchronized ServiceLoader setAuthProvider(AuthenticationHandler authenticationProvider){
		this.authenticationHandler=authenticationProvider;
		return this;
	}
	
	/**
	 * This method loads the all the REST Service class configured in Application class
	 * 
	 * @param application
	 */
	public Router loadServices(Application application){
		log.debug("Loading all service");
		log.debug("Application base url is "+application.baseUrl());
		Set<Class<?>> serviceClasses=application.getServiceClasses();
		Router finalRouter=Router.router(vertx);
		serviceClasses.parallelStream().forEach(serviceClass->{
			Router router=loadRouters(serviceClass);
			finalRouter.mountSubRouter(application.baseUrl(), router);
			log.debug("Loaded service class "+serviceClass);
		});
		return finalRouter;
	}

	/**
	 * Iterates through all methods in the given service class and creates the vertx Routes based on the annotations
	 * 
	 * @param serviceClass
	 * @return
	 */
	private Router loadRouters(Class<?> serviceClass){
		Router router=Router.router(vertx);
		try {
			Object serviceObject=Class.forName(serviceClass.getName()).newInstance();
			String baseUrl=getBaseUrl(serviceClass);
			log.debug("Base URL for "+serviceClass+" is "+baseUrl);
			Method[] declaredMethods = serviceClass.getDeclaredMethods();
			for(int i=0;i<declaredMethods.length;i++){
				router.mountSubRouter(baseUrl, processMethod(declaredMethods[i],serviceObject));
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			log.error("Error while loading the routers",e);
		}
		return router;
	}
	/**
	 * If the ServiceClass is annotated with @Path, the path specified will be considered as base URL (Context path)
	 * for the all service defined in the service class
	 * 
	 * @param serviceClass
	 * @return
	 */
	private String getBaseUrl(Class<?> serviceClass){
		Path servicePath=serviceClass.getAnnotation(Path.class);
		String baseUrl=null;
		if(servicePath==null){
			baseUrl="/";
		}else{
			baseUrl=servicePath.value();
		}
		return baseUrl;
	}
	
	/**
	 * If a service method is annotated with @AuthRequired then, authentication needs to be enabled for the services.
	 * This method check whether given method has the @AuthRequired annotation
	 * @param method
	 * @return
	 */
	private boolean isAuthRequired(Method method){
		if(method.isAnnotationPresent(AuthRequired.class)){
			return true;
		}
		return false;
	}
	
	/**
	 * Process each service methods
	 * 
	 * @param method
	 * @param serviceObject
	 * @return
	 */
	private Router processMethod(Method method,Object serviceObject){
		Router router=Router.router(vertx);
		Annotation[] declaredAnnotations = method.getDeclaredAnnotations();
		
		if(isAuthRequired(method)){
			if(authenticationHandler==null){
				log.debug("Authentication handler is not set.So, using default JWTAuthenticationHandler");
				authenticationHandler=new JWTAuthenticationHandler();
			}
			
			log.debug("Using "+authenticationHandler.getClass().getName()+" as authentication handler");
			processHttpMethod(router, declaredAnnotations).handler(authenticationHandler.getAuthHandler(vertx));
		}
		
		Route route=processHttpMethod(router, declaredAnnotations);
		route.consumes(getConsumes(method).getContentType())
			.produces(getProduces(method).getContentType());

		route.handler(routingContext->{
				try {
					method.setAccessible(true);
					method.invoke(serviceObject, routingContext);
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			});
		return router;
	}
	
	/**
	 * By default all service consumes ContentType.TEXT_PLAIN. Can be changed using @Consumes annotation
	 * 
	 * @param method
	 * @return
	 */
	private ContentType getConsumes(Method method){
		Consumes consumes=method.getDeclaredAnnotation(Consumes.class);
		if(consumes==null){
			return ContentType.TEXT_PLAIN;
		}
		return consumes.value();
	}
	/**
	 * By default all service produces  ContentType.TEXT_PLAIN. Can be changed using @Produces annotation
	 * 
	 * @param method
	 * @return
	 */
	private ContentType getProduces(Method method){
		Produces produce=method.getDeclaredAnnotation(Produces.class);
		if(produce==null){
			return ContentType.TEXT_PLAIN;
		}
		return produce.value();
	}
	
	/**
	 * Processes each method annotation and 
	 * @param router
	 * @param declaredAnnotations
	 * @return
	 */
	private Route processHttpMethod(Router router,Annotation[] declaredAnnotations){
		for(int i=0;i<declaredAnnotations.length;i++){
			Annotation annotation=declaredAnnotations[i];
			String annotationName=annotation.annotationType().getSimpleName();
			switch (annotationName) {		
			case "Post":
				return router.post(Post.class.cast(annotation).value());				
			case "Get":
				return router.get(Get.class.cast(annotation).value());
			case "Put":
				return router.put(Put.class.cast(annotation).value());
			case "Delete":
				return router.delete(Delete.class.cast(annotation).value());
			}
		}	
		return router.get("/");
	}
}
