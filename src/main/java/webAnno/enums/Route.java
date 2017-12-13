package webAnno.enums;

public enum Route {
    HOME(WebMethodType.DEFAULT),
    FORM(WebMethodType.POST),
    DEFAULT(WebMethodType.DEFAULT);

    private WebMethodType webMethodType;

    Route(WebMethodType webMethodType) {
        this.webMethodType = webMethodType;
    }

    public WebMethodType getWebMethodType() {
        return webMethodType;
    }
}
