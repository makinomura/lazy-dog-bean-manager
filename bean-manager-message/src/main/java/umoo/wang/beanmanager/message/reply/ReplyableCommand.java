package umoo.wang.beanmanager.message.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import umoo.wang.beanmanager.message.Command;

import java.util.function.BiConsumer;

/**
 * Created by yuanchen on 2019/01/15.
 */
@Data
@AllArgsConstructor
public class ReplyableCommand<T, R> {
	private Command<T> command;

	private BiConsumer<Boolean, R> callback;
}
