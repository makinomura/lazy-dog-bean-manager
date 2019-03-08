package umoo.wang.beanmanager.persistence;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.cursor.Cursor;
import org.apache.ibatis.executor.BatchResult;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import umoo.wang.beanmanager.persistence.support.DynamicMapperCreator;
import umoo.wang.beanmanager.persistence.support.Mapper;
import umoo.wang.beanmanager.persistence.support.PrimaryKey;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/24. 在获取Mapper时如果没有Mapper就动态添加
 * 
 * @see DelegateSqlSession#getMapper
 */
@Slf4j
public class DelegateSqlSession implements SqlSession {
	private SqlSession delegate;

	private DynamicMapperCreator mapperCreator;

	private List<Runnable> afterCommitRunners = new ArrayList<>();

	public DelegateSqlSession(SqlSession delegate,
			DynamicMapperCreator mapperCreator) {
		this.delegate = delegate;
		this.mapperCreator = mapperCreator;
	}

	public void registerCallbackAfterCommit(Runnable runnable) {
		afterCommitRunners.add(runnable);
	}

	private void runAfterCommitRunners() {
		Iterator<Runnable> iterator = afterCommitRunners.iterator();
		while (iterator.hasNext()) {
			Runnable runnable = iterator.next();

			try {
				runnable.run();
			} catch (Exception e) {
				log.error("afterCommitRunners", e);
			}

			iterator.remove();
		}
	}

	@SuppressWarnings("unchecked")
	public <PK, E extends PrimaryKey<PK>, M extends Mapper<PK, E>> M getMapperWithEntityClazz(
			Class<E> clazz) {

		Class<?> mapperClazz = mapperCreator.getOrCreateMapperClazz(clazz);
		return (M) getMapper(mapperClazz);
	}

	@Override
	public <T> T selectOne(String statement) {
		return delegate.selectOne(statement);
	}

	@Override
	public <T> T selectOne(String statement, Object parameter) {
		return delegate.selectOne(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement) {
		return delegate.selectList(statement);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter) {
		return delegate.selectList(statement, parameter);
	}

	@Override
	public <E> List<E> selectList(String statement, Object parameter,
			RowBounds rowBounds) {
		return delegate.selectList(statement, parameter, rowBounds);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, String mapKey) {
		return delegate.selectMap(statement, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter,
			String mapKey) {
		return delegate.selectMap(statement, parameter, mapKey);
	}

	@Override
	public <K, V> Map<K, V> selectMap(String statement, Object parameter,
			String mapKey, RowBounds rowBounds) {
		return delegate.selectMap(statement, parameter, mapKey, rowBounds);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement) {
		return delegate.selectCursor(statement);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement, Object parameter) {
		return delegate.selectCursor(statement, parameter);
	}

	@Override
	public <T> Cursor<T> selectCursor(String statement, Object parameter,
			RowBounds rowBounds) {
		return delegate.selectCursor(statement, parameter, rowBounds);
	}

	@Override
	public void select(String statement, Object parameter,
			ResultHandler handler) {
		delegate.select(statement, parameter, handler);
	}

	@Override
	public void select(String statement, ResultHandler handler) {
		delegate.select(statement, handler);
	}

	@Override
	public void select(String statement, Object parameter, RowBounds rowBounds,
			ResultHandler handler) {
		delegate.select(statement, parameter, rowBounds, handler);
	}

	@Override
	public int insert(String statement) {
		return delegate.insert(statement);
	}

	@Override
	public int insert(String statement, Object parameter) {
		return delegate.insert(statement, parameter);
	}

	@Override
	public int update(String statement) {
		return delegate.update(statement);
	}

	@Override
	public int update(String statement, Object parameter) {
		return delegate.update(statement, parameter);
	}

	@Override
	public int delete(String statement) {
		return delegate.delete(statement);
	}

	@Override
	public int delete(String statement, Object parameter) {
		return delegate.delete(statement, parameter);
	}

	@Override
	public void commit() {
		commit(false);
	}

	@Override
	public void commit(boolean force) {
		delegate.commit(force);
		runAfterCommitRunners();
	}

	@Override
	public void rollback() {
		delegate.rollback();
	}

	@Override
	public void rollback(boolean force) {
		delegate.rollback(force);
	}

	@Override
	public List<BatchResult> flushStatements() {
		return delegate.flushStatements();
	}

	@Override
	public void close() {
		delegate.close();
	}

	@Override
	public void clearCache() {
		delegate.clearCache();
	}

	@Override
	public Configuration getConfiguration() {
		return delegate.getConfiguration();
	}

	@Override
	public <T> T getMapper(Class<T> type) {
		MapperRegistry mapperRegistry = delegate.getConfiguration()
				.getMapperRegistry();

		if (!mapperRegistry.hasMapper(type)) {
			mapperRegistry.addMapper(type);
		}
		return delegate.getConfiguration().getMapper(type, this);
	}

	@Override
	public Connection getConnection() {
		return delegate.getConnection();
	}
}
