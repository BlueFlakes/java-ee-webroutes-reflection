package webAnno.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import webAnno.enums.EnumUtils;
import webAnno.enums.Route;
import webAnno.enums.WebMethodType;
import webAnno.models.Activity;

import java.io.IOException;
import java.net.HttpCookie;
import java.util.*;

public class MainRoute implements HttpHandler {
    private Map<UUID, String> userPath = new HashMap<>();
    private static final String defaultRoute = "/home";

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            handleMainRoute(httpExchange);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMainRoute(HttpExchange httpExchange) throws IOException {
        handleCookieMechanism(httpExchange);
        Activity activity = getActivityFromUri(httpExchange);

        System.out.println(httpExchange.getRequestURI());

        RouteService.INSTANCE.moveToChosenRoute(httpExchange, activity);
    }

    private Activity getActivityFromUri(HttpExchange httpExchange) {
        String uriPath = httpExchange.getRequestURI().getPath();

        String[] directories = Arrays.stream(uriPath.split("/"))
                                     .filter(p -> !p.isEmpty())
                                     .toArray(String[]::new);

        if (directories.length >= 1) {
            UUID userUUID = getUUIDFromCookie(httpExchange);
            String chosenRoute = directories[0];
            Route route = EnumUtils.getValue(Route.class, chosenRoute.toUpperCase());

            if (userUUID != null) {
                String lastUserPath = getPathFromUserCookie(httpExchange, userUUID);

                if (lastUserPath != null && !lastUserPath.equals(uriPath)) {
                    this.userPath.replace(userUUID, uriPath);
                    return new Activity(route, WebMethodType.DEFAULT);
                }
            }

            return new Activity(route, route.getWebMethodType());
        }

        return new Activity(Route.DEFAULT, WebMethodType.DEFAULT);
    }

    private String getPathFromUserCookie(HttpExchange httpExchange, UUID userUUID) {
        String cookies = httpExchange.getRequestHeaders().getFirst("Cookie");

        if (cookies != null && userUUID != null && this.userPath.containsKey(userUUID)) {
            return this.userPath.get(userUUID);
        }

        return null;
    }

    private UUID getUUIDFromCookie(HttpExchange httpExchange) {
        String cookies = httpExchange.getRequestHeaders().getFirst("Cookie");

        if (cookies != null) {
            String[] separatedCookies = cookies.split("; ");

            for (String cookie : separatedCookies) {
                HttpCookie foundCookie = HttpCookie.parse(cookie).get(0);
                if (foundCookie.getName().equals("UUID")) {
                    return UUID.fromString(foundCookie.getValue());
                }
            }
        }

        return null;
    }

    private void handleCookieMechanism(HttpExchange httpExchange) throws IOException {
        if (getUUIDFromCookie(httpExchange) == null) {
            UUID uuid = UUID.randomUUID();
            setCookie(httpExchange, uuid);
            this.userPath.put(uuid, httpExchange.getRequestURI()
                                                .getPath());
        }
    }

    private void setCookie(HttpExchange httpExchange, UUID uuid) throws IOException {
        httpExchange.getResponseHeaders().add("Set-Cookie", generateCookieAttribute("UUID", uuid.toString()));
    }

    private String generateCookieAttribute(String name, String value) {
        return name + "=" + value + ";";
    }
}
