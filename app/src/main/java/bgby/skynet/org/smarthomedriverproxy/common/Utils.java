package bgby.skynet.org.smarthomedriverproxy.common;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.preference.PreferenceManager;

/**
 * Created by Clariones on 7/8/2016.
 */
public class Utils {
    private Utils(){}


    public static void initScreenDirection(Activity activity) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(activity);
        String strDirect = pref.getString(Consts.PREFERENCE_KEY_DIRECTION, String.valueOf(Consts.DEFAULT_DIRECTION));
        int direct = Integer.parseInt(strDirect);
        int curDirect = activity.getRequestedOrientation();
        if (curDirect == direct){
            return;
        }
        switch (direct) {
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                break;
            default:
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
        }
    }
}
