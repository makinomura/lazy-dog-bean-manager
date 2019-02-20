package umoo.wang.beanmanager.client;

import java.util.List;

/**
 * Created by yuanchen on 2019/01/11.
 */
public class BeanConfig<B> {
	private String beanName;
	private Class<B> clazz;
	private B bean;
	private List<FieldConfig<?, B>> fieldConfigs;

	public BeanConfig() {
	}

	public BeanConfig(String beanName, Class<B> clazz, B bean,
			List<FieldConfig<?, B>> fieldConfigs) {
		this.beanName = beanName;
		this.clazz = clazz;
		this.bean = bean;
		this.fieldConfigs = fieldConfigs;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Class<B> getClazz() {
		return clazz;
	}

	public void setClazz(Class<B> clazz) {
		this.clazz = clazz;
	}

	public B getBean() {
		return bean;
	}

	public void setBean(B bean) {
		this.bean = bean;
	}

	public List<FieldConfig<?, B>> getFieldConfigs() {
		return fieldConfigs;
	}

	public void setFieldConfigs(List<FieldConfig<?, B>> fieldConfigs) {
		this.fieldConfigs = fieldConfigs;
	}
}
