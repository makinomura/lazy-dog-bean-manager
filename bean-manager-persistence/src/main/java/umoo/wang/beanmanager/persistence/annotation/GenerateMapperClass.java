package umoo.wang.beanmanager.persistence.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yuanchen on 2019/01/28.
 * 在实体类上使用此注解将自动在编译时生成对应的Mapper文件，实体类必须实现PrimaryKey<PK>接口
 * 
 * @see MapperAnnotationProcessor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { ElementType.TYPE })
@Documented
public @interface GenerateMapperClass {
}
