package com.batu.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface as a Spring Data JPA projection that should be validated
 * at compile time.
 *
 * <p>
 * The projection validator checks each method in the annotated interface
 * against the fields of the configured entity type. A projection method is
 * considered valid when its name maps to an entity field and its return type is
 * compatible with that field.
 * </p>
 *
 * <p>
 * Supported method naming styles include JavaBean accessors such as
 * {@code getName()} and {@code isActive()}, as well as field-style methods such
 * as {@code name()}.
 * </p>
 *
 * <p>
 * This annotation is intended to be used on projection interfaces.
 * </p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface JPAProjection {

    /**
     * The JPA entity class that this projection represents.
     *
     * <p>
     * The annotation processor uses this class as the source of truth when
     * validating projection method names and return types.
     * </p>
     *
     * @return the entity class backing the projection
     */
    Class<?> entity();
}