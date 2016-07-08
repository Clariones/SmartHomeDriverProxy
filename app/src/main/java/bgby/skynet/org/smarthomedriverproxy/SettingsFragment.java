package bgby.skynet.org.smarthomedriverproxy;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

import bgby.skynet.org.smarthomedriverproxy.common.Consts;
import bgby.skynet.org.smarthomedriverproxy.preference.RotateScreenPreference;

/**
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected static final String KEY_ROTATE_SCREEN = "pref_key_rotate_screen";
    protected static final String KEY_MULTICAST_PORT = Consts.PREFERENCE_KEY_MULTICAST_PORT;
    protected static final String KEY_PROXY_PORT = Consts.PREFERENCE_KEY_PROXY_PORT;
    protected static final String KEY_MINE_ID = Consts.PREFERENCE_KEY_MINE_ID;
    protected static final String[] MINE_KEYS = {
            KEY_MULTICAST_PORT,
            KEY_PROXY_PORT,
            KEY_MINE_ID
    };
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        initSummaries();
    }

    private void initSummaries() {
        for(String key : MINE_KEYS){
            EditTextPreference pref = (EditTextPreference) findPreference(key);
            String value = pref.getText();
            pref.setSummary(value);
        }
    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_ROTATE_SCREEN) || key.equals(Consts.PREFERENCE_KEY_DIRECTION)){
            RotateScreenPreference pref = (RotateScreenPreference) findPreference(key);
//            pref.setSummary("AHA");
        }else{
            Log.i("OK", "onSharedPreferenceChanged: key="+key+", value="+sharedPreferences.getString(key, null));
            EditTextPreference pref = (EditTextPreference) findPreference(key);
            pref.setSummary(sharedPreferences.getString(key, null));
        }
    }
}
