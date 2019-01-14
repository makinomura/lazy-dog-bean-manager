package umoo.wang.beanmanager.message.server.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by yuanchen on 2019/01/14.
 */
@Data
@AllArgsConstructor
public class ServerHeartBeatMessage {
	private long timestamp;
}
