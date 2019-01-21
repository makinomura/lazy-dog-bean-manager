package umoo.wang.beanmanager.server.persistence.transaction;

import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.apache.ibatis.transaction.TransactionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created by yuanchen on 2019/01/21.
 */
public class DelegateTransactionFactory implements TransactionFactory {
	private static ThreadLocal<DelegateTransaction> transactions = new ThreadLocal<>();

	private TransactionFactory delegate;

	public DelegateTransactionFactory(TransactionFactory delegate) {
		this.delegate = delegate;
	}

	public static DelegateTransaction getCurrentTransaction() {
		return transactions.get();
	}

	@Override
	public void setProperties(Properties props) {
		delegate.setProperties(props);
	}

	@Override
	public Transaction newTransaction(Connection conn) {
		Transaction transaction = delegate.newTransaction(conn);

		DelegateTransaction delegateTransaction = new DelegateTransaction(
				transaction);
		transactions.set(delegateTransaction);
		return delegateTransaction;
	}

	@Override
	public Transaction newTransaction(DataSource ds,
			TransactionIsolationLevel level, boolean autoCommit) {
		Transaction transaction = delegate.newTransaction(ds, level,
				autoCommit);
		DelegateTransaction delegateTransaction = new DelegateTransaction(
				transaction);
		transactions.set(delegateTransaction);
		return delegateTransaction;
	}
}
