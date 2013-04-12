package com.google.code.siren4j.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SirenSubEntity {
   String name() default "";
   String[] rel() default {};
   String uri() default "";
   boolean embeddedLink() default false;
   SirenLink[] links() default {};
   SirenAction[] actions() default {};   
}
