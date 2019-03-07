package umoo.wang.beanmanager.web.controller;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import umoo.wang.beanmanager.cache.dao.RedisDao;
import umoo.wang.beanmanager.cache.entity.ClientInfo;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.reply.ReplyableCommand;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.BeanListReqMessage;
import umoo.wang.beanmanager.web.support.AbstractRequestProcessor;
import umoo.wang.beanmanager.web.support.Mapping;
import umoo.wang.beanmanager.web.util.HttpRequestUtil;

import java.util.List;
import java.util.Map;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Bean
@Mapping(path = "/client")
public class ClientController {

	@Inject
	private RedisDao redisDao;
	@Inject
	private ChannelFuture serverChannelFuture;

	@Mapping(path = "/all")
	public List<ClientInfo> all(FullHttpRequest request) {
		return redisDao.listClientInfo();
	}

	@Mapping(path = "/beans")
	public void beans(FullHttpRequest request, ChannelHandlerContext ctx) {
		Map<String, String> queryParameters = HttpRequestUtil
				.getQueryParameters(request);

		String channelKey = queryParameters.getOrDefault("channelKey", "111");

		Command<?> requireBeanListCommand = Command.builderWithDefault()
				.commandTarget(CommandTargetEnum.SERVER.value())
				.commandType(
						ServerCommandTypeEnum.REQUIRE_CLIENT_BEAN_LIST.value())
				.commandObj(new BeanListReqMessage(channelKey)).build();

		serverChannelFuture.channel().writeAndFlush(new ReplyableCommand<>(
				requireBeanListCommand, (success, result) -> {
					ctx.writeAndFlush(AbstractRequestProcessor.json(ctx,
							request, HttpResponseStatus.OK, result))
							.addListener(ChannelFutureListener.CLOSE);
				}));
	}
}
