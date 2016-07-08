package bgby.skynet.org.smarthomedriverproxy;


import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import bgby.skynet.org.smarthomedriverproxy.common.Utils;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Controllers.setScreenDirection(this);
        setContentView(R.layout.activity_setting_page);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        Utils.initScreenDirection(this);
        getFragmentManager().beginTransaction()
                .replace(R.id.fragmentSettings, new SettingsFragment())
                .commit();

    }
}