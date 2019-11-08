# spring-metrics-grabber
spring-metrics-grabber publish metrics with value on single endpoint '/metricx'

#### Building from Source
```
$ ./gradlew clean build
```
#### How to use
* Add dependency in you application from local repository
```groovy
compile("org.springframework.boot:spring-metrics-grabber:1.0.0-SNAPSHOT");
compile("org.springframework.boot:spring-boot-starter-actuator");
```

* Add `MetricxEndpoint.class` to `@SpringBootApplication` scan path
```java
import org.springframework.metricx.controller.MetricxEndpoint;

@SpringBootApplication(scanBasePackageClasses = { MetricxEndpoint.class, YourSpringBootApplication.class })
public class YourSpringBootApplication {
	public static void main(String[] args) {
		new SpringApplication(YourSpringBootApplication.class).run(args);
	}
}
```

* Alternatively provide a `@Bean` of type `MetricxEndpoint` in your application configuration

* All Metrics will be published on '/metricx' endpoint with name and value both

* Pattern search is also supported
e.g. '/metricx/jvm.*'