package nz.co.cjc.base.framework.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Chris Cooper on 4/05/16.
 * <p/>
 * Collection of string utility methods.
 */
public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Given a string, determine if it is null or empty.
     *
     * @param source string to evaluate.
     * @return true if the string is null or empty.
     */
    public static boolean isEmpty(@Nullable String source) {
        return source == null || source.length() == 0;
    }

    /**
     * Given a string, return an empty string if it is null.
     *
     * @param source string to evaluate for null.
     * @return an empty string if source string is null, or the original
     * source string.
     */
    @NonNull
    public static String emptyIfNull(@Nullable String source) {
        return source == null ? "" : source;
    }

    /**
     * Appends the correct suffix onto the date number
     *
     * @param dayOfMonth The day of the month number to append the suffix to
     * @return The correct suffix string
     */
    @NonNull
    public static String getDateSuffix(@NonNull String dayOfMonth) {

        String suffix = "";

        if (dayOfMonth.equals("11") || dayOfMonth.equals("12") || dayOfMonth.equals("13"))
            return "th";

        if (dayOfMonth.endsWith("1")) suffix = "st";
        if (dayOfMonth.endsWith("2")) suffix = "nd";
        if (dayOfMonth.endsWith("3")) suffix = "rd";
        if (dayOfMonth.endsWith("0") || dayOfMonth.endsWith("4") || dayOfMonth.endsWith("5") || dayOfMonth.endsWith("6") || dayOfMonth.endsWith("7") || dayOfMonth.endsWith("8") || dayOfMonth.endsWith("9"))
            suffix = "th";

        return suffix;

    }
}
