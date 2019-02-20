package umoo.wang.beanmanager.persistence;

import umoo.wang.beanmanager.common.PropertyResolver;

import java.util.Objects;

/**
 * Created by yuanchen on 2019/01/20.
 */
public class JdbcConfig {
	private static JdbcConfig cache = null;
	private String driver;
	private String url;
	private String username;
	private String password;

	public JdbcConfig(String driver, String url, String username,
			String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public static JdbcConfig read() {
		if (cache == null) {
			String driver = PropertyResolver.read("jdbc.driver");
			String url = PropertyResolver.read("jdbc.url");
			String username = PropertyResolver.read("jdbc.username");
			String password = PropertyResolver.read("jdbc.password");

			cache = new JdbcConfig(driver, url, username, password);
		}

		return cache;
	}

	public String getDriver() {
		return driver;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		JdbcConfig that = (JdbcConfig) o;
		return Objects.equals(driver, that.driver)
				&& Objects.equals(url, that.url)
				&& Objects.equals(username, that.username)
				&& Objects.equals(password, that.password);
	}

	@Override
	public int hashCode() {
		return Objects.hash(driver, url, username, password);
	}
}
