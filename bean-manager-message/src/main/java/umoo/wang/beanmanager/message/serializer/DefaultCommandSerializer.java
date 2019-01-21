package umoo.wang.beanmanager.message.serializer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import umoo.wang.beanmanager.common.converter.ConverterFactory;
import umoo.wang.beanmanager.common.util.EnumUtil;
import umoo.wang.beanmanager.common.util.StringUtil;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.client.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.server.ServerCommandTypeEnum;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * Created by yuanchen on 2019/01/14. CommandSerializer的默认实现，使用FastJSON序列化
 */
public class DefaultCommandSerializer implements CommandSerializer {

	public final static DefaultCommandSerializer instance = new DefaultCommandSerializer();

	private final Charset charset = StandardCharsets.UTF_8;

	@Override
	public byte[] serialize(Command command) {
		return JSON.toJSONString(command).getBytes(charset);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Command deserialize(byte[] bytes) {
		Command command = JSON.parseObject(new String(bytes, charset),
				new TypeReference<Command<String>>() {
				});

		int commandTarget = command.getCommandTarget();
		int commandType = command.getCommandType();

		CommandTargetEnum commandTargetEnum = EnumUtil.valueOf(commandTarget,
				CommandTargetEnum.class);

		String commandObjectClazz = "";

		// 根据消息类型获取实体类型
		if (commandTargetEnum != null) {
			switch (commandTargetEnum) {
			case CLIENT:
				ClientCommandTypeEnum clientCommandTypeEnum = EnumUtil
						.valueOf(commandType, ClientCommandTypeEnum.class);

				if (clientCommandTypeEnum != null) {
					commandObjectClazz = clientCommandTypeEnum.clazz();
				}

				break;
			case SERVER:
				ServerCommandTypeEnum serverCommandTypeEnum = EnumUtil
						.valueOf(commandType, ServerCommandTypeEnum.class);

				if (serverCommandTypeEnum != null) {
					commandObjectClazz = serverCommandTypeEnum.clazz();
				}
				break;
			default:
				break;
			}
		}

		// 反序列化实体
		if (!StringUtil.isNullOrEmpty(commandObjectClazz)) {
			try {
				Class<?> objClazz = Class.forName(commandObjectClazz);
				Object commandObj = ConverterFactory.withType(objClazz)
						.convert((String) command.getCommandObj(), objClazz);
				command.setCommandObj(commandObj);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return command;
	}
}
