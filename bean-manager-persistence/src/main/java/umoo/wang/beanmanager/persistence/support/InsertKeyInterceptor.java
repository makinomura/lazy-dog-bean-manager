package umoo.wang.beanmanager.persistence.support;

import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.scripting.xmltags.XMLScriptBuilder;
import org.apache.ibatis.session.Configuration;

import java.lang.reflect.Field;
import java.util.Properties;

/**
 * Created by yuanchen on 2019/01/22.
 */
@Intercepts(value = {
		@Signature(type = Executor.class, method = "update", args = {
				MappedStatement.class, Object.class }) })
public class InsertKeyInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		Object[] args = invocation.getArgs();
		MappedStatement statement = (MappedStatement) args[0];

		// 支持useGeneratedKeys 以便回写主键
		if (statement.getSqlCommandType() == SqlCommandType.INSERT
				&& statement.getSqlSource() instanceof ProviderSqlSource) {
			Configuration configuration = statement.getConfiguration();

			String sql = statement.getBoundSql(args[1]).getSql();

			// 暂定主键回写id字段 后续可根据EntitySqlSupport
			// ProviderSqlSource.providerContext.mapperType 获取pkName
			// 需要使用多次反射可能影响性能 暂不处理
			String script = String.format(
					"<insert useGeneratedKeys=\"true\" keyProperty=\"id\">%s</insert>",
					sql);
			XPathParser parser = new XPathParser(script, false,
					configuration.getVariables(),
					new XMLMapperEntityResolver());

			XMLScriptBuilder builder = new XMLScriptBuilder(configuration,
					parser.evalNode("/insert"), null);
			SqlSource sqlSource = builder.parseScriptNode();

			Field sqlSourceField = MappedStatement.class
					.getDeclaredField("sqlSource");

			sqlSourceField.setAccessible(true);
			sqlSourceField.set(statement, sqlSource);
		}

		return invocation.proceed();
	}

	@Override
	public Object plugin(Object obj) {
		return Plugin.wrap(obj, this);
	}

	@Override
	public void setProperties(Properties arg0) {
	}
}
