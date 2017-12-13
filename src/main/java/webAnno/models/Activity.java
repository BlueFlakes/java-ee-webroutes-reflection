package webAnno.models;

import lombok.Getter;
import webAnno.enums.Route;
import webAnno.enums.WebMethodType;

@Getter
public class Activity {
    private Route route;
    private WebMethodType webMethodType;

    public Activity(Route route, WebMethodType webMethodType) {
        this.route = route;
        this.webMethodType = webMethodType;
    }
}
