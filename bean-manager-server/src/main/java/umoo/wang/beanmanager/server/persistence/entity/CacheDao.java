package umoo.wang.beanmanager.server.persistence.entity;

import umoo.wang.beanmanager.common.cache.Cache;
import umoo.wang.beanmanager.common.cache.GenericLocalCache;
import umoo.wang.beanmanager.server.persistence.transaction.DelegateTransactionFactory;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/21.
 */
public class CacheDao<PK, E extends PrimaryKey<PK>> implements Dao<PK, E> {

	// TODO 事务异常回滚，缓存处理
	private Cache<PK, E> cache = new GenericLocalCache<>();

	private Dao<PK, E> delegate;

	public CacheDao(Dao<PK, E> delegate) {
		this.delegate = delegate;
	}

	@Override
	public E selectOne(PK pk) {
		return cache.get(pk, key -> delegate.selectOne(pk));
	}

	@Override
	public List<E> listAll() {
		return null;
	}

	@Override
	public List<E> list(E entity) {
		return null;
	}

	@Override
	public void save(E entity) {
		delegate.save(entity);

		DelegateTransactionFactory.getCurrentTransaction()
				.registerCallbackAfterCommit(() -> {
					cache.set(entity.getPrimaryKey(), entity);
				});
	}

	@Override
	public void delete(PK pk) {
		delegate.delete(pk);

		DelegateTransactionFactory.getCurrentTransaction()
				.registerCallbackAfterCommit(() -> {
					cache.delete(pk);
				});
	}
}
