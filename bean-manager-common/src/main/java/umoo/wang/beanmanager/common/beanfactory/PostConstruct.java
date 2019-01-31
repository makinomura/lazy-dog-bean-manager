package umoo.wang.beanmanager.common.beanfactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuanchen on 2019/01/30. 在方法上使用该注解将在对象注入完成后执行该方法（方法必须为0参数）
 * 
 * @see InjectBeanFactory
 */

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PostConstruct {
}