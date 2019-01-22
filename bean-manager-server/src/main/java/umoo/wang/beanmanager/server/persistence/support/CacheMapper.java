package umoo.wang.beanmanager.server.persistence.support;

import umoo.wang.beanmanager.common.cache.Cache;
import umoo.wang.beanmanager.common.cache.GenericLocalCache;
import umoo.wang.beanmanager.server.persistence.transaction.DelegateTransactionFactory;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/21.
 */
public class CacheMapper<PK, E extends PrimaryKey<PK>>
		implements Mapper<PK, E> {

	// TODO 事务异常回滚，缓存处理
	private Cache<PK, E> cache = new GenericLocalCache<>();

	private Mapper<PK, E> delegate;

	public CacheMapper(Mapper<PK, E> delegate) {
		this.delegate = delegate;
	}

	@Override
	public E selectOne(PK pk) {
		return cache.get(pk, key -> delegate.selectOne(pk));
	}

	@Override
	public long selectCount(E entity) {
		return delegate.selectCount(entity);
	}

	@Override
	public List<E> listAll() {
		return delegate.listAll();
	}

	@Override
	public List<E> list(E entity) {
		return delegate.list(entity);
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
