package ache.io.service.viewer;

import ache.ACHEContext;
import ache.io.service.GlobalChannelGroupService;
import io.ninei.service.DefaultRoom;
import io.ninei.service.DefaultUser;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Getter
@Log4j2
public class ViewerRoom extends DefaultRoom implements ACHEContext {

    @Override
    protected void dispose() {}

    public ViewerRoom(String id, String name, int max) {
        super(id, name, max, new GlobalChannelGroupService());
        log.info("Viewer Room Creation Complete!! - {}", getTraceInfo());
    }

}
