package com.opu.opube.exception;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiErrorResponses {
    ErrorCode[] value();
}
