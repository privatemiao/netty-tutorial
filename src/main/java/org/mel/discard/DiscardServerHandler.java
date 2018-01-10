package org.mel.discard;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    final boolean SEND_BACK = true;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (SEND_BACK) {
//            不要释放资源（msg），当ctx写入成功后，会自动释放资源
            ctx.writeAndFlush(msg);
        } else {
            ByteBuf in = (ByteBuf) msg;
            try {
                System.out.print(in.toString(CharsetUtil.UTF_8));
            } finally {
//            必须要释放 引用计数类型的 ByteBuf
                ReferenceCountUtil.release(msg);
            }
        }
    }

    /**
     * 通常在此处的操作：
     * 1、记录错误日志
     * 2、返回错误号给客户端
     * 3、关闭通道（channel）
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

}
