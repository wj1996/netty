package com.wj04.netty.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;
//import io.netty.util.ReferenceCountUtil;

public class ClientProtocolHandler extends SimpleChannelInboundHandler {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = msg.toString();
        System.out.println("client receive content: " + msg);
        message = SeverProtocolHandler.ProtocolParser.parse(message);
        if (null == message) {
            System.out.println("error request from cient");
            return;
        }
        System.out.println("client receive message ：" + message);
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

    static class ProtocolParser {
        public static String parse(String message) {
            String[] temp = message.split("HEADBODY");
            temp[0] = temp[0].substring(4);
            temp[1] = temp[1].substring(0,(temp[1].length() - 4));
            int length = Integer.parseInt(temp[0].substring(temp[0].indexOf(":") + 1));
            if (length != temp[1].length()) {
                return null;
            }

            return temp[1];
        }

        public static String transfer(String message) {
            StringBuilder sb = new StringBuilder();
            sb.append("HEADcontent-length:" + message.length() + "HEADBODY" + message + "BODY");
            return sb.toString();
        }
    }
}
