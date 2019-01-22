package umoo.wang.beanmanager.server.persistence.transaction;

import org.apache.ibatis.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yuanchen on 2019/01/21.
 */
public class DelegateTransaction implements Transaction {

	private final static Logger logger = LoggerFactory
			.getLogger(DelegateTransaction.class);

	private Transaction delegate;

	private List<Runnable> afterCommitRunners = new ArrayList<>();

	public DelegateTransaction(Transaction transaction) {
		this.delegate = transaction;
	}

	public void registerCallbackAfterCommit(Runnable runnable) {
		afterCommitRunners.add(runnable);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return delegate.getConnection();
	}

	@Override
	public void commit() throws SQLException {
		delegate.commit();

		Iterator<Runnable> iterator = afterCommitRunners.iterator();
		while (iterator.hasNext()) {
			Runnable runnable = iterator.next();

			try {
				runnable.run();
			} catch (Exception e) {
				logger.error("afterCommitRunners", e);
			}

			iterator.remove();
		}
	}

	@Override
	public void rollback() throws SQLException {
		delegate.rollback();
	}

	@Override
	public void close() throws SQLException {
		delegate.close();
	}

	@Override
	public Integer getTimeout() throws SQLException {
		return delegate.getTimeout();
	}
}
