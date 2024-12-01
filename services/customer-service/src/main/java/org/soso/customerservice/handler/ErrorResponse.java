package org.soso.customerservice.handler;

import java.util.Map;

public record ErrorResponse (Map<String, String> errors
) {

}
