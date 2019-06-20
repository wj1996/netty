package com.wj05.netty.marshalling;

import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.marshalling.*;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;

public class SerializableFactoryMarshalling {

    /**
     * 解码器
     * @return
     */
    public static ChannelHandler buildMarshallingDecoder() {
        //首先通过Marshalling工具类的方法获取Marshalling实例对象，参数serial标识创建的是java序列化工厂对象
        //Jboss-marshalling-serial 包提供
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        //创建了MarshallingConfiguration对象，配置版本号为5
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        //序列化版本号，使用jdk5以上，version只能定义为5
        configuration.setVersion(5);
        //根据marshallerFactory和configuration创建provider
        UnmarshallerProvider provider = new DefaultUnmarshallerProvider(marshallerFactory,configuration);
        //构建Netty的MarshallingDecoder对象，两个参数分别为provider和单个消息序列化后的最大长度
        MarshallingDecoder decoder = new MarshallingDecoder(provider,1024 * 1024 *1);
        return decoder;

    }

    /**
     * 编码器
     * @return
     */
    public static ChannelHandler buildMarshallingEncoder() {
        final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
        final MarshallingConfiguration configuration = new MarshallingConfiguration();
        configuration.setVersion(5);
        MarshallerProvider provider = new DefaultMarshallerProvider(marshallerFactory,configuration);
        MarshallingEncoder encoder = new MarshallingEncoder(provider);
        return encoder;
    }
}
