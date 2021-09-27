package com.wuying.commons;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 启动类注解
 * @author wuying
 * @since 1.0.0
 * @date 2021-09-26
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootApplication
@EnableCircuitBreaker
public @interface WuyingCloudApplication {

    /**
     * Exclude specific auto-configuration classes such that they will never be applied.
     * @return the classes to exclude
     */
    @AliasFor(annotation = EnableAutoConfiguration.class, attribute = "exclude")
    Class<?>[] exclude() default {};

    /**
     * Exclude specific auto-configuration class names such that they will never be
     * applied.
     * @return the class names to exclude
     * @since 1.3.0
     */
    @AliasFor(annotation = EnableAutoConfiguration.class, attribute = "excludeName")
    String[] excludeName() default {};

    /**
     * Base packages to scan for annotated components. Use {@link #scanBasePackageClasses}
     * for a type-safe alternative to String-based package names.
     * <p>
     * <strong>Note:</strong> this setting is an alias for
     * {@link ComponentScan @ComponentScan} only. It has no effect on {@code @Entity}
     * scanning or Spring Data {@link Repository} scanning. For those you should add
     * {@link org.springframework.boot.autoconfigure.domain.EntityScan @EntityScan} and
     * {@code @Enable...Repositories} annotations.
     * @return base packages to scan
     * @since 1.3.0
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackages")
    String[] scanBasePackages() default {};

    /**
     * Type-safe alternative to {@link #scanBasePackages} for specifying the packages to
     * scan for annotated components. The package of each class specified will be scanned.
     * <p>
     * Consider creating a special no-op marker class or interface in each package that
     * serves no purpose other than being referenced by this attribute.
     * <p>
     * <strong>Note:</strong> this setting is an alias for
     * {@link ComponentScan @ComponentScan} only. It has no effect on {@code @Entity}
     * scanning or Spring Data {@link Repository} scanning. For those you should add
     * {@link org.springframework.boot.autoconfigure.domain.EntityScan @EntityScan} and
     * {@code @Enable...Repositories} annotations.
     * @return base packages to scan
     * @since 1.3.0
     */
    @AliasFor(annotation = ComponentScan.class, attribute = "basePackageClasses")
    Class<?>[] scanBasePackageClasses() default {};
}
