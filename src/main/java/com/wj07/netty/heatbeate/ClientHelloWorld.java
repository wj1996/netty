package com.wj07.netty.heatbeate;

import com.utils.GzipUtils;
import com.utils.RequestMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 客户端发起请求，不需要监听，只需要定义一个唯一的线程组
 */
public class ClientHelloWorld {

    private EventLoopGroup group = null;
    private Bootstrap bootstrap = null;

    public ClientHelloWorld() {
        init();
    }

    private void init() {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group).channel(NioSocketChannel.class);

    }

    public ChannelFuture doRequest(String host,int port) throws InterruptedException {
       this.bootstrap.handler(new ChannelInitializer<SocketChannel>() {
           protected void initChannel(SocketChannel ch) throws Exception {
               ch.pipeline().addLast(SerializableFactoryMarshalling.buildMarshallingDecoder());
               ch.pipeline().addLast(SerializableFactoryMarshalling.buildMarshallingEncoder());
               ch.pipeline().addLast(new ClientHelloWorldHandler());
           }
       });
        ChannelFuture channelFuture = this.bootstrap.connect(host, port).sync();
        return channelFuture;
    }

    public void release() {
        this.group.shutdownGracefully();
    }

    public static void main(String[] args) {
        ClientHelloWorld client = new ClientHelloWorld();
        ChannelFuture channelFuture = null;
        try {
            channelFuture = client.doRequest("localhost",9998);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
