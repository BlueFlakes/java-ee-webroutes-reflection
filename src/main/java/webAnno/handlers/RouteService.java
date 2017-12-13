package webAnno.handlers;

import com.sun.net.httpserver.HttpExchange;
import webAnno.abstraction.WebRoute;
import webAnno.enums.Route;
import webAnno.enums.RouteType;
import webAnno.enums.WebMethodType;
import webAnno.models.Activity;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum RouteService {
    INSTANCE;

    private static final String servicePath = "RouteService";

    public synchronized void moveToChosenRoute(HttpExchange httpExchange, Activity userActivity) {
        Method method = RouteService.INSTANCE.findChosenRoute(userActivity);

        if (method != null) {
            try {
                method.invoke(RouteService.INSTANCE, httpExchange);

            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            throw new IllegalStateException("Couldn't find expected method to invoke.");
        }
    }

    private Method findChosenRoute(Activity userActivity) {
        try {
            Method[] methods = Class.forName(servicePath).getMethods();
            return getProperMethod(methods, userActivity);

        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Given servicePath is probably wrong, couldn't find expected class.");
        }
    }

    private Method getProperMethod(Method[] methods, Activity userActivity) {
        for (Method method : methods) {
            if (method.isAnnotationPresent(WebRoute.class)) {
                WebRoute annotation = method.getAnnotation(WebRoute.class);

                if (isCorrectRoute(annotation, userActivity.getRoute(), userActivity.getWebMethodType())) {
                    return method;
                }
            }
        }

        return null;
    }

    private boolean isCorrectRoute(WebRoute annotation, Route chosenRoute, WebMethodType webMethodType) {
        Route annotationRoute = annotation.path();
        WebMethodType annotationMethod = annotation.methodType();

        return annotationRoute.equals(chosenRoute) && annotationMethod.equals(webMethodType);
    }

    @WebRoute(path = Route.DEFAULT, routeType = RouteType.REDIRECT)
    public void defaultRedirect(HttpExchange httpExchange) throws IOException {
        Common.redirect(httpExchange, "/home");
    }

    @WebRoute(path = Route.FORM, routeType = RouteType.REDIRECT)
    public void formRedirect(HttpExchange httpExchange) throws IOException {
        Common.redirect(httpExchange, "/form");
    }

    @WebRoute(path = Route.HOME, routeType = RouteType.WRITER)
    public void homeRoute(HttpExchange httpExchange) throws IOException {

        String response = "<html><body><h1>Hello from home</h1></body></html>";
        Common.writeHttpOutputStream(httpExchange, response);
    }

    @WebRoute(path = Route.FORM, methodType = WebMethodType.POST, routeType = RouteType.WRITER)
    public void formRoute(HttpExchange httpExchange) throws IOException {

        String response = "<html><body><h1>Hello from FORM</h1></body></html>";
        Common.writeHttpOutputStream(httpExchange, response);
    }

    @WebRoute(methodType = WebMethodType.POST, path = Route.DEFAULT, routeType = RouteType.WRITER)
    public void handlePost(HttpExchange httpExchange) throws IOException {

    }
}