/*
 * okjson - A small efficient flexible JSON parser/generator for Java
 * author	: calvin
 * email	: calvinwilliams@163.com
 *
 * See the file LICENSE in base directory.
 */

package org.albianj.common.fkjson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FKJSON {
	final public static int	OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE = 1 ;
	final public static int	OPTIONS_PRETTY_FORMAT_ENABLE = 2 ;
	final public static int	OPTIONS_STRICT_POLICY = 4 ;
	
	final public static int	OKJSON_ERROR_END_OF_BUFFER = FkJsonParser.OKJSON_ERROR_END_OF_BUFFER ;
	final public static int	OKJSON_ERROR_UNEXPECT = FkJsonParser.OKJSON_ERROR_UNEXPECT ;
	final public static int	OKJSON_ERROR_EXCEPTION = FkJsonParser.OKJSON_ERROR_EXCEPTION ;
	final public static int	OKJSON_ERROR_INVALID_BYTE = FkJsonParser.OKJSON_ERROR_INVALID_BYTE ;
	final public static int	OKJSON_ERROR_FIND_FIRST_LEFT_BRACE = FkJsonParser.OKJSON_ERROR_FIND_FIRST_LEFT_BRACE ;
	final public static int	OKJSON_ERROR_NAME_INVALID = FkJsonParser.OKJSON_ERROR_NAME_INVALID ;
	final public static int	OKJSON_ERROR_EXPECT_COLON_AFTER_NAME = FkJsonParser.OKJSON_ERROR_EXPECT_COLON_AFTER_NAME ;
	final public static int	OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE = FkJsonParser.OKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE ;
	final public static int	OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = FkJsonParser.OKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT ;
	final public static int	OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = FkJsonParser.OKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT ;
	final public static int	OKJSON_ERROR_NEW_OBJECT = FkJsonParser.OKJSON_ERROR_NEW_OBJECT ;
	
	private static ThreadLocal<FkJsonGenerator>			okjsonGeneratorCache ;
	private static ThreadLocal<FkJsonParser>			okjsonParserCache ;
	
	private static ThreadLocal<Integer>	errorCode = new ThreadLocal<Integer>() ;
	private static ThreadLocal<String>	errorDesc = new ThreadLocal<String>() ;
	
	public static Integer getErrorCode() {
		return errorCode.get();
	}
	
	public static String getErrorDesc() {
		return errorDesc.get();
	}
	
	public static int objectToFile( Object object, String filePath, int options ) {
		String jsonString = objectToString( object, options ) ;
		try {
			Files.write(Paths.get(filePath), jsonString.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
			return 0;
		} catch (IOException e) {
			return -1;
		}
	}
	
	public static String objectToString( Object object, int options ) {
		FkJsonGenerator okjsonGenerator ;
		
		if( okjsonGeneratorCache == null ) {
			okjsonGeneratorCache = new ThreadLocal<FkJsonGenerator>() ;
			if( okjsonGeneratorCache == null ) {
				errorDesc.set("New object failed for ThreadLocal<OkJsonGenerator>") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonGenerator = new FkJsonGenerator() ;
			if( okjsonGenerator == null ) {
				errorDesc.set("New object failed for OkJsonGenerator") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonGeneratorCache.set(okjsonGenerator);
		} else {
			okjsonGenerator = okjsonGeneratorCache.get();
		}
		
		if( (options&OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE) != 0 )
			okjsonGenerator.setDirectAccessPropertyEnable(true);
		else
			okjsonGenerator.setDirectAccessPropertyEnable(false);
		if( (options&OPTIONS_PRETTY_FORMAT_ENABLE) != 0 )
			okjsonGenerator.setPrettyFormatEnable(true);
		else
			okjsonGenerator.setPrettyFormatEnable(false);
		
		String string = okjsonGenerator.objectToString(object) ;
		
		errorCode.set(okjsonGenerator.getErrorCode());
		errorDesc.set(okjsonGenerator.getErrorDesc());
		
		return string;
	}
	
	public static <T> T fileToObject( String filePath, Class<T> clazz, int options ) {
		String jsonString = null ;
		
		try {
			jsonString = new String(Files.readAllBytes(Paths.get(filePath))) ;
		} catch(IOException e) {
			return null;
		}
		
		return stringToObject( jsonString, clazz, options );
	}
	
	public static <T> T stringToObject( String jsonString, Class<T> clazz, int options ) {
		FkJsonParser okjsonParser ;
		
		if( okjsonParserCache == null ) {
			okjsonParserCache = new ThreadLocal<FkJsonParser>() ;
			if( okjsonParserCache == null ) {
				errorDesc.set("New object failed for ThreadLocal<OkJsonParser>") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonParser = new FkJsonParser() ;
			if( okjsonParser == null ) {
				errorDesc.set("New object failed for okjsonParser") ;
				errorCode.set(OKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			okjsonParserCache.set(okjsonParser);
		} else {
			okjsonParser = okjsonParserCache.get();
		}
		
		if( (options&OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE) != 0 )
			okjsonParser.setDirectAccessPropertyEnable(true);
		else
			okjsonParser.setDirectAccessPropertyEnable(false);
		if( (options&OPTIONS_STRICT_POLICY) != 0 )
			okjsonParser.setStrictPolicyEnable(true);
		else
			okjsonParser.setStrictPolicyEnable(false);
		
		T object ;
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		object = okjsonParser.stringToObject(jsonString, object);
		
		errorCode.set(okjsonParser.getErrorCode());
		errorDesc.set(okjsonParser.getErrorDesc());
		
		return object;
	}
}

