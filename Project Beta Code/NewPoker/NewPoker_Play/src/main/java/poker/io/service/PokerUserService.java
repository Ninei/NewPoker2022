package poker.io.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.ninei.service.DefaultUserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import poker.PokerContext;
import poker.io.codec.PokerProtocolFactory;
import poker.io.service.play.PokerUser;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Log4j2
@Service
public final class PokerUserService extends DefaultUserService<PokerUser> implements PokerContext {

    public void purchaseProduct(PokerUser user) {
        // TODO: 결제 처리 로직 추가
        int productPrice = 1000;
        updateCoin(user, user.subtractCoin(productPrice));
        log.info("{}PurchaseProduct: {}",user, user.subtractCoin(productPrice));
    }

    public void updateCoin(PokerUser user, BigInteger userCoin) {
        // TODO: DB 저장 로직 추가
        user.setCoin(userCoin); // FIXME: 메모리 낭비, 수정해야 함....
        log.info("{}SetUserCoin: {}", user, userCoin);
    }

    public PokerUser createPokerUser(ChannelHandlerContext context) {
        Integer cnt = atomic.incrementAndGet();
        String id = cnt + UUID.randomUUID().toString();
        return new PokerUser(context);
    }

    @Override
    public void login(PokerUser user) throws Exception {
        //TODO: DB 조회 로직 추가
        user.login();
        user.sendMessage(pf.Proc_Login(user));
        log.info("{}Login", user);
    }

    @Override
    public void logout(PokerUser user) throws Exception {
        pokerRoomService.exitRoom(user);
        log.info("{}Log Out", user);
    }

    @Override
    protected void dispose() {
        atomic = null;
        pokerRoomService = null;
    }

    private PokerUserService(PokerRoomService pokerRoomService) {
        super(pokerRoomService);
        this.pokerRoomService = pokerRoomService;
    }

    private AtomicInteger atomic = new AtomicInteger(0);
    private PokerRoomService pokerRoomService;

    private static final PokerProtocolFactory pf = new PokerProtocolFactory();
}
