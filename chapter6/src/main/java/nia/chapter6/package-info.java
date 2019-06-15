/**
 * Created by kerr.
 *
 * 代码清单 6-1 释放消息资源 {@link nia.chapter6.DiscardHandler}
 *
 * 代码清单 6-2 使用 SimpleChannelInboundHandler {@link nia.chapter6.SimpleDiscardHandler}
 *
 * 代码清单 6-3 消费并释放入站消息 {@link nia.chapter6.DiscardInboundHandler}
 *
 * 代码清单 6-4 丢弃并释放出站消息 {@link nia.chapter6.DiscardOutboundHandler}
 *
 * 代码清单 6-5 修改 ChannelPipeline {@link nia.chapter6.ModifyChannelPipeline#modifyPipeline()}
 *
 * 代码清单 6-6 从 ChannelHandlerContext 访问 Channel {@link nia.chapter6.WriteHandlers#writeViaChannel()}
 *
 * 代码清单 6-7 通过 ChannelHandlerContext 访问 ChannelPipeline {@link nia.chapter6.WriteHandlers#writeViaChannelPipeline()}
 *
 * 代码清单 6-8 调用 ChannelHandlerContext 的 write()方法 {@link nia.chapter6.WriteHandlers#writeViaChannelHandlerContext()}
 *
 * 代码清单 6-9 缓存到 ChannelHandlerContext 的引用 {@link nia.chapter6.WriteHandler}
 *
 * 代码清单 6-10 可共享的 ChannelHandler {@link nia.chapter6.SharableHandler}
 *
 * 代码清单 6-11 @Sharable 的错误用法 {@link nia.chapter6.UnsharableHandler}
 *
 * 代码清单 6-12 基本的入站异常处理 {@link nia.chapter6.InboundExceptionHandler}
 *
 * 代码清单 6-13 添加 ChannelFutureListener 到 ChannelFuture {@link nia.chapter6.ChannelFutures#addingChannelFutureListener()}
 *
 * 代码清单 6-14 添加 ChannelFutureListener 到 ChannelPromise{@link nia.chapter6.OutboundExceptionHandler}
 *
 *
 * 笔记
 * 可以认为 ChannelPipeline 是一个拦截流经 Channel 的入站和出站事件的 ChannelHandler 实例链，
 *
 * 每一个新创建的 Channel 都将会被分配一个新的 ChannelPipeline 。这项关联是永久性的；
 * Channel 既不能附加另外一个 ChannelPipeline ，也不能分离其当前的。在 Netty 组件的生命周期中，这是一项固定的操作，不需要开发人员的任何干预。
 *
 * 根据事件的起源，事件将会被 ChannelInboundHandler 或者 ChannelOutboundHandler 处理。随后，通过调用 ChannelHandlerContext 实现，
 * 它将被转发给同一超类型的下一个 ChannelHandler 。**ChannelHandlerContext 负责将事件传递给后面的ChannelHandler**
 *
 * ChannelHandlerContext
 * ChannelHandlerContext 代表了 ChannelHandler 和 ChannelPipeline 之间的关联，
 * ChannelHandlerContext 使得 ChannelHandler 能够和它的 ChannelPipeline 以及其他的 ChannelHandler 交互。
 * ChannelHandlerContext 可以通知其所属的 ChannelPipeline 中的下一个 ChannelHandler ，甚至可以动态修改它所属的 ChannelPipeline 。
 *
 * 每当有 ChannelHandler 添加到 ChannelPipeline 中时，都会创建 ChannelHandlerContext 。
 * ChannelHandlerContext 的主要功能是管理它所关联的 ChannelHandler 和在同一个 ChannelPipeline 中的其他 ChannelHandler 之间的交互。
 *
 * ChannelHandlerContext 和 ChannelHandler 之间的关联（绑定）是永远不会改变的，所以缓存对它的引用是安全的；
 *
 * ctx.write 和 channel.write/pipeline.write 的区别是前者会从当前handler的上一个handler开始传递处理事件(会跳过一些handler，也就是从某个特定的handler开始传递消息)，
 *                                        而后者则会从pipline的尾部开始，在整个pipline上处理事件；
 *
 * ChannelHandler
 * 因为一个 ChannelHandler 可以从属于多个 ChannelPipeline ，所以它也可以绑定到多个 ChannelHandlerContext 实例，
 * 但是此时这个ChannelHandler必须是线程安全的，而且要使用 @Sharable 注解；
 * 在多个 ChannelPipeline 中安装同一个ChannelHandler的一个常见原因就是用于收集跨越多个 Channel 的统计信息；
 *
 *
 * ChannelPipline
 * Netty总是把左边定义为头部，右边定义尾端，入站的时候从头到尾。出站的时候，从尾到头；
 * 调用ChannelPipline.add(*)方法添加的ChannelHandler是按照添加的顺序从左到右排列的；
 * 通常 ChannelPipeline 中的每一个 ChannelHandler 都是通过它的 EventLoop（I/O 线程）来处理传递给它的事件的。所以至关重要的是不要阻塞这个线程，
 * ChannelPipeline 的 API 公开了用于调用入站和出站操作的附加方法，调用它的一些方法可以触发事件(fireChannelRegistered,fireChannelUnregistered...)，以调用ChannelHandler的方法
 *
 *
 */
package nia.chapter6;