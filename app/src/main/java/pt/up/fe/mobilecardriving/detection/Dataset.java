package pt.up.fe.mobilecardriving.detection;

public class Dataset {
    private final static int NUM_CLASSES_KITTI = 2;
    private final static int NUM_CLASSES_GTSDB = 5;

    public enum Name {
        KITTI,
        GTSDB
    }

    public static int getNumClasses() {
        return NUM_CLASSES_KITTI + NUM_CLASSES_GTSDB;
    }

    public static int getNumClassesGtsdb() {
        return NUM_CLASSES_GTSDB;
    }

    public static int getNumClassesKitti() {
        return NUM_CLASSES_KITTI;
    }
}
