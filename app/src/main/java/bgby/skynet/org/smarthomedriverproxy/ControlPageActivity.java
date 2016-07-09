package bgby.skynet.org.smarthomedriverproxy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.skynet.bgby.driverutils.DriverUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import bgby.skynet.org.smarthomedriverproxy.common.Consts;
import bgby.skynet.org.smarthomedriverproxy.common.ServiceStatus;
import bgby.skynet.org.smarthomedriverproxy.common.Utils;

public class ControlPageActivity extends Activity {
    public static final String TAG = "ControlPageActivity";
    protected Button btnStart;
    protected Button btnStop;
    protected Button btnSetting;
    protected Button btnQuery;
    protected TextView txtState;
    protected TextView txtDetail;
    private String mineIpAddress;
    private InetAddress broadCastAddr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        setContentView(R.layout.activity_control_page);
        Utils.initScreenDirection(this);
        if (!isValidConfig()){
            openSettingPage();
        }
        btnStart = (Button) findViewById(R.id.btnStartService);
        btnStop = (Button) findViewById(R.id.btnStopService);
        btnSetting = (Button) findViewById(R.id.btnSettings);
        btnQuery = (Button) findViewById(R.id.btnRefresh);
        txtState = (TextView) findViewById(R.id.txtActiveState);
        txtDetail = (TextView) findViewById(R.id.txtServiceStatus);
        txtDetail.setMovementMethod(new ScrollingMovementMethod());
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettingPage();
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        btnQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryServiceStatus();
            }
        });

        this.broadCastAddr = findBroadCastAddress();
        txtState.setText("服务状态：未启动");
        txtDetail.setText(getIpAddressInfo() + "服务未启动，无详细信息");
        queryServiceStatus();
    }

    private void stopService() {
        Intent intent = new Intent(this, DriverProxyStarter.class);
        stopService(intent);
        txtState.setText("服务状态：未启动");
        txtDetail.setText(getIpAddressInfo() + "服务未启动，无详细信息");
    }

    private void startService() {
        if (!isValidConfig()){
            Toast.makeText(ControlPageActivity.this, "请先正确配置参数", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, DriverProxyStarter.class);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

        intent.putExtra(Consts.CFG_NAME_REST_PORT, DriverUtils.getAsInt(pref.getString(Consts.PREFERENCE_KEY_PROXY_PORT, "8981"), 8981));
        intent.putExtra(Consts.CFG_NAME_UDP_PORT, DriverUtils.getAsInt(pref.getString(Consts.PREFERENCE_KEY_MULTICAST_PORT, "8982"), 8982));
        intent.putExtra(Consts.CFG_NAME_APP_ID, pref.getString(Consts.PREFERENCE_KEY_MINE_ID, null));
        intent.putExtra(Consts.CFG_NAME_BROADCAST_ADDR, broadCastAddr.getHostAddress());
        startService(intent);
    }

    private boolean isValidConfig() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        if (pref.getString(Consts.PREFERENCE_KEY_DEVICE_ID, Consts.PREFERENCE_VALUE_UNSET_STRING).equals(Consts.PREFERENCE_VALUE_UNSET_STRING)){
            return false;
        }
        int port = DriverUtils.getAsInt(pref.getString(Consts.PREFERENCE_KEY_MULTICAST_PORT, "0"), 0);
        if (port < 1024 || port > 65535){
            return false;
        }
        port = DriverUtils.getAsInt(pref.getString(Consts.PREFERENCE_KEY_PROXY_PORT, "0"), 0);
        if (port < 1024 || port > 65535){
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case Consts.REQUEST_CODE_BASIC_SETTING:
                Utils.initScreenDirection(this);
                Toast.makeText(ControlPageActivity.this, "更改设置后，应该重启服务", Toast.LENGTH_SHORT).show();
                return;
        }
    }

    protected void openSettingPage(){
        Log.w(TAG, "Basical setting not set. Open preferneces page");
        Intent intent = new Intent();
        intent.setClassName(this, SettingsActivity.class.getName());
        startActivityForResult(intent, Consts.REQUEST_CODE_BASIC_SETTING);
    }
    protected void queryServiceStatus() {
        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "Service connected");
                DriverProxyStarter.MyBinder binder = (DriverProxyStarter.MyBinder) service;
                onBinderGotFromService(binder);
                unbindConn(this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG, "Service disconnected");
                unbindConn(this);
            }
        };
        Intent intent = new Intent(this, DriverProxyStarter.class);
        bindService(intent, conn, BIND_IMPORTANT);
        //unbindService(conn);
    }


    private void onBinderGotFromService(DriverProxyStarter.MyBinder binder ) {
        StringBuilder sb = new StringBuilder(getIpAddressInfo());
        ServiceStatus status = binder.getServiceStatues();
        appendFromBinder(sb, status.getStandards(), "支持的设备标准", "尚未支持任何设备标准， 请联系厂家售后");
        appendFromBinder(sb, status.getProfiles(), "支持的设备型号", "未配置任何设备型号， 请下发设备Profile");
        appendFromBinder(sb, status.getLayouts(), "已布局的控制屏", "没有配置任何布局, 请下发布局配置");
        appendFromBinder(sb, status.getDevices(), "已配置的设备", "没有配置任何设备, 请下发设备配置");

        txtDetail.setText(sb.toString());
        if (status.getState() == ServiceStatus.STATE_RUNNING){
            txtState.setText("服务状态：运行中");
        }else{
            txtState.setText("服务状态：未启动");
        }
    }

    private void appendFromBinder(StringBuilder sb, List<String> lines, String title, String emptyMsg) {
        sb.append("\r\n").append(title);
        if (lines == null || lines.isEmpty()){
            sb.append(":\r\n    ").append(emptyMsg);
            return;
        }
        for(String line : lines){
            sb.append("\r\n    ").append(line);
        }
    }


    private void unbindConn(ServiceConnection serviceConnection) {
        unbindService(serviceConnection);
    }

    protected InetAddress findBroadCastAddress(){
        WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        int ip = dhcp.ipAddress;
        this.mineIpAddress = String.format("%d.%d.%d.%d", (ip >> 0)&0xFF,(ip >> 8)&0xFF,(ip >> 16)&0xFF,(ip >> 24)&0xFF);
        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        try {
            InetAddress addr = InetAddress.getByAddress(quads);
            Log.i(TAG, "broadcast address should be " + addr);
            return addr;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String getIpAddressInfo(){
        return String.format("%-13s: %s\n%-5s: %s\n", "本机IP", mineIpAddress, "本机广播组", broadCastAddr);
    }
}
