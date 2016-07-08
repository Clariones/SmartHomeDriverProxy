package bgby.skynet.org.smarthomedriverproxy;

import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.skynet.bgby.deviceconfig.DeviceConfigData;
import org.skynet.bgby.deviceconfig.DeviceConfigManager;
import org.skynet.bgby.deviceconfig.DeviceConfigManagerPCImpl;
import org.skynet.bgby.devicedriver.DriverManager;
import org.skynet.bgby.deviceprofile.DeviceProfile;
import org.skynet.bgby.deviceprofile.DeviceProfileManager;
import org.skynet.bgby.deviceprofile.DeviceProfileManagerPCImpl;
import org.skynet.bgby.devicestatus.DeviceStatusManager;
import org.skynet.bgby.devicestatus.DeviceStatusManagerPCImpl;
import org.skynet.bgby.driverproxy.DPStatusReporter;
import org.skynet.bgby.driverproxy.DriverProxyConfiguration;
import org.skynet.bgby.driverproxy.DriverProxyService;
import org.skynet.bgby.driverutils.DriverUtils;
import org.skynet.bgby.layout.LayoutData;
import org.skynet.bgby.layout.LayoutManager;
import org.skynet.bgby.layout.LayoutManagerPCImpl;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import bgby.skynet.org.smarthomedriverproxy.common.Consts;
import bgby.skynet.org.smarthomedriverproxy.common.Logger4Andriod;
import bgby.skynet.org.smarthomedriverproxy.common.ServiceStatus;
import bgby.skynet.org.smarthomedriverproxy.devicedriver.DeviceDriverManagerAndroidImpl;

public class DriverProxyStarter extends Service {
    public static final String TAG = "DriverProxyStarter";
    private MyBinder binder;
    protected DriverProxyService service;
    protected List<String> logRecords = new ArrayList<>();
    private DriverProxyConfiguration serviceConfig;
    private WifiManager.WifiLock wifiLock;

