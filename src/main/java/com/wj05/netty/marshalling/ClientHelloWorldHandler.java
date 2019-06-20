package com.wj05.netty.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
//import io.netty.util.ReferenceCountUtil;

public class ClientHelloWorldHandler extends SimpleChannelInboundHandler {


    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        System.out.println("from server : ClassName - " + msg.getClass().getName() + " ; message : " + msg.toString());
    }
}
