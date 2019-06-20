package com.wj01.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
//import io.netty.util.ReferenceCountUtil;

public class ClientHelloWorldHandler extends SimpleChannelInboundHandler<ByteBuf> {


    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        System.out.println("client accept " + msg.toString(CharsetUtil.UTF_8));
//        ReferenceCountUtil.release(msg);
    }
}
