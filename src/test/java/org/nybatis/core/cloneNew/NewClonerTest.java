package org.nybatis.core.cloneNew;

import com.rits.cloning.Cloner;
import org.nybatis.core.clone.NewCloner;
import org.nybatis.core.clone.vo.Product;
import org.nybatis.core.clone.vo.ProductMeta;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-29
 */
public class NewClonerTest {


    @Test
    public void test() {

        NewCloner cloner = new NewCloner();
        Cloner originalCloner = new Cloner();

        Product product1 = getSample();
        Product product2 = originalCloner.deepClone( product1 );
        Product product3 = originalCloner.shallowClone( product1 );
        Product product4 = cloner.deepClone( product1 );
        Product product5 = cloner.shallowClone( product1 );

        product1.getMeta().getAuthor().set( 0, "Not Jim !!" );


        System.out.println( product1 );
        System.out.println( "-------------");
        System.out.println( product2 );
        System.out.println( product3 );

        System.out.println( "-------------");
        System.out.println( product4 );
        System.out.println( product5 );

    }

    @Test
    public void booleanTest() {

        System.out.println( true ^ true );
        System.out.println( true ^ false );
        System.out.println( false ^ true );
        System.out.println( false ^ false );

    }

    private Product getSample() {

        Product product = new Product();

        ProductMeta meta = new ProductMeta();

        product.setProdId( "001" );
        product.setProdName( "Book" );
        product.setPrice( 1000 );

        meta.setAuthor( Arrays.asList("Jim", "Gray", "Nayasis") );
        meta.setDescription( "simple description" );

        product.setMeta( meta );

        return product;

    }

}