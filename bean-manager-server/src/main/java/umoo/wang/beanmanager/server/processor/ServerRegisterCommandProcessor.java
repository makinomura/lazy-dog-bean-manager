package umoo.wang.beanmanager.server.processor;

import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import umoo.wang.beanmanager.cache.dao.RedisDao;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Inject;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandProcessor;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;
import umoo.wang.beanmanager.message.server.message.ServerRegisterMessage;
import umoo.wang.beanmanager.persistence.SqlSessionExecutor;
import umoo.wang.beanmanager.persistence.entity.App;
import umoo.wang.beanmanager.persistence.support.Mapper;
import umoo.wang.beanmanager.server.ClientManager;
import umoo.wang.beanmanager.server.ClientStatusEnum;

import java.util.List;
import java.util.Objects;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Bean
public class ServerRegisterCommandProcessor implements CommandProcessor {
	private final static Logger logger = LoggerFactory
			.getLogger(ServerRegisterCommandProcessor.class);

	@Inject
	private RedisDao redisDao;
	@Inject
	private ClientManager clientManager;
	@Inject
	private SqlSessionExecutor sqlSessionExecutor;

	@Override
	public boolean process(ChannelHandlerContext ctx, Command<?> command) {

		if (command.getCommandType() == ServerCommandTypeEnum.REGISTER
				.value()) {
			ServerRegisterMessage msg = (ServerRegisterMessage) command
					.getCommandObj();

			List<App> appList = sqlSessionExecutor.execute(true,
					delegateSqlSession -> {
						Mapper<Integer, App> appMapper = delegateSqlSession
								.getMapperWithEntityClazz(App.class);

						return appMapper.listAll();
					});

			appList.stream().filter(
					app -> Objects.equals(app.getAppName(), msg.getAppName()))
					.findAny().ifPresent(app -> {
						logger.info(
								"Client register:" + command.getCommandObj());

						// 设置Client信息
						ClientManager.ClientConfig config = clientManager
								.getConfig(ctx);

						config.setAppName(msg.getAppName());
						config.setEnvironmentName(msg.getEnvironmentName());

						// 更新Client状态
						clientManager.updateStatus(ctx,
								ClientStatusEnum.REGISTERED.value());
					});

			return true;
		}

		return false;
	}
}
