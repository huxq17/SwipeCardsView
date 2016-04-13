package okhttp3.internal;

/**
 * Created by huxq17 on 2015/12/17.
 */
public final class Version {
    public static String userAgent() {
        return "okhttp/${project.version}";
    }

    private Version() {
    }
}