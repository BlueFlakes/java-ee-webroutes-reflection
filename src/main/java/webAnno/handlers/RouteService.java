package webAnno.handlers;

import com.sun.net.httpserver.HttpExchange;
import webAnno.abstraction.Redirect;
import webAnno.abstraction.WebRoute;
import webAnno.enums.Route;
import webAnno.enums.WebMethodType;
import webAnno.models.Activity;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiPredicate;

public class RouteService<T extends Annotation, U> {
    private static final String servicePath = "webAnno.handlers.RouteService";

    void moveToChosenRoute(HttpExchange httpExchange,
                           U userActivity,
                           Class<T> annotationClass,
                           BiPredicate<T, U> p) {

        Method method = this.findChosenRoute(userActivity, annotationClass, p);

        if (method != null) {
            try {
                method.invoke(this, httpExchange);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("Couldn't find expected method to invoke.");
        }
    }

    private Method findChosenRoute(U userActivity, Class<T> annotationClass, BiPredicate<T, U> p) {
        try {
            Method[] methods = Class.forName(servicePath).getMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(annotationClass)) {
                    T annotation = method.getAnnotation(annotationClass);
                    
                    if (p.test(annotation, userActivity)) {
                        return method;
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Given servicePath is probably wrong, couldn't find expected class.");
        }

        return null;
    }

    @Redirect(route = Route.DEFAULT)
    public void defaultRedirect(HttpExchange httpExchange) throws IOException {
        Common.redirect(httpExchange, "/home");
    }

    @Redirect(route = Route.FORM)
    public void formRedirect(HttpExchange httpExchange) throws IOException {
        Common.redirect(httpExchange, "/form");
    }

    @WebRoute(path = Route.HOME)
    public void homeRoute(HttpExchange httpExchange) throws IOException {

        String response = "<html><body><h1>Hello from home</h1></body></html>";
        Common.writeHttpOutputStream(httpExchange, response);
    }

    @WebRoute(path = Route.FORM, methodType = WebMethodType.POST)
    public void formRoute(HttpExchange httpExchange) throws IOException {

        String response = "<html><body><h1>Hello from FORM</h1></body></html>";
        Common.writeHttpOutputStream(httpExchange, response);
    }

    @WebRoute(methodType = WebMethodType.POST, path = Route.DEFAULT)
    public void handlePost(HttpExchange httpExchange) throws IOException {

    }
}