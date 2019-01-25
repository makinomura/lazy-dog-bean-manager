package umoo.wang.beanmanager.server.persistence;

import org.apache.ibatis.session.SqlSession;
import umoo.wang.beanmanager.common.exception.ServerException;
import umoo.wang.beanmanager.server.persistence.support.DynamicMapperCreator;

/**
 * Created by yuanchen on 2019/01/24.
 */
public class ReadonlySqlSession extends DelegateSqlSession {
	public ReadonlySqlSession(SqlSession delegate,
			DynamicMapperCreator mapperCreator) {
		super(delegate, mapperCreator);
	}

	@Override
	public int insert(String statement) {
		return unsupported();
	}

	@Override
	public int insert(String statement, Object parameter) {
		return unsupported();
	}

	@Override
	public int update(String statement) {
		return unsupported();
	}

	@Override
	public int update(String statement, Object parameter) {
		return unsupported();
	}

	@Override
	public int delete(String statement) {
		return unsupported();
	}

	@Override
	public int delete(String statement, Object parameter) {
		return unsupported();
	}

	private int unsupported() {
		throw new ServerException("Current sql session is readonly");
	}
}
