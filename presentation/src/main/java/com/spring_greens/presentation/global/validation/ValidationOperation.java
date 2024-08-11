package com.spring_greens.presentation.global.validation;

/**
 * ValidationOperation is basic validation operation. <br>
 * so, if you need to validate whatever, you extends below interface.<br>
 * @author itstime0809
 */
public interface ValidationOperation<T> {
    void validate(T object);

}
