package webAnno.abstraction;

import webAnno.enums.Route;
import webAnno.enums.RouteType;
import webAnno.enums.WebMethodType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

public @interface WebRoute {
    RouteType routeType();
    WebMethodType methodType() default WebMethodType.DEFAULT;
    Route path() default Route.DEFAULT;
}
