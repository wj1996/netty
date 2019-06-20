package com.wj04.netty.protocol;


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
public class SeverProtocolHandler extends SimpleChannelInboundHandler {

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        String message = msg.toString();
        System.out.println("server receive content: " + msg);
        message = ProtocolParser.parse(message);
        if (null == message) {
            System.out.println("error request from cient");
            return;
        }
        System.out.println("server receive message:" + message);
        String line = "server message";
        line = ProtocolParser.transfer(line);
        System.out.println("server send protocol content :" + line);
        ctx.writeAndFlush(Unpooled.copiedBuffer(line.getBytes("utf-8")));
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        cause.printStackTrace();
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
