package org.nybatis.core.reflection.mapper;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * Created by nayasis@gmail.com on 2015-07-13.
 */
public class NInvocationHandler<T> implements InvocationHandler {

    private T               originalInstance;
    private MethodInvocator methodInvocator;

    public NInvocationHandler( T instance, MethodInvocator methodInvocator  ) {
        this.originalInstance = instance;
        this.methodInvocator  = methodInvocator;
    }

    public T getOriginalInstance() {
        return originalInstance;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] arguments ) throws Throwable {
        try {
            return methodInvocator.invoke( proxy, method, arguments );
        } catch ( Throwable e ) {
            throw unwrapInvokeThrowable( e );
        }
    }

    private Throwable unwrapInvokeThrowable( Throwable throwable ) {

        Throwable unwrappedThrowable = throwable;

        while (true) {

            if ( unwrappedThrowable instanceof InvocationTargetException) {
                unwrappedThrowable = ((InvocationTargetException) unwrappedThrowable).getTargetException();

            } else if ( unwrappedThrowable instanceof UndeclaredThrowableException) {
                unwrappedThrowable = ((UndeclaredThrowableException) unwrappedThrowable).getUndeclaredThrowable();

            } else {
                return unwrappedThrowable;
            }

        }

    }

}
