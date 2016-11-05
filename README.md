# vertx-rest

This is a light weight REST framework for Vertx.  REST API can be implemented just by annotating the methods. 
Following are some key features of the framework

1. Annotation for HTTP methods  
2. Authentication can be enabled on a service just by annotating the method @AuthRequired  
3. Autherization can be enabled by using @Permission anotation on method or Service level.  
4. Flexible Authentication and Autherization mechanisim. You can use all Authetication and Autherization mechanisim provided by
  vertx, and also implement customized Authentication and Autherization Provider which can be configured during application bootstrap   
5. We can implement granular access controll, by providing what permission is required to access a particular API and HTTP method.   
6. All permission configured will be available as out of the box REST API, which makes in User management easier. 
If your application requires create and manage user with different roles, by using this REST API, you can get all the required roles
and assign them to a role. 
