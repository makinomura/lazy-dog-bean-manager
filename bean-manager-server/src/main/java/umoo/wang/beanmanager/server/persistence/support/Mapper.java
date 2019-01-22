package umoo.wang.beanmanager.server.persistence.support;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/21.
 */
public interface Mapper<PK, E extends PrimaryKey<PK>> {

	@SelectProvider(type = SqlProvider.class, method = "selectOne")
	E selectOne(PK pk);

	@SelectProvider(type = SqlProvider.class, method = "selectCount")
	long selectCount(E entity);

	@SelectProvider(type = SqlProvider.class, method = "listAll")
	List<E> listAll();

	@SelectProvider(type = SqlProvider.class, method = "list")
	List<E> list(E entity);

	@InsertProvider(type = SqlProvider.class, method = "save")
	void save(E entity);

	@DeleteProvider(type = SqlProvider.class, method = "delete")
	void delete(PK pk);
}
