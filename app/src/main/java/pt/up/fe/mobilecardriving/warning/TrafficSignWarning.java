package pt.up.fe.mobilecardriving.warning;

public class TrafficSignWarning extends Warning {
    TrafficSign sign;

    public TrafficSignWarning(TrafficSign sign, int priority){
        super(sign.toString(), priority);
        this.sign = sign;
    }

    public TrafficSign getSign() {
        return sign;
    }
}
