package org.nybatis.core.reflection.core;

import org.nybatis.core.log.NLogger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
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

    private final ConcurrentHashMap<Class,ClassNode>          cacheClassNode   = new ConcurrentHashMap<>();
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
            ClassNode classNode = getClassNode( method );
            if( classNode == null ) {
                cacheMethod.putIfAbsent( method, getNamesByReflection(method) );
            } else {
                cacheMethod.putIfAbsent( method, getNamesByAsm(method, classNode ) );
            }
        }
        return cacheMethod.get( method );
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

    private List<String> getNamesByReflection( Constructor constructor ) {
        List<String> names = new ArrayList<>();
        for( Parameter parameter : constructor.getParameters() ) {
            names.add( parameter.getName() );
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
            ClassNode classNode = getClassNode( constructor );
            if( classNode == null ) {
                cacheConstructor.putIfAbsent( constructor, getNamesByReflection(constructor) );
            } else {
                cacheConstructor.putIfAbsent( constructor, getNamesByAsm(constructor, classNode ) );
            }
        }
        return cacheConstructor.get( constructor );
    }

    private List<String> getNamesByAsm( Constructor constructor, ClassNode classNode ) {

        List<String> names           = new ArrayList<>();
        MethodNode   constructorNode = getConstructorNode( constructor, classNode );

        if( constructorNode == null ) {
            return getNamesByReflection( constructor );
        }

        for( LocalVariableNode variableNode : (List<LocalVariableNode>) constructorNode.localVariables ) {
            names.add( variableNode.name );
        }

        return names;

    }

    private MethodNode getConstructorNode( Constructor constructor, ClassNode classNode ) {
        String descriptor = Type.getConstructorDescriptor( constructor );
        for( MethodNode methodNode : (List<MethodNode>) classNode.methods ) {
            // ASM parse constructor as method and name it to "<init>"
            if( "<init>".equals(methodNode.name) && methodNode.desc.equals(descriptor) ) return methodNode;
        }
        return null;
    }

    private ClassNode getClassNode( Method method ) {
        Class klass = method.getDeclaringClass();
        return getClassNode( klass );
    }

    private ClassNode getClassNode( Constructor constructor ) {
        Class klass = constructor.getDeclaringClass();
        return getClassNode( klass );
    }

    private ClassNode getClassNode( Class klass ) {

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
