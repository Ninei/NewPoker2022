package poker.io.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.extern.log4j.Log4j2;
import poker.PokerContext;
import poker.io.codec.protocol.PROC_Card;
import poker.io.codec.protocol.PROC_ChoiceCard;
import poker.io.codec.protocol.PROC_Poker;
import poker.io.codec.protocol.PROC_UnionTable;
import poker.io.service.PokerServiceManager;
import poker.io.service.play.PokerUser;

@Log4j2
public class PlayServerHandler extends ChannelInboundHandlerAdapter implements PokerContext {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof PROC_Poker) {
            switch (((PROC_Poker) msg).procTableType()) {
                case PROC_UnionTable.PROC_Login:
                    pokerServiceMgr.RCV_Login(user);
                    break;
                case PROC_UnionTable.PROC_Room_Enter:
                    pokerServiceMgr.RCV_EnterRoom(user);
                    break;
                case PROC_UnionTable.PROC_Room_Ready:
                    pokerServiceMgr.RCV_ReadyRoom(user);
                    break;
                case PROC_UnionTable.PROC_Room_Exit:
                    pokerServiceMgr.RCV_ExitRoom(user);
                    break;
                case PROC_UnionTable.PROC_ChoiceCard:
                    ((PROC_Poker) msg).procTable(procChoiceCard);
                    pokerServiceMgr.RCV_UserChoiceCard(user, procChoiceCard.choiceCard(procCard));
                    break;
                case PROC_UnionTable.PROC_Batting:
                    pokerServiceMgr.RCV_UserBatting(user, (PROC_Poker)msg);
                    break;
            }
        } else {
            log.error("Wrong Instance - " + msg);
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        // NOTE: 보안, 추후 적용 예정
        if (ctx.pipeline().get(SslHandler.class) != null) {
            ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(
                (GenericFutureListener<Future<Channel>>) future -> {
                    log.info("Your session is protected by " +
                            ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() +
                            " cipher suite.\n");
                }
            );
        }
        user = pokerServiceMgr.channelActive(ctx);
        // NOTE: handshake 끝나지 않아 메세지 전달 불가
        log.info("channelActive");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // NOTE: 채널로 부터 ip 획득 가능, mac ???
//        ctx.channel().remoteAddress();
        super.channelInactive(ctx);
        pokerServiceMgr.channelInactive(ctx, user);
        log.info("channelInactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // FIXME: 공통 예외 처리 모듈 필요...
        log.error(cause);
        if(ctx.channel().isActive()) {
            log.info("exceptionCaught: " + ctx.channel().remoteAddress());
            pokerServiceMgr.channelInactive(ctx, user);
            cause.printStackTrace();
            ctx.close();
        } else {
            log.info("exceptionCaught: " + cause);
            ctx.fireExceptionCaught(cause);
        }
    }

    public PlayServerHandler(PokerServiceManager pokerServiceManager) {
        super();
        this.pokerServiceMgr = pokerServiceManager;
    }

    private PROC_ChoiceCard procChoiceCard = new PROC_ChoiceCard();
    private PROC_Card procCard = new PROC_Card();

    private PokerUser user;

    private final PokerServiceManager pokerServiceMgr; // Singleton
}
