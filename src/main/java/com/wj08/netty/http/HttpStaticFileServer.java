package com.wj08.netty.http;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpStaticFileServer {

    private final int port;

    public HttpStaticFileServer(int port) {
        this.port = port;
    }

    public void run() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup,workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpStaticFileServerInitializer());
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) throws Exception {
        int port = 8889;
        new HttpStaticFileServer(port).run();
    }
}
