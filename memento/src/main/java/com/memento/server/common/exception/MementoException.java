package com.memento.server.common.exception;

import com.memento.server.common.error.ErrorCode;

import lombok.Getter;

@Getter
public class MementoException extends RuntimeException {
  private final ErrorCode errorCode;

  public MementoException(final ErrorCode errorCode) {
    this.errorCode = errorCode;
  }
}
