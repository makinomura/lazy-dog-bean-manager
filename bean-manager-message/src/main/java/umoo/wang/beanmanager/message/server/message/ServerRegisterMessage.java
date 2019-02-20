package umoo.wang.beanmanager.message.server.message;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by yuanchen on 2019/01/23.
 */
@Data
@AllArgsConstructor
public class ServerRegisterMessage {
	private String appName;
	private String environmentName;
}
