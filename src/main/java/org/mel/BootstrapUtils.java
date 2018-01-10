package org.mel;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;

public class BootstrapUtils {

    public interface HandlerCreator{
        void doProcess(SocketChannel channel);
    }

    public static void startServer(final HandlerCreator creator) throws Exception {
        /*
         * NioEventLoopGroup: 是一个多线程的、控制输入输出流的事件循环
         * bossGroup: 接受客户端的连接，将连接注册到 workGroup
         * workGroup: 处理接受连接的流量（traffic）
         */
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            /*
             * ServerBootstrap: 是一个设置服务的帮助类
             */
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
//                    指定使用 NioServerSocketChannel 实例化一个新的通道接受客户端的请求
                    .channel(NioServerSocketChannel.class)
//                    指定的 childHandler 将会被新连接的通道评估
//                    ChannelInitializer 是一个特殊的处理程序 添加处理程序（handler）
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            creator.doProcess(ch);
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind(21).sync();
            /*
             * 等待，直到 Server Socket 被关闭
             * 如果不设置，当第一个客户端连接成功后，服务端立刻退出运行
             */
            f.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public interface MessageSender{
        void send(ChannelFuture f);
    }
    public static void startClient(final HandlerCreator creator, MessageSender sender) throws InterruptedException {
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            Bootstrap b = new Bootstrap();
            b.group(workGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            creator.doProcess(ch);
                        }
                    });
            ChannelFuture f = b.connect("127.0.0.1", 21).sync();
            sender.send(f);
            f.channel().closeFuture().sync();
        } finally {
            workGroup.shutdownGracefully();
        }
    }
}
