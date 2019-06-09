package nia.chapter2.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * 代码清单 2-2 EchoServer 类
 *
 * 总结：
 * EventLoop 定义了 Netty 的核心抽象，用于处理连接的生命周期中所发生的事件
 *
 * ChannelPipeline 提供了 ChannelHandler 链的容器。当 Channel 被创建时，它会被自动地分配到它专属的 ChannelPipeline 。
 *
 * ChannelHandler 是专为支持广泛的用途而设计的，可以将它看作是处理往来 ChannelPipeline 事件（包括数据）的任何代码的通用容器；
 * 实际上：被我们称为 ChannelPipeline 的就是这些 ChannelHandler 的编排顺序。
 *
 * Netty 的引导类为应用程序的网络层配置提供了容器，这涉及将一个进程绑定到某个指定的端口，或者将一个进程连接到另一个运行在某个指定主机的指定端口上的进程。
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args)
        throws Exception {
        /*if (args.length != 1) {
            System.err.println("Usage: " + EchoServer.class.getSimpleName() +
                " <port>"
            );
            return;
        }*/
        //设置端口值（如果端口参数的格式不正确，则抛出一个NumberFormatException）
        //int port = Integer.parseInt(args[0]);
        int port = 10000;
        //调用服务器的 start()方法
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler serverHandler = new EchoServerHandler();
        //(1) 创建EventLoopGroup
        //EventLoop的作用：
        // Netty 通过触发事件将 Selector 从应用程序中抽象出来，消除了所有本来将需要手动编写的派发代码。
        // 在内部，将会为每个 Channel 分配 一个 EventLoop ，用以处理所有事件，包括：
        //1. 注册感兴趣的事件;  2.将事件派发给 ChannelHandler ；3.安排进一步的动作。
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            //(2) 创建ServerBootstrap
            ServerBootstrap b = new ServerBootstrap();
            b.group(group)
                //(3) 指定所使用的 NIO 传输 Channel
                .channel(NioServerSocketChannel.class)
                //(4) 使用指定的端口设置套接字地址
                .localAddress(new InetSocketAddress("127.0.0.1",port))
                //(5) 添加一个 EchoServerHandler 到子Channel的 ChannelPipeline
                //ChannelInitializer 这个类是关键，当一个新的连接被接受时，一个新的子 Channel 将会被创建，
                //而 ChannelInitializer 将会把一个你的 EchoServerHandler 的实例添加到该 Channel 的
                //ChannelPipeline 中。正如我们之前所解释的，这个 ChannelHandler 将会收到有关入站消息的通知。
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        //EchoServerHandler 被标注为@Shareable，所以我们可以总是使用同样的实例
                        //这里对于所有的客户端连接来说，都会使用同一个 EchoServerHandler，因为其被标注为@Sharable，
                        //这将在后面的章节中讲到。
                        ch.pipeline().addLast(serverHandler);
                    }
                });
            //(6) 异步地绑定服务器；调用 sync()方法阻塞等待直到绑定完成
            ChannelFuture f = b.bind().sync();
            System.out.println(EchoServer.class.getName() +
                " started and listening for connections on " + f.channel().localAddress());
            //(7) 获取 Channel 的CloseFuture，并且阻塞当前线程直到它完成
            f.channel().closeFuture().sync();
        } finally {
            //(8) 关闭 EventLoopGroup，释放所有的资源
            group.shutdownGracefully().sync();
        }
    }
}
