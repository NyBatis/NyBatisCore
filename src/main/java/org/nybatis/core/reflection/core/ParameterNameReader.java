package org.nybatis.core.reflection.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javassist.*;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.objectweb.asm.Type;

/**
 * Parameter name reader
 *
 * it use ASM library.
 *
 * @author nayasis@gmail.com
 * @since 2017-03-25
 */
public class ParameterNameReader {

    private final ConcurrentHashMap<Method,List<String>>      cacheMethod      = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Constructor,List<String>> cacheConstructor = new ConcurrentHashMap<>();

    /**
     * Read method's parameter name
     *
     * @param method method to inspect parameter name
     * @return parameter names
     */
    public List<String> read( Method method ) {
        if( method == null ) new ArrayList<>();
        if( ! cacheMethod.containsKey( method ) ) {
            List<String> names = getNamesFromBytecode( method );
            if( names == null )
                         names = getNamesFromReflector( method );
            cacheMethod.putIfAbsent( method, names );
        }
        return cacheMethod.get( method );
    }

    private List<String> getNamesFromReflector( Method method ) {
        List<String> names = new ArrayList<>();
        for( Parameter parameter : method.getParameters() ) {
            names.add( parameter.getName() );
        }
        return names;
    }

    private List<String> getNamesFromBytecode( Method method ) {
        ClassPool classPool = ClassPool.getDefault();
        try {
            CtClass  klass          = classPool.get( method.getDeclaringClass().getName() );
            CtMethod declaredMethod = klass.getDeclaredMethod( method.getName() );
            return extractParameterNames( declaredMethod.getMethodInfo() );
        } catch( NotFoundException e ) {
            return null;
        }
    }

    private List<String> extractParameterNames( MethodInfo methodInfo ) {
        List<String> names = new ArrayList<>();
        LocalVariableAttribute table = (LocalVariableAttribute) methodInfo.getCodeAttribute().getAttribute( LocalVariableAttribute.tag );
        for( int i = 0, iCnt = table.tableLength(); i < iCnt; i++ ) {
            String name = methodInfo.getConstPool().getUtf8Info( table.nameIndex(i) );
            if( "this".equals( name ) ) continue;
            names.add( name );
        }
        return names;
    }

    /**
     * Read constructor's parameter name
     *
     * @param constructor constructor to inspect parameter name
     * @return parameter names
     */
    public List<String> read( Constructor constructor ) {
        if( constructor == null ) new ArrayList<>();
        if( ! cacheConstructor.containsKey( constructor ) ) {
            List<String> names = getNamesFromBytecode( constructor );
            if( names == null )
                         names = getNamesFromReflector( constructor );
            cacheConstructor.putIfAbsent( constructor, names );
        }
        return cacheConstructor.get( constructor );
    }

    private List<String> getNamesFromReflector( Constructor constructor ) {
        List<String> names = new ArrayList<>();
        for( Parameter parameter : constructor.getParameters() ) {
            names.add( parameter.getName() );
        }
        return names;
    }


    private List<String> getNamesFromBytecode( Constructor constructor ) {

        ClassPool classPool = ClassPool.getDefault();

        try {
            CtClass klass = classPool.get( constructor.getDeclaringClass().getName() );
            CtConstructor declaredConstructor = klass.getConstructor( Type.getConstructorDescriptor(constructor) );
            return extractParameterNames( declaredConstructor.getMethodInfo() );
        } catch( NotFoundException e ) {
            return null;
        }

    }

}
