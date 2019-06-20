package com.wj03.specialendcharacter;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * 客户端发起请求，不需要监听，只需要定义一个唯一的线程组
 */
public class ClientDelimiter {

    private EventLoopGroup group = null;
    private Bootstrap bootstrap = null;

    public ClientDelimiter() {
        init();
    }

    private void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);

    }

    public ChannelFuture doRequest(String host, int port) throws InterruptedException {
       this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
           protected void initChannel(SocketChannel ch) throws Exception {
               //数据分割符
               ByteBuf delimter = Unpooled.copiedBuffer("$E$".getBytes());
               ChannelHandler[] handlers = new ChannelHandler[3];
               handlers[0] = new DelimiterBasedFrameDecoder(1024,delimter);
               handlers[1] = new StringDecoder(Charset.forName("UTF-8"));
               handlers[2] = new ClientDelimiterHandler();
               ch.pipeline().addLast(handlers);
//               ch.pipeline().addLast(acceptHandlers);
           }
       });
        ChannelFuture channelFuture = this.bootstrap.connect(host, port).sync();
        return channelFuture;
    }

    public void release() {
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        ClientDelimiter client = new ClientDelimiter();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = client.doRequest("localhost",9999);
            Scanner scanner = null;
            while (true) {
                System.out.println("enter message to server (exit for close server)");
                scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                if ("exit".equals(line)) {
                    channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes()))
                        .addListener(ChannelFutureListener.CLOSE);
                    break;
                }
                channelFuture.channel().writeAndFlush(Unpooled.copiedBuffer(line.getBytes()));
                TimeUnit.SECONDS.sleep(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
