package umoo.wang.beanmanager.cache.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by yuanchen on 2019/02/22.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientInfo {
	private String channelKey;
	private String appName;
	private String environmentName;
}
