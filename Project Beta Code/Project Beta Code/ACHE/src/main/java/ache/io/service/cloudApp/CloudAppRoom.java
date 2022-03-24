package ache.io.service.cloudApp;

import ache.ACHEContext;
import ache.io.service.GlobalChannelGroupService;
import io.ninei.service.DefaultRoom;
import io.ninei.service.DefaultUser;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Log4j2
public class CloudAppRoom extends DefaultRoom implements ACHEContext {

    public synchronized CloudApp getCloudApp(String pid) throws Exception {
//        log.info("[Find pid] - {}", pid);
        CloudApp cloudApp = null;
        Map<String, DefaultUser> userMap = getUserMap();
        if (userMap.containsKey(pid)) {
            cloudApp = (CloudApp) userMap.get(pid);
        }
        return cloudApp;
    }

    @Override
    protected void dispose() {}

    public CloudAppRoom(String id, String name, int max) {
        super(id, name, max, new GlobalChannelGroupService());
        log.info("Room Creation Complete!! - {}", getTraceInfo());
    }
}