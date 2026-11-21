package app.template.extension.extension;

@SuppressWarnings("unused")
public class ExamplePatch {

    /**
     * Returns true to spoof a purchased/premium state.
     * Called by patches that inject smali to invoke this method.
     */
    public static boolean isPurchased() {
        return true;
    }

    /**
     * Returns Integer.MAX_VALUE to remove any photo limit check.
     */
    public static int getUnlimitedCount() {
        return Integer.MAX_VALUE;
    }
}
