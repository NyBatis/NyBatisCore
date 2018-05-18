package org.nybatis.core.db.datasource.proxy.bci;

import java.io.IOException;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.UnmodifiableClassException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javassist.*;
import org.nybatis.core.exception.unchecked.BaseRuntimeException;
import org.nybatis.core.exception.unchecked.ClassCastingException;
import org.nybatis.core.reflection.core.RedefineClassAgent;

/**
 * Connection Modifier
 *
 * it modify connection via Bytecode injection
 *
 * @author nayasis@gmail.com
 * @since 2018-05-17
 */
public class ConnectionModifier {

    private Set<String>                        modifiedChecker = new HashSet<>();
    private Map<Connection,ConnectionResource> resources       = new HashMap<>();

    public static ConnectionModifier $ = new ConnectionModifier();
    private ConnectionModifier() {}

    public void modify( Connection connection ) {

        if( connection == null ) return;

        try {
            injectBytecode( connection );
        } catch( NotFoundException | ClassNotFoundException | UnmodifiableClassException e ) {
            throw new ClassCastingException( e );
        } catch( CannotCompileException | IOException | RedefineClassAgent.FailedToLoadAgentException e ) {
            throw new BaseRuntimeException( e );
        }

        resources.put( connection, new ConnectionResource() );

    }

    private synchronized void injectBytecode( Connection connection ) throws NotFoundException, CannotCompileException, IOException, UnmodifiableClassException, ClassNotFoundException, RedefineClassAgent.FailedToLoadAgentException {

        if( isModified(connection) ) return;

        ClassPool classPool = ClassPool.getDefault();
        classPool.appendClassPath( new LoaderClassPath( getClass().getClassLoader() ) );

        classPool.importPackage( "org.nybatis.core.db.datasource.proxy.bci.*" );

        CtClass klass = classPool.get( connection.getClass().getName() );
        klass.stopPruning( true );

        if( klass.isFrozen() ) {
            klass.defrost();
        }

        for( CtMethod method : klass.getDeclaredMethods() ) {

            StringBuilder sb = new StringBuilder();
            sb.append( "{" );
            sb.append( "org.nybatis.core.db.datasource.proxy.bci.ConnectionResource resource = org.nybatis.core.db.datasource.proxy.bci.ConnectionModifier.$.getResource(this);" );
            sb.append( "resource.resetLastUsedTime();" );

            switch( method.getName() ) {
                case "close" :
                    sb.append( "resource.releasePool();" );
                    sb.append( "resource.executeRunnable();" );
                    break;
                case "rollback" :
                    sb.append( "resource.releasePool();" );
                    break;
                case "createStatement":
                case "prepareStatement":
                case "prepareAutoCloseStatement":
                case "prepareCall":
                    sb.append( "resource.invoke($_);" );
                    break;
                case "releaseSavepoint":
                    sb.append( "if( $args != null && $args[0] == ConnectionResource.RELEASE_RESOURCE )" );
                    sb.append( "resource.releasePool();" );
                    break;
            }
            sb.append( "}" );

            method.insertAfter( sb.toString() );

        }

        ClassDefinition definition = new ClassDefinition( connection.getClass(), klass.toBytecode() );
        RedefineClassAgent.redefineClasses( definition );

    }

    private boolean isModified( Connection connection ) {
        return modifiedChecker.contains( connection.getClass().getName() );
    }

    public ConnectionResource getResource( Connection connection ) {
        return resources.get( connection );
    }

    public void removeResource( Connection connection ) {
        if( resources.containsKey(connection) ) {
            resources.get( connection ).releasePool();
            resources.remove( connection );
        }
    }

}
