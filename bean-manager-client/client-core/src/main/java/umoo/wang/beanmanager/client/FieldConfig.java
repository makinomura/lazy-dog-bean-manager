package umoo.wang.beanmanager.client;

import java.lang.reflect.Field;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class FieldConfig<F, B> {
	private String fieldName;
	private Class<F> clazz;
	private Field field;
	private BeanConfig<B> beanConfig;

	public FieldConfig() {
	}

	public FieldConfig(String fieldName, Class<F> clazz, Field field,
			BeanConfig<B> beanConfig) {
		this.fieldName = fieldName;
		this.clazz = clazz;
		this.field = field;
		this.beanConfig = beanConfig;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public Class<F> getClazz() {
		return clazz;
	}

	public void setClazz(Class<F> clazz) {
		this.clazz = clazz;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public BeanConfig<B> getBeanConfig() {
		return beanConfig;
	}

	public void setBeanConfig(BeanConfig<B> beanConfig) {
		this.beanConfig = beanConfig;
	}

	public void apply(F value) {
		field.setAccessible(true);
		try {
			field.set(beanConfig.getBean(), value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
