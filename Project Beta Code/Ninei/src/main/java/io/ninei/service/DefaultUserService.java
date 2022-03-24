package io.ninei.service;

import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class DefaultUserService<T> implements DefaultService {

    public abstract void login(T user) throws Exception;

    public abstract void logout(T user) throws Exception;

    protected synchronized void enterRoom(DefaultUser user) throws Exception {
        roomService.enterRoom(user);
    }

    protected synchronized void exitRoom(DefaultUser user) throws Exception {
        roomService.exitRoom(user);
    }

    protected abstract void dispose();

    public void destroy() {
        dispose();
        roomService = null;
    }

    protected DefaultUserService(DefaultRoomService roomService) {
        this.roomService = roomService;
    }

    protected DefaultRoomService roomService;
}
