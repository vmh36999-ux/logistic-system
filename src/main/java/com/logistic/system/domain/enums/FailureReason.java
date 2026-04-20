package com.logistic.system.domain.enums;

public enum FailureReason {
    CUSTOMER_NOT_AVAILABLE(true),
    CUSTOMER_REQUESTED_RESCHEDULE(true),
    INVALID_ADDRESS(false),
    CUSTOMER_REFUSED_DELIVERY(false),
    WEATHER_DELAY(true),
    SYSTEM_ISSUE(false);

    private final boolean retryable;

    FailureReason(boolean retryable) {
        this.retryable = retryable;
    }

    public boolean isRetryable() {
        return retryable;
    }
}
