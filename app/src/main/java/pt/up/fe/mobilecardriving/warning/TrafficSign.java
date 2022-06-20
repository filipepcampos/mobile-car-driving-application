package pt.up.fe.mobilecardriving.warning;

public enum TrafficSign {
    STOP("Stop"),
    GIVE_WAY("Give way"),
    PROHIBITED("Prohibited way"),
    PROHIBITED_OVERTAKING("Overtaking is prohibited"),
    ALLOWED_OVERTAKING("Overtaking is allowed");

    private final String message;

    TrafficSign(String message){
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
