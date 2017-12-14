package webAnno.enums;

import lombok.Getter;

@Getter
public enum Route {
    HOME(WebMethodType.DEFAULT, "/home"),
    FORM(WebMethodType.DEFAULT, "/form"),
    CONTACT(WebMethodType.DEFAULT, "/contact"),
    DEFAULT(WebMethodType.DEFAULT, "/home");

    private WebMethodType webMethodType;
    private String path;

    Route(WebMethodType webMethodType, String path) {
        this.webMethodType = webMethodType;
        this.path = path;
    }
}
