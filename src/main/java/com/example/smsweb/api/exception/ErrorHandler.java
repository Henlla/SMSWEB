package com.example.smsweb.api.exception;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

public class ErrorHandler extends RuntimeException{
    public ErrorHandler(String msg) {
        super(msg);
    }
}
