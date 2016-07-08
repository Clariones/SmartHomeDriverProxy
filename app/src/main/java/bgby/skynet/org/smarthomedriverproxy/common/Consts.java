package bgby.skynet.org.smarthomedriverproxy.common;

import android.content.pm.ActivityInfo;

/**
 * Created by Clariones on 7/8/2016.
 */
public class Consts {
    public static final int REQUEST_CODE_BASIC_SETTING = 1000;
    public static final String PREFERENCE_KEY_DIRECTION = "pref_key_rotate_screen";
    public static final String PREFERENCE_KEY_DEVICE_ID = "pref_key_mine_id";
    public static final int DEFAULT_DIRECTION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public static final String PREFERENCE_VALUE_UNSET_STRING = "---";
    public static final String PREFERENCE_KEY_MULTICAST_PORT = "pref_key_multicast_port";
    public static final String PREFERENCE_KEY_PROXY_PORT = "pref_key_driverproxy_port";
    public static final String PREFERENCE_KEY_MINE_ID = "pref_key_mine_id";
    public static final String CFG_NAME_REST_PORT = "param_proxy_port";
    public static final String CFG_NAME_UDP_PORT = "param_udp_port";
    public static final String CFG_NAME_APP_ID = "param_mine_id";
    public static final int DEFAULT_UDP_PORT = 8982;
    public static final int DEFAULT_CONN_TIME_OUT = 30000;
    public static final int DEFAULT_READ_TIMEOUT = 30000;
    public static final int DEFAULT_REST_SERVICE_PORT = 8981;
    public static final int SERVICE_NOTIFY_ID = 1377;
    public static final String FS_DEVICE_PROFILE = "fs/deviceProfile";
    public static final String FS_DEVICE_INFO = "fs/deviceInfo";
    public static final String FS_CONTROLLER_LAYOUT = "fs/controllerLayout";
    public static final String FS_DEVIE_STATUS = "fs/devieStatus";
    public static final String CFG_NAME_BROADCAST_ADDR ="param_broadcast_address" ;
}
