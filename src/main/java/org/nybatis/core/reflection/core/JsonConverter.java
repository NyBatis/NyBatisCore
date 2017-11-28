package org.nybatis.core.reflection.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import org.apache.poi.ss.formula.functions.T;
import org.nybatis.core.exception.unchecked.JsonIOException;
import org.nybatis.core.model.NMap;
import org.nybatis.core.model.PrimitiveConverter;
import org.nybatis.core.util.StringUtil;
import org.nybatis.core.util.Types;
import org.nybatis.core.validation.Validator;

import java.io.IOException;
import java.util.*;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.ALWAYS;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static javafx.scene.input.KeyCode.Q;

/**
 * @author nayasis@gmail.com
 * @since 2017-11-27
 */
public class JsonConverter {

    private ObjectMapper objectMapper;
    private JsonInclude.Value DEFAULT_INCLUSION = JsonInclude.Value.empty();

    public JsonConverter( ObjectMapper mapper ) {
        this.objectMapper = mapper;
    }

    /**
     * Get json text
     *
     * @param fromBean		instance to convert as json data
     * @param prettyPrint	whether or not to make json text pretty
     * @param sort			whether or not to sort key of json
     * @param ignoreNull	whether or not to ignore null value
     * @return json text
     */
    public String toJson( Object fromBean, boolean prettyPrint, boolean sort, boolean ignoreNull ) throws JsonIOException {

        if( fromBean == null ) return null;

        ObjectWriter writer = prettyPrint ? objectMapper.writerWithDefaultPrettyPrinter() : objectMapper.writer();
        config( writer, SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, sort );
        config( writer, ignoreNull ? NON_NULL : ALWAYS );

        try {
            return writer.writeValueAsString( fromBean );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    private void config( ObjectWriter writer, JsonInclude.Include include ) {
        writer.getConfig().withPropertyInclusion( DEFAULT_INCLUSION.withValueInclusion( include ) );
    }

    private void config( ObjectWriter writer, SerializationFeature feature, boolean state ) {
        SerializationConfig config = writer.getConfig();
        if( state ) {
            config.with( feature );
        } else {
            config.without( feature );
        }
    }

    private void config( ObjectReader reader, DeserializationFeature feature, boolean state ) {
        DeserializationConfig config = reader.getConfig();
        if( state ) {
            config.with( feature );
        } else {
            config.without( feature );
        }
    }

    /**
     * Get json text
     *
     * @param fromBean		instance to convert as json data
     * @param prettyPrint	whether or not to make json text pretty
     * @return json text
     */
    public String toJson( Object fromBean, boolean prettyPrint ) throws JsonIOException {
        return toJson( fromBean, prettyPrint, false, false );

    }

    /**
     * Get json text
     *
     * @param fromBean		instance to convert as json data
     * @return json text
     */
    public String toJson( Object fromBean ) throws JsonIOException {
        return toJson( fromBean, false );
    }

    /**
     * get json text without null value
     *
     * @param fromBean		instance to convert as json
     * @param prettyPrint	true if you want to see json text with indentation
     * @return json text
     */
    public String toNullIgnoredJson( Object fromBean, boolean prettyPrint ) throws JsonIOException {
        return toJson( fromBean, prettyPrint, false, true );
    }

    /**
     * get json text without null value
     *
     * @param fromBean	instance to convert as json data
     * @return json text
     */
    public String toNullIgnoredJson( Object fromBean ) throws JsonIOException {
        return toNullIgnoredJson( fromBean, false );
    }

    /**
     * Get map with flatten key
     *
     * <pre>
     * json text or map like this
     *
     * {
     *   "name" : {
     *     "item" : [
     *     	  { "key" : "A", "value" : 1 }
     *     	]
     *   }
     * }
     *
     * will be converted as below.
     *
     * { "name.item[0].key" : "A", "name.item[0].value" : 1 }
     * </pre>
     *
     * @param object	json string, Map or bean
     * @return map with flattern key
     */
    public Map<String, Object> toMapWithFlattenKey( Object object ) {
        Map<String, Object> map = new HashMap<>();
        if( Validator.isNull(object) ) return map;
        flattenKeyRecursivly( "", toMapFrom( object ), map );
        return map;
    }

    private void flattenKeyRecursivly( String currentPath, Object json, Map result ) {

        if( json instanceof Map ) {
            Map<String, Object> map = (Map) json;
            String prefix = StringUtil.isEmpty( currentPath ) ? "" : currentPath + ".";
            for( String key : map.keySet() ) {
                flattenKeyRecursivly( prefix + key, map.get( key ), result );
            }
        } else if( json instanceof List ) {
            List list = (List) json;
            for( int i = 0, iCnt = list.size(); i < iCnt; i++ ) {
                flattenKeyRecursivly( String.format( "%s[%d]", currentPath, i ), list.get( i ), result );
            }
        } else {
            result.put( currentPath, json );
        }

    }

    /**
     * Get map with unflatten key
     *
     * <pre>
     * json text or map like this
     *
     * { "name.item[0].key" : "A", "name.item[0].value" : 1 }
     *
     * will be converted as below.
     *
     * {
     *   "name" : {
     *     "item" : [
     *     	  { "key" : "A", "value" : 1 }
     *     	]
     *   }
     * }
     * </pre>
     *
     * @param object	json string, Map or bean
     * @return map with flattern key
     */
    public Map<String, Object> toMapWithUnflattenKey( Object object ) {

        Map<String, Object> map = new HashMap<>();

        if( Validator.isNull(object) ) return map;

        Map<String, Object> objectMap = toMapFrom( object );

        for( String key : objectMap.keySet() ) {
            unflattenKeyRecursivly( key, objectMap.get( key ), map );
        }

        return map;

    }

    private void unflattenKeyRecursivly( String jsonPath, Object value, Map result ) {

        String path  = jsonPath.replaceFirst( "\\[.*\\]", "" ).replaceFirst( "\\..*?$", "" );
        String index = jsonPath.replaceFirst(  "^(" + path + ")\\[(.*?)\\](.*?)$", "$2" );

        if( index.equals( jsonPath ) ) index = "";

        boolean isArray = ! index.isEmpty();

        String currentPath = String.format( "%s%s", path, isArray ? String.format("[%s]", index) : "" );

        boolean isKey = currentPath.equals( jsonPath );

        if( isKey ) {
            if( isArray ) {
                int idx = new PrimitiveConverter( index ).toInt();
                setValueToListInJson( path, idx, value, result );
            } else {
                result.put( path, value );
            }
        } else {

            if( ! result.containsKey(path) ) {
                result.put( path, isArray ? new ArrayList() : new HashMap() );
            }

            Map newVal;

            if( isArray ) {
                List list = (List) result.get( path );
                int idx = new PrimitiveConverter( index ).toInt();
                if( list.size() <= idx || list.get(idx) == null ) {
                    setValueToListInJson( path, idx, new HashMap(), result );
                }
                newVal = (Map) list.get( idx );
            } else {
                newVal = (Map) result.get( path );
            }

            String recursivePath = jsonPath.replaceFirst( currentPath.replaceAll( "\\[", "\\\\[" ) + ".", "" );
            unflattenKeyRecursivly( recursivePath, value, newVal );

        }

    }

    private void setValueToListInJson( String key, int idx, Object value, Map json ) {

        if( ! json.containsKey( key ) ) {
            json.put( key, new ArrayList<>() );
        }

        List list = (List) json.get( key );
        int listSize = list.size();
        if( idx >= listSize ) {
            for( int i = listSize; i <= idx; i++ ) {
                list.add( null );
            }
        }
        list.set( idx, value );

    }

    public JsonNode readTree( String json ) throws JsonIOException {


        // TODO : read tree structure
/**
 *  JsonNode node = mapper.readTree( json );
 *  Iterator<String> names = node.fieldNames();
 *  while (names.hasNext()) {
 *   String name = (String) names.next();
 *   JsonNodeType type = node.get(name).getNodeType();
 *   System.out.println(name+":"+type); //will print id:STRING
 *  }
 */

        try {
            return objectMapper.readTree( json );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    private String getContent( String fromJsonString ) {
        return StringUtil.isEmpty( fromJsonString ) ? "{}" : fromJsonString;
    }

    private String getArrayContent( String fromJsonString ) {
        return StringUtil.isEmpty( fromJsonString ) ? "[]" : fromJsonString;
    }

    /**
     * check text is valid json type
     *
     * @param json	json text
     * @return valid or not
     */
    public boolean isValidJson( String json ) {
        try {
            objectMapper.readTree( json );
            return true;
        } catch( IOException e ) {
            return false;
        }
    }

    /**
     * Convert as bean from object
     * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @param toClass	class to return
     * @param <T>		return type
     * @return	bean filled by object's value
     */
    public <T> T toBeanFrom( Object object, Class<T> toClass ) throws JsonIOException {
        if( Types.isString( object ) ) {
            return toBeanFromJson( object.toString(), toClass );
        } else {
            return objectMapper.convertValue( object, toClass );
        }
    }

    /**
     * Convert as bean from object
     * @param object		json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @param typeReference	type to return
     * 	<pre>
     *	  Examples are below.
     *	  	- new TypeReference&lt;List&lt;HashMap&lt;String, Object&gt;&gt;&gt;() {}
     *	    - new TypeReference&lt;List&lt;String&gt;&gt;() {}
     *	    - new TypeReference&lt;List&gt;() {}
     * 	</pre>
     * @param <T>		return type
     * @return	bean filled by object's value
     */
    public <T> T toBeanFrom( Object object, TypeReference<T> typeReference ) throws JsonIOException {
        if( Types.isString( object ) ) {
            return toBeanFromJson( object.toString(), typeReference );
        } else {
            return objectMapper.convertValue( object, typeReference );
        }
    }

    /**
     * Convert as bean from json text
     * @param jsonString	json text
     * @param toClass		class to return
     * @param <T>			return type
     * @return bean filled by json value
     */
    private <T> T toBeanFromJson( String jsonString, Class<T> toClass ) throws JsonIOException {
        try {
            return objectMapper.readValue( getContent( jsonString ), toClass );
        } catch( JsonParseException e ) {
            throw new JsonIOException( "JsonParseException : {}\n\t- json string :\n{}\n\t- target class : {}", e.getMessage(), jsonString, toClass );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    /**
     * Convert as bean from json text
     * @param jsonString	json text
     * @param typeReference	type to return
     * 	<pre>
     *	  Examples are below.
     *	  	- new TypeReference<List<HashMap<String, Object>>>() {}
     *	    - new TypeReference<List<String>>() {}
     *	    - new TypeReference<List>() {}
     * 	</pre>
     * @param <T>			return type
     * @return bean filled by json value
     */
    private <T> T toBeanFromJson( String jsonString, TypeReference<T> typeReference ) throws JsonIOException {
        try {
            return objectMapper.readValue( getContent( jsonString ), typeReference );
        } catch( JsonParseException e ) {
            throw new JsonIOException( "JsonParseException : {}\n\t- json string :\n{}", e.getMessage(), jsonString );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    /**
     * convert json to list
     *
     * @param jsonString	json text
     * @param typeClass   	list's generic type
     * @param <T> generic type
     * @return list
     */
    public <T> List<T> toListFromJson( String jsonString, Class<T> typeClass ) throws JsonIOException {
        try {
            return objectMapper.readValue( getArrayContent(jsonString), objectMapper.getTypeFactory().constructCollectionType(List.class, typeClass) );
        } catch( JsonParseException e ) {
            throw new JsonIOException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), jsonString );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    public <T> Collection<T> toCollectionFromJson( String json, Class<? extends Collection> collectionClass, Class<T> typeClass ) throws JsonIOException {
        try {
            return objectMapper.readValue( getArrayContent(json), objectMapper.getTypeFactory().constructCollectionType(collectionClass, typeClass) );
        } catch( JsonParseException e ) {
            throw new JsonIOException( "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), json );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    /**
     * Convert as List
     *
     * @param json json text
     * @return List
     */
    public List toListFromJson( String json ) throws JsonIOException {
        return toListFromJson( json, Object.class );
    }

    /**
     * Convert as Map from object
     * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @return	Map filled by object's value
     */
    public Map<String, Object> toMapFrom( Object object ) throws JsonIOException {
        if( object == null ) return new HashMap<>();
        if( Types.isString(object)  ) {
            return toMapFromJson( object.toString() );
        } else {
            return objectMapper.convertValue( object, Map.class );
        }
    }

    /**
     * Convert as Map from json text
     * @param json	json text
     * @return	Map filled by object's value
     */
    private Map<String, Object> toMapFromJson( String json ) throws JsonIOException {
        try {
            Map<String, Object> stringObjectMap = objectMapper.readValue( getContent( json ), new TypeReference<LinkedHashMap<String, Object>>() {} );
            return Validator.nvl( stringObjectMap, new LinkedHashMap<String, Object>() );
        } catch( JsonParseException e ) {
            throw new JsonIOException( e, "JsonParseException : {}\n\t-source :\n{}\n", e.getMessage(), json );
        } catch( IOException e ) {
            throw new JsonIOException( e );
        }
    }

    /**
     * Convert as NMap from object
     * @param object	json text (type can be String, StringBuffer, StringBuilder), Map or bean to convert
     * @return	NMap filled by object's value
     */
    public NMap toNMapFrom( Object object ) throws JsonIOException {
        return new NMap( toMapFrom( object ) );
    }

    public boolean isJsonDate( Object value ) {
        if( Types.isNotString(value) ) return false;
        String val = value.toString();
        return Validator.isMatched( val, "\\d{4}-(0[0-9]|1[0-2])-(0[1-9]|[1-2][0-9]|3[0-1])T([0-1][0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]\\.\\d{3}[+-]([0-1][0-9]|2[0-4])[0-5][0-9]" );
    }

}


