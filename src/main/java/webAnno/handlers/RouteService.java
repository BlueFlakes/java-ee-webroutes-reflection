package webAnno.handlers;

import com.sun.net.httpserver.HttpExchange;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
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
        Common.redirect(httpExchange, Route.HOME.getPath());
    }

    @Redirect(route = Route.FORM)
    public void formRedirect(HttpExchange httpExchange) throws IOException {
        Common.redirect(httpExchange, Route.FORM.getPath());
    }

    @Redirect(route = Route.CONTACT)
    public void contactRedirect(HttpExchange httpExchange) throws IOException {
        Common.redirect(httpExchange, Route.CONTACT.getPath());
    }

    @WebRoute(path = Route.CONTACT)
    public void contactRoute(HttpExchange httpExchange) throws IOException {
        String color = "pink";

        Common.writeHttpOutputStream(httpExchange, getResponse(color));
    }

    @WebRoute(path = Route.HOME)
    public void homeRoute(HttpExchange httpExchange) throws IOException {
        String color = "aliceblue";

        Common.writeHttpOutputStream(httpExchange, getResponse(color));
    }

    @WebRoute(path = Route.FORM)
    public void formRoute(HttpExchange httpExchange) throws IOException {

        String color = "yellowgreen";
        Common.writeHttpOutputStream(httpExchange, getResponse(color));
    }

    private String getResponse(String color) {
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/MainTemplate.twig");
        JtwigModel model = JtwigModel.newModel();

        model.with("color", color);

        return template.render(model);
    }
}