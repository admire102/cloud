package com.igniubi.core.aysnc.model;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;

@Data
public class InvokeSignature {
    private Method method;
    private Object[] args;

    public InvokeSignature(Method method, Object[] args) {
        this.method = method;
        this.args = args;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvokeSignature that = (InvokeSignature) o;
        return Objects.deepEquals(method, that.method) &&
                Arrays.deepEquals(args, that.args);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(method);
        result = 31 * result + Arrays.hashCode(args);
        return result;
    }
}
