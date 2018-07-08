package org.jtv.uned;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Could not obtain grades")
class GradesObtainingException extends RuntimeException {

    GradesObtainingException(final Throwable cause) {
        super(cause);
    }
}
