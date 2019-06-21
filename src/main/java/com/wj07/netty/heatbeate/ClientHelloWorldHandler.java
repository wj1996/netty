package com.wj07.netty.heatbeate;

import com.utils.HeatbeatMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.hyperic.sigar.*;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
//import io.netty.util.ReferenceCountUtil;

public class ClientHelloWorldHandler extends SimpleChannelInboundHandler {

    private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
    private ScheduledFuture heatbeat;
    private InetAddress remoteAddr;
    private static final String HEATBEAT_SUCCESS = "SERVER_RETURN_HEATBEAT_SUCCESS";

    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof String) {
            if (HEATBEAT_SUCCESS.equals(msg)) {
                this.heatbeat = this.executorService.scheduleWithFixedDelay(new HeatbeatTask(ctx),0L,2L,TimeUnit.SECONDS);
                System.out.println("client receive " + msg);
            } else {
                System.out.println("client receive " + msg);
            }
        }
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.remoteAddr = InetAddress.getLocalHost();
        String computerName = System.getenv().get("COMPUTERNAME");
        String credentials = this.remoteAddr.getHostAddress() + "_" + computerName;
        System.out.println(credentials);
        ctx.writeAndFlush(credentials);
    }
    class HeatbeatTask implements Runnable {
        private ChannelHandlerContext ctx;

        public HeatbeatTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        public void run() {
            HeatbeatMessage msg = new HeatbeatMessage();
            msg.setIp(remoteAddr.getHostAddress());
            Sigar sigar = new Sigar();
            try {
                CpuPerc cpuPerc = sigar.getCpuPerc();
                Map<String,Object> cpuMsgMap = new HashMap();
                cpuMsgMap.put("Combined",cpuPerc.getCombined());
                cpuMsgMap.put("User",cpuPerc.getUser());
                cpuMsgMap.put("Sys",cpuPerc.getSys());
                cpuMsgMap.put("Wait",cpuPerc.getWait());
                cpuMsgMap.put("Idle",cpuPerc.getIdle());
                //内存信息
                Map<String,Object> memMsgMap = new HashMap();
                Mem mem = sigar.getMem();
                memMsgMap.put("Total",mem.getTotal());
                memMsgMap.put("Used",mem.getUsed());
                memMsgMap.put("Free",mem.getFree());
                //文件系统
                Map<String,Object> fileSystemMap = new HashMap<String, Object>();
                FileSystem[] list = sigar.getFileSystemList();
                fileSystemMap.put("fileSysCount",list.length);
                List<String> msgList = null;
                for (FileSystem fs : list) {
                    msgList = new ArrayList();
                    msgList.add(fs.getDevName() + " 总大小 ：  " + sigar.getFileSystemUsage(fs.getDirName()).getTotal() + " kb");
                    msgList.add(fs.getDevName() + " 剩余大小 ：  " + sigar.getFileSystemUsage(fs.getDirName()).getFree() + " kb");
                    fileSystemMap.put(fs.getDevName(),msgList);
                }
                msg.setCpuMsgMap(cpuMsgMap);
                msg.setMemMsgMap(memMsgMap);
                msg.setFileSysMsgMap(fileSystemMap);
                ctx.writeAndFlush(msg);
            } catch (SigarException e) {
                e.printStackTrace();
            }

        }
    }
}
