/*
 * fkjson - A small efficient flexible JSON parser/generator for Java
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
	
	final public static int	FKJSON_ERROR_END_OF_BUFFER = FkJsonParser.FKJSON_ERROR_END_OF_BUFFER ;
	final public static int	FKJSON_ERROR_UNEXPECT = FkJsonParser.FKJSON_ERROR_UNEXPECT ;
	final public static int	FKJSON_ERROR_EXCEPTION = FkJsonParser.FKJSON_ERROR_EXCEPTION ;
	final public static int	FKJSON_ERROR_INVALID_BYTE = FkJsonParser.FKJSON_ERROR_INVALID_BYTE ;
	final public static int	FKJSON_ERROR_FIND_FIRST_LEFT_BRACE = FkJsonParser.FKJSON_ERROR_FIND_FIRST_LEFT_BRACE ;
	final public static int	FKJSON_ERROR_NAME_INVALID = FkJsonParser.FKJSON_ERROR_NAME_INVALID ;
	final public static int	FKJSON_ERROR_EXPECT_COLON_AFTER_NAME = FkJsonParser.FKJSON_ERROR_EXPECT_COLON_AFTER_NAME ;
	final public static int	FKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE = FkJsonParser.FKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE ;
	final public static int	FKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = FkJsonParser.FKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT ;
	final public static int	FKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = FkJsonParser.FKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT ;
	final public static int	FKJSON_ERROR_NEW_OBJECT = FkJsonParser.FKJSON_ERROR_NEW_OBJECT ;
	
	private static ThreadLocal<FkJsonGenerator>			fkjsonGeneratorCache ;
	private static ThreadLocal<FkJsonParser>			fkjsonParserCache ;
	
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
		FkJsonGenerator fkjsonGenerator ;
		
		if( fkjsonGeneratorCache == null ) {
			fkjsonGeneratorCache = new ThreadLocal<FkJsonGenerator>() ;
			if( fkjsonGeneratorCache == null ) {
				errorDesc.set("New object failed for ThreadLocal<OkJsonGenerator>") ;
				errorCode.set(FKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			fkjsonGenerator = new FkJsonGenerator() ;
			if( fkjsonGenerator == null ) {
				errorDesc.set("New object failed for OkJsonGenerator") ;
				errorCode.set(FKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			fkjsonGeneratorCache.set(fkjsonGenerator);
		} else {
			fkjsonGenerator = fkjsonGeneratorCache.get();
		}
		
		if( (options&OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE) != 0 )
			fkjsonGenerator.setDirectAccessPropertyEnable(true);
		else
			fkjsonGenerator.setDirectAccessPropertyEnable(false);
		if( (options&OPTIONS_PRETTY_FORMAT_ENABLE) != 0 )
			fkjsonGenerator.setPrettyFormatEnable(true);
		else
			fkjsonGenerator.setPrettyFormatEnable(false);
		
		String string = fkjsonGenerator.objectToString(object) ;
		
		errorCode.set(fkjsonGenerator.getErrorCode());
		errorDesc.set(fkjsonGenerator.getErrorDesc());
		
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
		FkJsonParser fkjsonParser ;
		
		if( fkjsonParserCache == null ) {
			fkjsonParserCache = new ThreadLocal<FkJsonParser>() ;
			if( fkjsonParserCache == null ) {
				errorDesc.set("New object failed for ThreadLocal<OkJsonParser>") ;
				errorCode.set(FKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			fkjsonParser = new FkJsonParser() ;
			if( fkjsonParser == null ) {
				errorDesc.set("New object failed for fkjsonParser") ;
				errorCode.set(FKJSON_ERROR_NEW_OBJECT);
				return null;
			}
			fkjsonParserCache.set(fkjsonParser);
		} else {
			fkjsonParser = fkjsonParserCache.get();
		}
		
		if( (options&OPTIONS_DIRECT_ACCESS_PROPERTY_ENABLE) != 0 )
			fkjsonParser.setDirectAccessPropertyEnable(true);
		else
			fkjsonParser.setDirectAccessPropertyEnable(false);
		if( (options&OPTIONS_STRICT_POLICY) != 0 )
			fkjsonParser.setStrictPolicyEnable(true);
		else
			fkjsonParser.setStrictPolicyEnable(false);
		
		T object ;
		try {
			object = clazz.newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		object = fkjsonParser.stringToObject(jsonString, object);
		
		errorCode.set(fkjsonParser.getErrorCode());
		errorDesc.set(fkjsonParser.getErrorDesc());
		
		return object;
	}
}

