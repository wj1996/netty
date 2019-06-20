package com.wj03.specialendcharacter;


import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

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
public class SeverDelimiterHandler extends SimpleChannelInboundHandler {

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server accept " + msg.toString());
        String line = "server msg $E$ test delimiter handler! $E$ second message $E$";
        //写操作自动释放内存，避免内存溢出问题
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")));

    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
    }
}
