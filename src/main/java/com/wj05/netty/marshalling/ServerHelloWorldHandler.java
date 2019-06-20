package com.wj05.netty.marshalling;


import com.utils.GzipUtils;
import com.utils.RequestMessage;
import com.utils.ResponseMessage;
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
public class ServerHelloWorldHandler extends SimpleChannelInboundHandler {

    /**
     * @param ctx  上下文对象，其中包含客户端建立连接的所有资源
     * @throws Exception
     */
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("from client : ClassName - " + msg.getClass().getName() + " ; message : " + msg.toString());
        if (msg instanceof RequestMessage) {
            RequestMessage request = (RequestMessage) msg;
            byte[] attachment = GzipUtils.unzip(request.getAttachment());
            System.out.println(new String(attachment));
        }
        ResponseMessage response = new ResponseMessage(0L,"test response");
        ctx.writeAndFlush(response);
    }
}
