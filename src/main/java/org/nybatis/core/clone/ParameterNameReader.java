package org.nybatis.core.clone;

import org.nybatis.core.log.NLogger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Parameter name reader
 *
 * it use ASM library.
 *
 * @author nayasis@gmail.com
 * @since 2017-03-25
 */
public class ParameterNameReader {

    private final ConcurrentHashMap<Class,ClassNode>     cacheClassNode   = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Method,List<String>> cacheMethodNames = new ConcurrentHashMap<>();

    /**
     * Read method's parameter name
     *
     * @param method
     * @return
     */
    public List<String> read( Method method ) {

        if( ! cacheMethodNames.containsKey( method ) ) {

            if( method.getParameterCount() < 2 ) {
                cacheMethodNames.putIfAbsent( method, getNamesByReflection(method) );
            } else {
                ClassNode classNode = getClassNode( method );
                if( classNode == null ) {
                    cacheMethodNames.putIfAbsent( method, getNamesByReflection(method) );
                } else {
                    cacheMethodNames.putIfAbsent( method, getNamesByAsm(method, classNode ) );
                }
            }

        }

        return cacheMethodNames.get( method );

    }

    private List<String> getNamesByReflection( Method method ) {
        List<String> names = new ArrayList<>();
        for( Parameter parameter : method.getParameters() ) {
            names.add( parameter.getName() );
        }
        return names;
    }

    private List<String> getNamesByAsm( Method method, ClassNode classNode ) {

        List<String> names      = new ArrayList<>();
        MethodNode   methodNode = getMethodNode( method, classNode );

        if( methodNode == null ) {
            return getNamesByReflection( method );
        }

        for( LocalVariableNode variableNode : (List<LocalVariableNode>) methodNode.localVariables ) {
            // if methodNode is not static, first local variable always be "this".
            if( "this".equals(variableNode.name) ) continue;
            names.add( variableNode.name );
        }

        return names;

    }

    private MethodNode getMethodNode( Method method, ClassNode classNode ) {
        String descriptor = Type.getMethodDescriptor( method );
        for( MethodNode methodNode : (List<MethodNode>) classNode.methods ) {
            if( methodNode.name.equals(method.getName()) && methodNode.desc.equals(descriptor) ) return methodNode;
        }
        return null;
    }


    private ClassNode getClassNode( Method method ) {

        Class klass = method.getDeclaringClass();

        if( ! cacheClassNode.containsKey( klass ) ) {

            ClassLoader classLoader = klass.getClassLoader();
            Type        type        = Type.getType( klass );
            String      url         = type.getInternalName() + ".class";

            InputStream file = classLoader.getResourceAsStream( url );
            if( file == null ) {
                cacheClassNode.putIfAbsent( klass, null );
                throw new IllegalArgumentException( String.format("Class loader cannot find the bytecode of class(%s:%s).", url, klass.getName()) );
            }

            try {

                ClassNode   classNode   = new ClassNode();
                ClassReader classReader = new ClassReader( file );
                classReader.accept( classNode, 0 );

                cacheClassNode.putIfAbsent( klass, classNode );

            } catch( IOException e ) {
                cacheClassNode.putIfAbsent( klass, null );
                NLogger.error( e );

            } finally {
                try { file.close(); } catch( IOException e ) {}
            }

        }

        return cacheClassNode.get( klass );

    }

}
