# Cotton
Cotton is a Vaadin extension destined for the ultimate of developer convenience.

## Why Cotton?

Cotton is meant to ease the start of Vaadin projects as well as offering concepts to control the growth of such applications over time.
 
It tackles these requirements by providing standardized solutions for complex problems most Vaadin application tend to require at one point or another. For example, it offers functionality like:
- Dependency Injection
- **M**odel/**V**iew/**P**resenter separation
- Fluent Component Building
- Advanced Data Binding
- View Access Restriction

... and many more.

Nevertheless, Cotton does not enforce the use of any of this functionality, but rather allows to utilize Vaadin natively whenever favored or necessary.

## In which environments can Cotton be used?

Like in native Vaadin, the base of all Cotton applications is the Servlet-API.
 
 As a result, Cotton can be used in plain servlet environments as well as in Hura Web/Weblaunch applications or even in a foreign environment like Spring/Spring Boot.
 
 The [Cotton-Flow Demo](https://github.com/MantledIllusion/cotton-flow-demo)'s chapter 1 provides five basic setups for Cotton applications which differ in functionality, weight and deployment style.
 
 The following Table provides a quick summary over the differences when building and deploying that chapter's demo applications:
 
 | | [Cotton + Hura Web](https://github.com/MantledIllusion/cotton-flow-demo/tree/01/a/hura_web_setup) | [Cotton + Hura WebLaunch](https://github.com/MantledIllusion/cotton-flow-demo/tree/01/b/hura_weblaunch_setup) | [Cotton +  Spring WebMVC](https://github.com/MantledIllusion/cotton-flow-demo/tree/01/c/spring_webmvc_setup) | [Cotton + Spring Boot](https://github.com/MantledIllusion/cotton-flow-demo/tree/01/d/spring_boot_setup) | [Cotton + Native Servlet-API](https://github.com/MantledIllusion/cotton-flow-demo/tree/01/e/native_setup) |
 | --- | :---: | :---: | :---: | :---: | :---: |
 | Packaging | .WAR | .JAR | .WAR | .JAR | .WAR |
 | Webserver | Tomcat 9 | Undertow 2 (embedded) | Tomcat 9 | Tomcat 9 (embedded) | Tomcat 9 |
 | File Size | 19.126 kb | 24.827 kb | 24.972 kb | 36.680 kb | 19.114 kb |
 | Startup Time | 5.088 ms | 592 ms | 5.651 ms | 3.155 ms | 3.318 ms |
 | Environment Injection | &#10003; | &#10003; | &#10003; | &#10003; | X |
 | Massive Framework | X | X | &#10003; | &#10003; | X |
 
Obviously, Hura Web and WebLaunche's low profile provide a great performance at a very low footprint at the cost of lacking the huge framework support Spring offers.

For example, when in need for calling a REST web service, a Hura Web environment will bring the necessity of adding a REST framework like [Retrofit](http://square.github.io/retrofit/), while Spring already provides its RestTemplate for the job.

**In the end, the Vaadin application's target architecture defines which way to go.**

If the frontend is meant to be rather lightweight, either without any heavy computation or with all of the complexity done in a middleware or backend, a Hura Web environment can be a provide an awesome base.

If frontend and backend are combined however and the application needs to call several services, grab data from a database and needs to publish events made by users to a JMS queue, a Spring setup might be right way to go.

Since main environment beans are delivered to the Hura injected views from the CottonServlet either way, it is no problem to start off with a lightweight Hura Web environment at the start and later switch to a heavier Spring environment later on.