    public DriverProxyStarter() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        DriverUtils.setLogger(new Logger4Andriod());
        Log.i(TAG, "Service starting");
        binder = new MyBinder();
    }

    private void createDriverProxyService() {
        service = new DriverProxyService();
        service.setConfig(serviceConfig);
        service.setDeviceConfigManager(createDeviceConfigManager());
        service.setDeviceStatusManager(createDeviceStatusManager());
        service.setDriverManager(createDriverManager());
        service.setLayoutManager(createLayoutManager());
        service.setDeviceProfileManager(createDeviceProfileManager());
        service.setReporter(new StartReporter());
    }

    private DeviceProfileManager createDeviceProfileManager() {
        DeviceProfileManagerPCImpl profileManager = new DeviceProfileManagerPCImpl();
        profileManager.setBaseFolder(new File(getFilesDir(), Consts.FS_DEVICE_PROFILE));
        return profileManager;
    }

    private LayoutManager createLayoutManager() {
        LayoutManagerPCImpl layoutManager = new LayoutManagerPCImpl();
        layoutManager.setBaseFolder(new File(getFilesDir(), Consts.FS_CONTROLLER_LAYOUT));
        return layoutManager;
    }

    private DriverManager createDriverManager() {
        DeviceDriverManagerAndroidImpl driverManager = new DeviceDriverManagerAndroidImpl();
        InputStream is = getResources().openRawResource(R.raw.drivers);
        driverManager.setRegisterInputStream(is);
        return driverManager;
    }

    private DeviceStatusManager createDeviceStatusManager() {
        DeviceStatusManagerPCImpl statusManager = new DeviceStatusManagerPCImpl();
        statusManager.setBaseFolder(new File(getFilesDir(), Consts.FS_DEVIE_STATUS));
        return statusManager;
    }

    private DeviceConfigManager createDeviceConfigManager() {
        DeviceConfigManagerPCImpl configManager = new DeviceConfigManagerPCImpl();
        configManager.setBaseFolder(new File(getFilesDir(), Consts.FS_DEVICE_INFO));
        return configManager;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        if (service!= null){
            service.stop();
        }
        super.onDestroy();
        Log.i(TAG, "Service stopped");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Notification notification=new NotificationCompat.Builder(this)
                .setContentText("DriverProxyService")
                .setPriority(Notification.PRIORITY_MIN)
                .build();
        startForeground(Consts.SERVICE_NOTIFY_ID, notification);
        serviceConfig = getConfigFromIntent(intent);
        createDriverProxyService();
        startDriverProxyService();
        return START_STICKY;
    }

    private DriverProxyConfiguration getConfigFromIntent(Intent intent) {
        DriverProxyConfiguration cfg = new DriverProxyConfiguration();
        cfg.setAppId(intent.getStringExtra(Consts.CFG_NAME_APP_ID));
        cfg.setConnectionTimeout(Consts.DEFAULT_CONN_TIME_OUT);
        cfg.setMulticastAddress(null);
        cfg.setMulticastPort(intent.getIntExtra(Consts.CFG_NAME_UDP_PORT, Consts.DEFAULT_UDP_PORT));
        cfg.setReadTimeout(Consts.DEFAULT_READ_TIMEOUT);
        cfg.setRestServicePort(intent.getIntExtra(Consts.CFG_NAME_REST_PORT, Consts.DEFAULT_REST_SERVICE_PORT));
        cfg.setMulticastAddress(intent.getStringExtra(Consts.CFG_NAME_BROADCAST_ADDR));
        return cfg;
    }

    private void startDriverProxyService() {
        WifiManager wifi;
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
//            WifiManager.MulticastLock wifiLock = wifi.createMulticastLock("just some tag text");
        wifiLock = wifi.createWifiLock("mylock");
        wifiLock.acquire();
        service.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
//        throw new UnsupportedOperationException("Not yet implemented");
        Log.i(TAG, "Onbind called");
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
//        return super.onUnbind(intent);
        Log.i(TAG, "onUnbind called");
        return true;
    }

    public class MyBinder extends Binder {
        public ServiceStatus getServiceStatues(){
            return checkServiceStatus();
        }
    }

    private ServiceStatus checkServiceStatus() {
        ServiceStatus rst = new ServiceStatus();
        rst.setState(ServiceStatus.STATE_RUNNING);
        List<String> infoList;

        Map<String, DeviceConfigData> map = service.getDeviceConfigManager().listAllDevices();
        if (map != null){
            infoList = new ArrayList<>(map.size());
            for(DeviceConfigData data: map.values()){
                infoList.add(data.getID()+": " + data.getProfile());
            }
            Collections.sort(infoList);
            rst.setDevices(infoList);
        }

        Map<String, DeviceProfile> map2 = service.getDeviceProfileManager().listAllProfiles();
        if (map2 != null){
            infoList = new ArrayList<>(map2.size());
            for(DeviceProfile data: map2.values()){
                infoList.add(data.getID());
            }
            Collections.sort(infoList);
            rst.setProfiles(infoList);
        }

        Map<String, List<LayoutData>> map3 = service.getLayoutManager().getAllLayout();
        if (map3 != null){
            infoList = new ArrayList<>(map3.size());
            infoList.addAll(map3.keySet());
            Collections.sort(infoList);
            rst.setLayouts(infoList);
        }

        infoList = new ArrayList<>(DriverProxyService.deviceStandards.keySet());
        Collections.sort(infoList);
        rst.setStandards(infoList);
        return rst;
    }

    private class StartReporter implements DPStatusReporter {
        private final SimpleDateFormat sfm = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
        @Override
        public void reportError(int i, String s, String s1) {
            String msg = String.format("[ERROR]%s Step %d [%s]: %s\r\n",sfm.format(new Date()), i, s, s1);
            reportMsg(msg);
        }

        @Override
        public void reportStatus(int i, String s, String s1) {
            String msg = String.format("       %s Step %d [%s]: %s\r\n",sfm.format(new Date()), i, s, s1);
            reportMsg(msg);
        }
    }

    private boolean reportMsg(String msg) {
        DriverUtils.log(Level.INFO, TAG, msg);
        if (logRecords.size() > 100){
            logRecords.remove(0);
        }
        return logRecords.add(msg);
    }


}
