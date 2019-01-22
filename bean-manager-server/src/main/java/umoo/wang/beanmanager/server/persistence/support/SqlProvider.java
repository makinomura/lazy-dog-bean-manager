package umoo.wang.beanmanager.server.persistence.support;

import org.apache.ibatis.builder.annotation.ProviderContext;
import umoo.wang.beanmanager.common.exception.ServerException;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuanchen on 2019/01/22.
 */
@SuppressWarnings("unchecked")
public class SqlProvider {
	private static Map<Class<?>, Class<?>> mapper2EntityClazz = new HashMap<>();

	public static String selectOne(Object pk, ProviderContext context) {
		return getSqlSupport(context).buildSelectOneSql(pk);
	}

	public static String selectCount(Object entity, ProviderContext context) {
		return getSqlSupport(context).buildSelectCountSql(entity);
	}

	public static String listAll(ProviderContext context) {
		return getSqlSupport(context).buildSelectSql(null);
	}

	public static String list(Object entity, ProviderContext context) {
		return getSqlSupport(context).buildSelectSql(entity);
	}

	// TODO 主键回写
	public static String save(Object entity, ProviderContext context) {
		EntitySQLSupport sqlSupport = getSqlSupport(context);

		if (sqlSupport.isPkNull(entity)) {
			return sqlSupport.buildInsertSql(entity, true);
		} else {
			return sqlSupport.buildUpdateSql(entity, true);
		}
	}

	public static String delete(Object pk, ProviderContext context) {
		return getSqlSupport(context).buildDeleteByPrimaryKeySql(pk);
	}

	private static EntitySQLSupport getSqlSupport(ProviderContext context) {
		Class<?> entityClazz = resolveEntityClazz(context.getMapperType());
		return EntitySQLSupport.of(entityClazz);
	}

	private static Class<?> resolveEntityClazz(Class<?> mapperClazz) {
		return mapper2EntityClazz.computeIfAbsent(mapperClazz, key -> {
			try {
				return (Class<?>) ((ParameterizedType) mapperClazz
						.getGenericInterfaces()[0]).getActualTypeArguments()[1];
			} catch (Exception e) {
				throw new ServerException(
						"CustomMapper must directly extends Mapper<PK, E extends PrimaryKey<PK>>",
						e);
			}
		});
	}
}
