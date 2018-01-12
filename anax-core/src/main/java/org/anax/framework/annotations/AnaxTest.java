package org.anax.framework.annotations;


import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnaxTest {

    String[] value() default { "Anax Default Suite" };

    long priority() default 0;

}
