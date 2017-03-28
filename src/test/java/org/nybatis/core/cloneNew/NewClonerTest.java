package org.nybatis.core.cloneNew;

import org.nybatis.core.clone.vo.Product;
import org.nybatis.core.clone.vo.ProductMeta;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-29
 */
public class NewClonerTest {


    @Test
    public void test() {

        NewCloner cloner = new NewCloner();

        Map valueReference = new HashMap<>();

        Product product1 = getSample();
        Product product2 = cloner.cloneObject( product1, valueReference );

        product1.setProdName( "Changed book name" );

        System.out.println( product1 );
        System.out.println( product2 );

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