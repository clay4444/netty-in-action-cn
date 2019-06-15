package nia.chapter6;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 代码清单 6-12 基本的入站异常处理
 *
 * @author <a href="mailto:norman.maurer@gmail.com">Norman Maurer</a>
 *
 * ChannelHandler.exceptionCaught() 的默认实现是简单地将当前异常转发给 ChannelPipeline 中的下一个 ChannelHandler；
 * 要想定义自定义的处理逻辑，你需要重写 exceptionCaught() 方法。然后你需要决定是否需要将该异常传播出去。
 */
public class InboundExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx,
        Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
