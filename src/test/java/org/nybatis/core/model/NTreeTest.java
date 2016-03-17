package org.nybatis.core.model;

import org.nybatis.core.log.NLogger;
import org.testng.annotations.Test;

public class NTreeTest {

    @Test
    public void treeTest() {
        
        NTree<String> node = getDummyTree();

        System.out.println( node );
        System.out.println( node.getLeafNodes() );
        
        node.getLeafNodes().get( 1 ).append( "희한하다~" );
        
        System.out.println( node );
        
        System.out.println( node.toNList() );

        System.out.println( node.getValues() );

    }

    @Test
    public void addTreeTest() {
        
        NTree<String> node = new NTree<String>( "ROOT" );
        
        node.append( "A" );
        node.append( "B" ).append( "1" );
        

        NTree<String> anotherNode = new NTree<String>( "1" );
        
        anotherNode.append( "가" );
        anotherNode.append( "나" ).append( "(가)" );
        
        System.out.println( node );
        System.out.println( "------------------------------" );
        System.out.println( anotherNode );
        System.out.println( "------------------------------");
        System.out.println( node.appendTree( anotherNode ) );
        System.out.println( "------------------------------");
        System.out.println( node );
        
    }

    @Test
    public void mergeTest() {
        
        NTree<String> node = new NTree<String>( "1" );
        
        node.append( "2" ).append( "3" ).append( "4" );

        NTree<String> node1 = new NTree<String>( "1" );
        
        node1.append( "5" );

        NTree<String> node2 = new NTree<String>( "3" );
        
        node2.append( "4" ).append( "5" );
        

        NTree<String> node3 = new NTree<String>( "4" );
        
        node3.append( "7" );
        
        node.connect( node1 );
        node.connect( node2 );
        node.connect( node3 );
        
        System.out.println( node );
        
    }

    @Test
    public void reverse() {

        NTree<String> dummyNode = getDummyTree();

        NLogger.debug( ">> Source node" );

        System.out.println( dummyNode );
//        System.out.println( dummyNode.clone() );

        NLogger.debug( ">> Reversed node" );

        for( NTree<String> reversedNode : dummyNode.reverse() ) {

            System.out.println( "--------------------------" );
            System.out.println( reversedNode );
            System.out.println( "--------------------------" );
            System.out.println( reversedNode.getLeafNodes().get( 0 ) );

        }

    }

    @Test
    public void toList() {

        NTree<String> dummyNode = getDummyTree();

        NLogger.debug( ">> Source node" );

        System.out.println( dummyNode );

        NLogger.debug( ">> toList" );

        for( NTree.Leaf<String> leaf : dummyNode.toList() ) {
            System.out.println( leaf );
        }


    }

    private NTree<String> getDummyTree() {

        NTree<String> node = new NTree<String>( "ROOT" );

        node.append( "A" );

        NTree<String> subNode = node.append( "B" );

        node.append( "C" );

        subNode.append( "1" );
        subNode.append( "2" ).append( "가" );
        subNode.append( "3" ).append( "가" );

        return node;

    }
    
}
