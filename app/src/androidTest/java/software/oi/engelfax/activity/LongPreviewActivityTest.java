package software.oi.engelfax.activity;

/**
 * Tests long (=multipart) SMS messages
 * Created by Stefan Beukmann on 17.02.2016.
 */
public class LongPreviewActivityTest extends PreviewActivityTest {
    @Override
    protected String getMessage() {
        return "EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER EIN ENGEL IST POWER";
    }
}
