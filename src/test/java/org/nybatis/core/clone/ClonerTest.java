package org.nybatis.core.clone;

import org.nybatis.core.clone.vo.Product;
import org.nybatis.core.clone.vo.ProductMeta;
import org.testng.annotations.Test;

import java.util.Arrays;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-29
 */
public class ClonerTest {


    @Test
    public void test() {

        Cloner cloner = new Cloner();

        Product product1 = getSample();
        Product product2 = cloner.deepClone( product1 );
        Product product3 = cloner.shallowClone( product1 );

        product1.getMeta().getAuthor().set( 0, "Not Jim !!" );

        System.out.println( product1 );
        System.out.println( "-------------");
        System.out.println( product2 );
        System.out.println( product3 );

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