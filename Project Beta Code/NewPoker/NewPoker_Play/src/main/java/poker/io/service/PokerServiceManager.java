package poker.io.service;

import io.netty.channel.ChannelHandlerContext;
import io.ninei.service.DefaultService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.io.codec.protocol.*;
import poker.io.service.play.PlayContext;
import poker.io.service.play.PokerUser;

@Log4j2
@Service
public final class PokerServiceManager implements PlayContext, DefaultService {

    public void RCV_UserChoiceCard(PokerUser user, PROC_Card card) throws Exception {
        user.getRoom().RCV_UserChoiceCard(Play_Type.PLAY_USER, user, card);
    }

    public void RCV_UserBatting(PokerUser user, PROC_Poker msg) throws Exception {
        user.getRoom().RCV_UserBatting(Play_Type.PLAY_USER, user, msg);
    }

    public void RCV_EnterRoom(PokerUser user) throws Exception {
        pokerRoomService.enterRoom(Play_Type.PLAY_USER, user);
    }

    public void RCV_ReadyRoom(PokerUser user) throws Exception {
        user.getRoom().RCV_UserReady(user);
    }

    public void RCV_ExitRoom(PokerUser user) throws Exception {
        pokerRoomService.exitRoom(user);
    }

    public void RCV_Login(PokerUser user) throws Exception {
        pokerUserService.login(user);
    }

    public void RCV_Logout(PokerUser user) throws Exception {
        pokerUserService.logout(user);
    }

    public void broadcastGlobal(Object obj) throws Exception {
        pokerChannelGroupService.broadcastChannelGroup(obj);
    }

    public PokerUser channelActive(ChannelHandlerContext ctx) throws Exception {
        pokerChannelGroupService.enterChannelGroup(ctx.channel());
        return pokerUserService.createPokerUser(ctx);
    }

    public PokerUser channelInactive(ChannelHandlerContext ctx, PokerUser user) throws Exception {
        pokerChannelGroupService.exitChannelGroup(ctx.channel());
        if(user != null) {
            RCV_Logout(user);
            user.destroy();
        }
        return null;
    }

    @Override
    public void destroy() {
        if(pokerRoomService != null) pokerRoomService.destroy(); pokerRoomService = null;
        if(pokerUserService != null) pokerUserService.destroy(); pokerUserService = null;
        if(pokerChannelGroupService != null) pokerChannelGroupService.destroy(); pokerChannelGroupService = null;
    }

    private PokerServiceManager(PokerChannelGroupService globalChannelService, PokerRoomService pokerRoomService,
                                PokerUserService pokerUserService) {
        this.pokerChannelGroupService = globalChannelService;
        this.pokerRoomService = pokerRoomService;
        this.pokerUserService = pokerUserService;
    }

    private PokerChannelGroupService pokerChannelGroupService;
    private PokerRoomService pokerRoomService;
    private PokerUserService pokerUserService;
}
