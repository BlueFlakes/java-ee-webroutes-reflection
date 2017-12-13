package webAnno.models;

import lombok.Getter;
import webAnno.enums.Route;
import webAnno.enums.WebMethodType;

import java.lang.annotation.Annotation;
import java.util.function.BiPredicate;

@Getter
public class Activity<T extends Annotation, U> {
    private DataConsumer dataConsumer;
    private Class<T> annotatedClass;
    private BiPredicate<T, U> predicate;

    public Activity(Route route, WebMethodType webMethodType, Class<T> annotatedClass, BiPredicate<T, U> predicate) {
        this.dataConsumer = new DataConsumer(route, webMethodType);
        this.annotatedClass = annotatedClass;
        this.predicate = predicate;
    }

    @Getter
    public class DataConsumer {
        Route route;
        WebMethodType webMethodType;

        DataConsumer(Route route, WebMethodType webMethodType) {
            this.route = route;
            this.webMethodType = webMethodType;
        }
    }
}
