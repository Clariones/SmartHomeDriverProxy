package bgby.skynet.org.smarthomedriverproxy.common;

import java.util.List;

/**
 * Created by Clariones on 7/8/2016.
 */
public class ServiceStatus {
    public static final int STATE_STOP = 0;
    public static final int STATE_RUNNING = 1;

    protected int state;
    protected List<String> layouts;
    protected List<String> profiles;
    protected List<String> devices;
    protected List<String> standards;

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public List<String> getLayouts() {
        return layouts;
    }

    public void setLayouts(List<String> layouts) {
        this.layouts = layouts;
    }

    public List<String> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
    }

    public List<String> getDevices() {
        return devices;
    }

    public void setDevices(List<String> devices) {
        this.devices = devices;
    }

    public List<String> getStandards() {
        return standards;
    }

    public void setStandards(List<String> standards) {
        this.standards = standards;
    }

}
