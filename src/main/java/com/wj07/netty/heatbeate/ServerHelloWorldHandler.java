package com.wj07.netty.heatbeate;


import com.utils.GzipUtils;
import com.utils.HeatbeatMessage;
import com.utils.RequestMessage;
import com.utils.ResponseMessage;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.ArrayList;
import java.util.List;

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
public class ServerHelloWorldHandler extends SimpleChannelInboundHandler {

    private static List<String> credentials = new ArrayList();
    private static final String HEATBEAT_SUCCESS = "SERVER_RETURN_HEATBEAT_SUCCESS";

    public ServerHelloWorldHandler() {
        //初始化客户端列表信息，一般通过配置文件获取或者数据库获取
        credentials.add("169.254.20.50_WJ");
    }


    /**
     * @param ctx  上下文对象，其中包含客户端建立连接的所有资源
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            this.checkCredential(ctx,msg.toString());
        } else if (msg instanceof HeatbeatMessage) {
            this.readHeatbeanMessage(ctx,msg);
        } else {
            ctx.writeAndFlush("wrong msg ").addListener(ChannelFutureListener.CLOSE);
        }
    }

    private void readHeatbeanMessage(ChannelHandlerContext ctx, Object msg) {
        HeatbeatMessage message = (HeatbeatMessage) msg;
        System.out.println(message);
        System.out.println("==============================");
        ctx.writeAndFlush("receive heatbeat message");
    }

    private void checkCredential(ChannelHandlerContext ctx, String credential) {
        System.out.println(credential);
        System.out.println(credentials);
        if (credentials.contains(credential)) {
            ctx.writeAndFlush(HEATBEAT_SUCCESS);
        } else {
            ctx.writeAndFlush(" no credential contains").addListener(ChannelFutureListener.CLOSE);
        }

    }
}
