package org.nybatis.core.clone;

import org.nybatis.core.clone.vo.Product;
import org.nybatis.core.clone.vo.ProductMeta;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author nayasis@gmail.com
 * @since 2017-03-24
 */
public class CloneServiceTest {

    @Test
    public void test() {

        CloneService service = new CloneService();

        Product product = getSample();

        List<Field> fields = service.getFields( product );

        for( Field field : fields ) {
            System.out.printf( "%s : %s\n", field.getName(), service.getFieldValue(product, field) );
        }

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