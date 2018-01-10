package org.mel.complex;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import org.mel.BootstrapUtils;

public class ComplexClientHandler extends SimpleChannelInboundHandler {
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {

    }

    public static void main(String[] args) throws InterruptedException {
        BootstrapUtils.startClient(new BootstrapUtils.HandlerCreator() {
            public void doProcess(SocketChannel channel) {
                channel.pipeline().addLast(new StringEncoder());
            }
        }, new BootstrapUtils.MessageSender() {
            public void send(ChannelFuture f) {
                for (int i = 0; i < 100; i++) {
                    f.channel().writeAndFlush(String.format("Welcome %d\r\n", (i + 1)));
                }
            }
        });
    }

}
