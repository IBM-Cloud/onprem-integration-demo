package org.vaadin.presentation.views;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/*
 * A qualifier for CDI events used to decouple CustomerForm from the CustomerView
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD,PARAMETER})
public abstract @interface CustomerEvent {
    
    Type value();

    public enum Type {
        SAVE, DELETE, REFRESH
    }
}

