package umoo.wang.beanmanager.persistence;

import lombok.Data;
import umoo.wang.beanmanager.common.beanfactory.Bean;
import umoo.wang.beanmanager.common.beanfactory.Conf;

/**
 * Created by yuanchen on 2019/01/20.
 */
@Bean
@Data
public class JdbcConfig {
	private static JdbcConfig cache = null;
	@Conf(key = "jdbc.driver")
	private String driver;
	@Conf(key = "jdbc.url")
	private String url;
	@Conf(key = "jdbc.username")
	private String username;
	@Conf(key = "jdbc.password")
	private String password;
}
