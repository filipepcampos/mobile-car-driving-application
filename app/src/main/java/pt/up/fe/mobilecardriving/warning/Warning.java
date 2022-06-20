package pt.up.fe.mobilecardriving.warning;

public abstract class Warning implements Comparable<Warning> {
    private final String message;
    private final int priority;

    public Warning(String message, int priority) {
        this.message = message;
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public String getMessage() {
        return this.message;
    }

    public int compareTo(Warning warning){
        return this.priority - warning.getPriority();
    }
}
