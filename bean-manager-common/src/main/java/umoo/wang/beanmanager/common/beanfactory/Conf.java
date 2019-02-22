package umoo.wang.beanmanager.common.beanfactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuanchen on 2019/01/30. 在字段上使用注解将自动注入配置文件值
 * 
 * @see InjectBeanFactory
 */

@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Conf {
	String DEFAULT_NONE = "\n\t\t\n\t\t\n\uE000\uE001\uE002\n\t\t\t\t\n";

	String key();

	String defaultValue() default DEFAULT_NONE;
}