package umoo.wang.beanmanager.server.persistence.support;

import org.objectweb.asm.ClassWriter;
import umoo.wang.beanmanager.common.exception.ServerException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_INTERFACE;
import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.V1_8;

/**
 * Created by yuanchen on 2019/01/25. 动态构建Mapper Class文件并加载
 */
public class DynamicMapperFactory {
	private final static String MAPPER_INTERFACE_NAME = resolveClazzName(
			Mapper.class);
	private final static String OBJECT_CLASS_NAME = resolveClazzName(
			Object.class);
	private final static String ANONYMOUS_MAPPER_SIGNATURE = "Ljava/lang/Object;Lumoo/wang/beanmanager/server/persistence/support/Mapper<L%s;L%s;>;";
	private final static String ANONYMOUS_MAPPER_CLASS = "%s$%s";

	private Map<Class<?>, Class<?>> mappers = new HashMap<>();

	private static String resolveClazzName(Class<?> clazz) {
		return clazz.getName().replaceAll("\\.", "/");
	}

	public Class<?> getOrCreateMapperClazz(Class<?> entityClazz) {
		Class<?> mapperClazz = mappers.get(entityClazz);

		if (mapperClazz != null) {
			return mapperClazz;
		} else {
			return createMapperClazz(entityClazz);
		}
	}

	public Class<?> createMapperClazz(Class<?> entityClazz) {
		if (!PrimaryKey.class.isAssignableFrom(entityClazz)) {
			throw new ServerException(
					"Entity class must directly extends PrimaryKey.class");
		}

		try {
			Class<?> mapperClazz = buildAnonymousMapperClazz(entityClazz);

			mappers.put(entityClazz, mapperClazz);
			return mapperClazz;
		} catch (NoSuchMethodException | InvocationTargetException
				| IllegalAccessException e) {
			throw ServerException.wrap(e);
		}
	}

	/**
	 * 使用asm框架根据实体类动态创建Mapper接口类并加载到SystemClassLoader
	 * 
	 * @param entityClazz
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Class<?> buildAnonymousMapperClazz(Class<?> entityClazz)
			throws NoSuchMethodException, InvocationTargetException,
			IllegalAccessException {
		String entityName = entityClazz.getSimpleName();

		Class<?> pkClazz = (Class<?>) ((ParameterizedType) entityClazz
				.getGenericInterfaces()[0]).getActualTypeArguments()[0];

		String mapperClazzName = String.format(ANONYMOUS_MAPPER_CLASS,
				MAPPER_INTERFACE_NAME, entityName);

		String signature = String.format(ANONYMOUS_MAPPER_SIGNATURE,
				resolveClazzName(pkClazz), resolveClazzName(entityClazz));

		ClassWriter cw = new ClassWriter(COMPUTE_MAXS);

		cw.visit(V1_8, ACC_PUBLIC + ACC_INTERFACE + ACC_ABSTRACT,
				mapperClazzName, signature, OBJECT_CLASS_NAME,
				new String[] { MAPPER_INTERFACE_NAME });
		cw.visitEnd();

		byte[] bytes = cw.toByteArray();

		ClassLoader cl = ClassLoader.getSystemClassLoader();

		Method defineClassMethod = ClassLoader.class.getDeclaredMethod(
				"defineClass", String.class, byte[].class, int.class,
				int.class);

		defineClassMethod.setAccessible(true);

		return (Class<?>) defineClassMethod.invoke(cl, null, bytes, 0,
				bytes.length);
	}
}
