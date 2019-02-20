package umoo.wang.beanmanager.message.serializer;

import umoo.wang.beanmanager.message.Command;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

/**
 * Created by yuanchen on 2019/01/14. Command消息序列化
 */
public interface CommandSerializer {

	static CommandSerializer load() {
		Iterator<CommandSerializer> iterator = ServiceLoader
				.load(CommandSerializer.class).iterator();

		if (iterator.hasNext()) {
			return iterator.next();
		}

		return DefaultCommandSerializer.instance;
	}

	/**
	 * 序列化
	 * 
	 * @param command
	 * @return
	 */
	byte[] serialize(Command command);

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 * @return
	 */
	List<Command> deserialize(byte[] bytes);
}
