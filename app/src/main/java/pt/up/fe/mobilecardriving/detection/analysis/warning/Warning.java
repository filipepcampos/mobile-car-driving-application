package pt.up.fe.mobilecardriving.detection.analysis.warning;

public abstract class Warning {
    private final String message;
    // TODO: private Image icon;

    public Warning(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
