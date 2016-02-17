package software.oi.engelfax.activity;

/**
 * Tests a simple (length < 160 chars) SMS message
 * Created by Stefan Beukmann on 17.02.2016.
 */
public class ShortPreviewActivityTest extends PreviewActivityTest {
    @Override
    protected String getMessage() {
        return "EIN ENGEL IST POWER";
    }
}
