package io.ninei.service;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.ChannelGroupFutureListener;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;

@Log4j2
public abstract class DefaultChannelGroupService implements DefaultService {

    public ChannelGroupFuture broadcastChannelGroup(Object msg) throws Exception {
        return channelGroup.writeAndFlush(msg);
    }

    public boolean enterChannelGroup(Channel channel) { return channelGroup.add(channel); }

    public boolean exitChannelGroup(Channel channel) { return channelGroup.remove(channel); }

    protected void closeChannelGroup() {
        // if this is a graceful shutdown, log any channel closing failures. if this isn't a graceful shutdown, ignore them.
        channelGroup.close().addListener((ChannelGroupFutureListener) channelFutures -> {
            if (!channelFutures.isSuccess()) {
                for (ChannelFuture cf : channelFutures) {
                    if (!cf.isSuccess()) {
                        log.error("Unable to close channel. Cause of failure for {} is {}", cf.channel(), cf.cause());
                    } else {
                        log.info("Channel Group Closed!!");
                    }
                }
            }
        });
    }

    private ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
}
