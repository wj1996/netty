package com.wj.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Netyy
 */
public class EchoClient {

    private final int port;
    private final String host;

    public EchoClient(int port,String host) {
        this.port = port;
        this.host = host;
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host,port)
                    .handler(new EchoClientHandler());
            ChannelFuture channelFuture = bootstrap.connect().sync();//连接到远程阻塞 sync 直到连接成功
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        try {
            new EchoClient(9999,"localhost").start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

