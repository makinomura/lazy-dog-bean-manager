package umoo.wang.beanmanager.message.reply;

import lombok.AllArgsConstructor;
import lombok.Data;
import umoo.wang.beanmanager.message.Command;

import java.util.function.BiConsumer;

/**
 * Created by yuanchen on 2019/01/15. 需要回复的消息
 */
@Data
@AllArgsConstructor
public class ReplyableCommand<T, R> {
	// 消息体
	private Command<T> command;

	// 回调
	private BiConsumer<Boolean, R> callback;
}
