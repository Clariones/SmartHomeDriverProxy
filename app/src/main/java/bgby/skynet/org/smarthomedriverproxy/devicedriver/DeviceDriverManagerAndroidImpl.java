package bgby.skynet.org.smarthomedriverproxy.devicedriver;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.skynet.bgby.devicedriver.DriverManagerImpl;
import org.skynet.bgby.devicedriver.DriverModuleException;
import org.skynet.bgby.devicedriver.DriverRegisterInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

/**
 * Created by Clariones on 7/8/2016.
 */
public class DeviceDriverManagerAndroidImpl extends DriverManagerImpl {
    private InputStream ins;

    @Override
    protected void loadDriverRegisterInfos() throws DriverModuleException {
        List<DriverRegisterInfo> driverRegisterInfos = loadDriverRegisterInfoFromFile();
        if (driverRegisterInfos.isEmpty()) {
            throw new DriverModuleException(
                    "Cannot found any valid driver register info from package");
        }
        startingReporter.reportStatus(this.curStartingStep(), "Search all driver register infos",
                "Totally found " + driverRegisterInfos.size() + " driver infos");
        for (DriverRegisterInfo info : driverRegisterInfos) {
                addDriverRegisterInfo(info);
        }
    }

    private List<DriverRegisterInfo> loadDriverRegisterInfoFromFile() throws DriverModuleException{
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(this.ins);
        try{
            TypeToken<List<DriverRegisterInfo>> list = new TypeToken<List<DriverRegisterInfo>>() {};
            List<DriverRegisterInfo> result = gson.fromJson(reader, list.getType());
            return result;
        }catch (Exception e){
            e.printStackTrace();
            throw new DriverModuleException("Error when load from JSON", e);
        }finally {
            try {
                reader.close();
            } catch (IOException e) {
            }
        }
    }

    public void setRegisterInputStream(InputStream is) {
        this.ins = is;
    }
}
