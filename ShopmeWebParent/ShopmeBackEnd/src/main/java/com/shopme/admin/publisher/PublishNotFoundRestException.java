package com.shopme.admin.publisher;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Publisher Not Found")
public class PublishNotFoundRestException extends Exception {

}
