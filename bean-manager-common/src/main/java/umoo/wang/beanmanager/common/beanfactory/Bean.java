package umoo.wang.beanmanager.common.beanfactory;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuanchen on 2019/02/15. 在类上使用将被扫描
 *
 * @see InjectBeanFactory
 */

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
}
