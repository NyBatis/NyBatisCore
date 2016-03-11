package org.nybatis.core.file.handler;

import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.file.annotation.ExcelReadAnnotationInspector;
import org.nybatis.core.file.annotation.ExcelWriteAnnotationInspector;
import org.nybatis.core.file.handler.implement.ExcelHandlerApachePoi;
import org.nybatis.core.file.vo.User;
import org.nybatis.core.file.vo.UserForExcelReader;
import org.nybatis.core.log.NLogger;
import org.nybatis.core.model.NList;
import org.nybatis.core.model.NMap;
import org.nybatis.core.reflection.Reflector;
import org.nybatis.core.reflection.mapper.NObjectMapper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel Handler test
 *
 * @author nayasis@gmail.com
 * @since 2016-03-11
 */
public class ExcelHandlerTest {

    @Test
    public void testToExcelNList() throws Exception {

        ExcelHandler excelHandler = new ExcelHandlerApachePoi();

        NList excelData = excelHandler.toExcelNListFromBean( getDummy() );

        NLogger.debug( "1. original json" );
        NLogger.debug( Reflector.toJson( getDummy() ) );

        NLogger.debug( "2. converted excel data" );
        NLogger.debug( excelData );

        User user = Reflector.toBeanFrom( "{\"name\":\"Jung Hwasu\",\"id\":\"1\"}", User.class );

        NLogger.debug( user );

        List<User> users = toBeanFromExcelNList( excelData, User.class );

        NLogger.debug( "3. excel data -> bean" );
        NLogger.debug( users );

    }

    protected <T> List<T> toBeanFromExcelNList( NList fromList, Class<T> toClass ) throws JsonIOException {

        List<T> list = new ArrayList<>();

        if( fromList == null || fromList.size() == 0 ) return list;

        NObjectMapper excelMapper = getExcelMapper();

        for( NMap map : fromList ) {

            String json = map.toJson();

            UserForExcelReader userForExcelReader = Reflector.toBeanFrom( json, UserForExcelReader.class );

            NLogger.debug( "json : {}\n", json );
            NLogger.debug( userForExcelReader );


            try {

                T bean = excelMapper.readValue( json, toClass );

                NLogger.debug( bean );

                list.add( bean );

            } catch( IOException e ) {
                throw new JsonIOException( e, "JsonParseException : {}\n\t- json string :\n{}\n\t- target class : {}", e.getMessage(), json, toClass );
            }

        }

        return list;

    }

    private NObjectMapper getExcelMapper() {
        NObjectMapper excelMapper = new NObjectMapper( false );

        excelMapper.setAnnotationIntrospectors(
                new ExcelReadAnnotationInspector(),
                new ExcelWriteAnnotationInspector()
        );

//        excelMapper.setAnnotationIntrospector( new JacksonAnnotationIntrospector() );
        return excelMapper;
    }


    @Test
    public void parseTest() throws IOException {

        String json = "{\"주소\":\"Seoul\",\"이름\":\"Jung Hwasu\",\"아이디\":\"1\"}";

        NLogger.debug( json );

        UserForExcelReader userForExcelReader = Reflector.toBeanFrom( json, UserForExcelReader.class );

        NLogger.debug( userForExcelReader );

        NObjectMapper excelMapper = getExcelMapper();

        NLogger.debug( json );

        User user = excelMapper.readValue( json, User.class );

        NLogger.debug( user );

    }


    private List<User> getDummy() {

        List<User> list = new ArrayList<>();

        list.add( new User( "1" , "Jung Hwasu"   , "Seoul"    )  ) ;
        list.add( new User( "2" , "Jung Juho"    , "Mapo"     )  ) ;
        list.add( new User( "3" , "Kim Sunji"    , "MokDong"  )  ) ;
        list.add( new User( "4" , "Jung Hwajong" , "HapJeong" )  ) ;
        list.add( new User( "5" , "Moon"         , "HapJeong" )  ) ;

        return list;

    }

}