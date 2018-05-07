package org.mel.complex;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import org.mel.BootstrapUtils;

public class ComplexServerHandler extends SimpleChannelInboundHandler<String> {
    protected void channelRead0(ChannelHandlerContext ctx, String msg) {
        System.out.print(msg);
        System.out.println(String.format("-------->处理 %s", msg));
    }

    public static void main(String[] args) throws Exception {
        EventExecutorGroup workerPool = new DefaultEventExecutorGroup(10);
        BootstrapUtils.startServer(new BootstrapUtils.HandlerCreator() {
            public void doProcess(SocketChannel channel) {
                channel.pipeline().addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
                channel.pipeline().addLast(new StringDecoder());
                channel.pipeline().addLast(workerPool, new ComplexServerHandler());
            }
        });
    }
}
