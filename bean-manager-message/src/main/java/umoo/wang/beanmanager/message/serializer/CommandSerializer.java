package umoo.wang.beanmanager.message.serializer;

import umoo.wang.beanmanager.message.Command;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Created by yuanchen on 2019/01/14.
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

	byte[] serialize(Command command);

	Command deserialize(byte[] bytes);
}
