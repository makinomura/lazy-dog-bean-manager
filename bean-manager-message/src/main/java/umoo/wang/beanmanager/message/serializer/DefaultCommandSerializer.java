package umoo.wang.beanmanager.message.serializer;

import com.alibaba.fastjson.JSON;
import umoo.wang.beanmanager.message.Command;
import umoo.wang.beanmanager.message.CommandTargetEnum;
import umoo.wang.beanmanager.message.clientcommand.ClientCommandTypeEnum;
import umoo.wang.beanmanager.message.util.EnumUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * Created by yuanchen on 2019/01/14.
 */
public class DefaultCommandSerializer implements CommandSerializer {

	public final static DefaultCommandSerializer instance = new DefaultCommandSerializer();

	private final Charset charset = StandardCharsets.UTF_8;

	@Override
	public byte[] serialize(Command command) {
		return String
				.format("%s|%s|%s", command.getCommandTarget(),
						command.getCommandType(),
						JSON.toJSONString(command.getCommandObject()))
				.getBytes(charset);
	}

	@Override
	public Command deserialize(byte[] bytes) {
		try {
			String src = new String(bytes, charset);

			String[] part = src.split("\\|");

			int commandTarget = Integer.parseInt(part[0]);
			int commandType = Integer.parseInt(part[1]);
			String commandObject = src
					.substring((part[0] + "|" + part[1] + "|").length());

			CommandTargetEnum commandTargetEnum = EnumUtil
					.valueOf(commandTarget, CommandTargetEnum.class);

			String commandObjectClazz = "";

			if (commandTargetEnum != null) {
				switch (commandTargetEnum) {
				case CLIENT:
					ClientCommandTypeEnum clientCommandTypeEnum = EnumUtil
							.valueOf(commandType, ClientCommandTypeEnum.class);

					if (clientCommandTypeEnum != null) {
						commandObjectClazz = clientCommandTypeEnum.clazz();
					}

					break;
				case SERVER:// TODO
					break;
				default:
					break;
				}
			}

			if (!Objects.equals(commandObject, "")) {
				Object o = JSON.parseObject(commandObject,
						Class.forName(commandObjectClazz));
				return new Command<>(commandTarget, commandType, o);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}
}
