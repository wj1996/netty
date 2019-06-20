package com.wj02.netty.fixedlength;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledHeapByteBuf;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;

/**
 * Shareable注解代表当前Handler是一个可以分享的处理器，服务注册此Handler后，可以分享给多个客户端同时使用
 * 如果不使用注解描述类型，则每次客户端请求时，必须为客户端重新创建一个新的Handler对象
 * 如果Handler是一个Shareable，注意线程安全问题
 *  bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
 *             protected void initChannel(SocketChannel socketChannel) throws Exception {
 *                 socketChannel.pipeline().addLast(new XxxHanlder());
 *             }
 *         });
 */
@ChannelHandler.Sharable
public class ServerFixedLengthHandler extends SimpleChannelInboundHandler {

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server accept " + msg.toString());
        String line = "ok ";
        //写操作自动释放内存，避免内存溢出问题
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")));

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
