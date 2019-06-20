package com.wj02.netty.fixedlength;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

import java.nio.charset.Charset;

/**
 * 1.双线程组
 */
public class ServerFixedLength {

    //监听线程组，监听客户端请求
    private EventLoopGroup acceptGroup = null;
    //处理客户端相关操作线程组，负责处理与客户端的数据通讯
    private EventLoopGroup clientGroup = null;
    //服务启动相关配置信息
    private ServerBootstrap bootstrap = null;


    public ServerFixedLength() {
        init();
    }

    private void init() {
        //初始化线程组，构建线程组的时候，如果不传递参数，则默认构建的线程组线程数量是CPU核心数量
        acceptGroup = new NioEventLoopGroup();
        clientGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        //绑定线程组
        bootstrap.group(acceptGroup,clientGroup);
        //设置通讯模式为NIO
        bootstrap.channel(NioServerSocketChannel.class);
        //设置缓冲区大小
        bootstrap.option(ChannelOption.SO_BACKLOG,1024);
        //SO_SNDBUF发送缓冲区 SO_REVBUF 接收缓冲区 SO_KEEPALIVE开启心跳检测（保证连接有效）
        bootstrap.option(ChannelOption.SO_SNDBUF,16 * 1024)
                .option(ChannelOption.SO_RCVBUF,16 * 1024)
                .option(ChannelOption.SO_KEEPALIVE,true);

    }

    /**
     * 监听
     * @param port
     * @param acceptorHandlers
     * @return
     * @throws InterruptedException
     */
    public ChannelFuture doAccept(int port, final ChannelHandler...acceptorHandlers) throws InterruptedException {
        /**
         * childHandler是服务的Bootstrap独有的方法，是用于提供处理对象的
         * 可以一次增加若干个处理逻辑。是类似责任链模式的处理方式
         * 增加A,B两个处理逻辑，在处理客户端请求数据的时候，根据A-》B顺序依次处理
         *
         * ChannelInitializer 用于提供处理器的一个模型对象
         * initChannel方法，用于初始化处理逻辑责任链条的
         * 可以保证服务端的Bootstrap只初始化一次处理器，尽量提供处理逻辑的重用。
         * 避免反复的创建处理器对象。节约资源开销
         *
         */
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel socketChannel) throws Exception {
                ChannelHandler[] handlers = new ChannelHandler[3];
                //定长Handler，通过参数设置消息长度（单位：字节）。发送的消息长度不足可以使用空格补全
                /*使用这个注意依赖引入*/
                handlers[0] = new FixedLengthFrameDecoder(3);
                /*字符串解码器Handler，会自动处理ChannelRead方法的msg参数，将ByteBuf类型的数据转为字符*/
                handlers[1] = new StringDecoder(Charset.forName("utf-8"));
                handlers[2] = new ServerFixedLengthHandler();
                socketChannel.pipeline().addLast(handlers);
            }
        });
        //bind方法，绑定监听端口的。ServerBootStrap可以绑定多个监听端口。多次调用bind方法即可
        //sync  开始监听逻辑。返回一个ChannelFuture。返回结果代表的是监听成功后的一个对应的未来结果
        //可以使用ChannelFuture实现后续的服务器和客户端的交互
        ChannelFuture future = bootstrap.bind(port).sync();
        return future;
    }

    /**
     * shutdownGracefully 安全关闭的方法，可以保证不放弃任何一个已接收的客户端请求
     */
    public void release() {
        this.acceptGroup.shutdownGracefully();
        this.clientGroup.shutdownGracefully();
    }


    public static void main(String[] args) {
        ChannelFuture future = null;
        ServerFixedLength server = null;
        try {
            server = new ServerFixedLength();
            future = server.doAccept(9999);
            System.out.println("server started");
            future.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            if (null != future) {
                try {
                    future.channel().closeFuture().sync();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (null != server) {
                server.release();
            }
        }

    }



}
