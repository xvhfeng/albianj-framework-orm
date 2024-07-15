package org.albianj.common.fkjson;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class FkJsonGenerator {

    enum ClassFieldType {
        CLASSFIELDTYPE_STRING,
        CLASSFIELDTYPE_NOT_STRING,
        CLASSFIELDTYPE_LOCALDATE,
        CLASSFIELDTYPE_LOCALTIME,
        CLASSFIELDTYPE_LOCALDATETIME,
        CLASSFIELDTYPE_LIST,
        CLASSFIELDTYPE_SUBCLASS
    }

    class OkJsonClassField {
        char[] fieldName;
        char[] fieldNameQM;
        ClassFieldType type;
        Field field;
        Method getMethod;
        FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
    }

    private static ThreadLocal<HashMap<String, LinkedList<OkJsonClassField>>> classMapFieldListCache;
    private static ThreadLocal<FkJsonCharArrayBuilder> jsonByteArrayBuilderCache;
    private static ThreadLocal<FkJsonCharArrayBuilder> fieldByteArrayBuilderCache;
    private static ThreadLocal<HashMap<Class, Boolean>> basicTypeClassMapBooleanCache;

    private boolean strictPolicyEnable;
    private boolean directAccessPropertyEnable;
    private boolean prettyFormatEnable;

    private Integer errorCode;
    private String errorDesc;

    final public static int FKJSON_ERROR_EXCEPTION = -8;
    final public static int FKJSON_ERROR_NEW_OBJECT = -31;

    final private static char SEPFIELD_CHAR = ',';
    final private static char[] SEPFIELD_CHAR_PRETTY = " ,\n".toCharArray();
    final private static char ENTER_CHAR = '\n';
    final private static String NULL_STRING = "null";

    private int objectToListString(List<Object> array, int arrayCount, OkJsonClassField classField, FkJsonCharArrayBuilder jsonCharArrayBuilder, int depth) {

        HashMap<Class, Boolean> basicTypeClassMapBoolean = basicTypeClassMapBooleanCache.get();
        int arrayIndex;
        int nret;

        try {
            Type type = classField.field.getGenericType();
            ParameterizedType pt = (ParameterizedType) type;
            Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0];
            Boolean b = basicTypeClassMapBoolean.get(typeClazz);
            if (b == null)
                b = false;
            if (b) {
                arrayIndex = 0;
                for (Object object : array) {
                    arrayIndex++;
                    if (arrayIndex > 1) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendCharArrayWith3(SEPFIELD_CHAR_PRETTY).appendTabs(depth + 1);
                        } else {
                            jsonCharArrayBuilder.appendChar(SEPFIELD_CHAR);
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1);
                        }
                    }

                    if (typeClazz == String.class && object != null) {
                        String str = (String) object;
                        jsonCharArrayBuilder.appendJsonQmStringQm(str);
                    } else if (typeClazz == LocalDate.class && object != null) {
                        LocalDate localDate;
                        String defaultDateTimeFormatter;
                        localDate = (LocalDate) object;
                        if (classField.fkjsonDateTimeFormatter != null) {
                            defaultDateTimeFormatter = classField.fkjsonDateTimeFormatter.format();
                        } else {
                            defaultDateTimeFormatter = "yyyy-MM-dd";
                        }
                        String localDateString = DateTimeFormatter.ofPattern(defaultDateTimeFormatter).format(localDate);
                        jsonCharArrayBuilder.appendJsonQmStringQm(localDateString);
                    } else if (typeClazz == LocalTime.class && object != null) {
                        LocalTime localTime;
                        String defaultDateTimeFormatter;
                        localTime = (LocalTime) object;
                        if (classField.fkjsonDateTimeFormatter != null) {
                            defaultDateTimeFormatter = classField.fkjsonDateTimeFormatter.format();
                        } else {
                            defaultDateTimeFormatter = "yyyy-MM-dd";
                        }
                        String localTimeString = DateTimeFormatter.ofPattern(defaultDateTimeFormatter).format(localTime);
                        jsonCharArrayBuilder.appendJsonQmStringQm(localTimeString);
                    } else if (typeClazz == LocalDateTime.class && object != null) {
                        LocalDateTime localDateTime;
                        String defaultDateTimeFormatter;
                        localDateTime = (LocalDateTime) object;
                        if (classField.fkjsonDateTimeFormatter != null) {
                            defaultDateTimeFormatter = classField.fkjsonDateTimeFormatter.format();
                        } else {
                            defaultDateTimeFormatter = "yyyy-MM-dd";
                        }
                        String localDateTimeString = DateTimeFormatter.ofPattern(defaultDateTimeFormatter).format(localDateTime);
                        jsonCharArrayBuilder.appendJsonQmStringQm(localDateTimeString);
                    } else {
                        jsonCharArrayBuilder.appendJsonString(object.toString());
                    }
                }
            } else {
                arrayIndex = 0;
                for (Object object : array) {
                    arrayIndex++;
                    if (arrayIndex > 1) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendCharArrayWith3(SEPFIELD_CHAR_PRETTY);
                        } else {
                            jsonCharArrayBuilder.appendChar(SEPFIELD_CHAR);
                        }
                    }

                    if (object != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendString("{\n");
                        } else {
                            jsonCharArrayBuilder.appendChar('{');
                        }
                        nret = objectToPropertiesString(object, jsonCharArrayBuilder, depth + 1);
                        if (nret != 0)
                            return nret;
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendString("}");
                        } else {
                            jsonCharArrayBuilder.appendChar('}');
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FKJSON_ERROR_EXCEPTION;
        }

        if (prettyFormatEnable) {
            jsonCharArrayBuilder.appendChar(ENTER_CHAR);
        }

        return 0;
    }

    private String unfoldEscape(String value) {

        FkJsonCharArrayBuilder fieldCharArrayBuilder = fieldByteArrayBuilderCache.get();
        char[] jsonCharArrayBuilder;
        int jsonCharArrayLength;
        int jsonCharArrayIndex;
        int segmentBeginOffset;
        int segmentLen;
        char c;

        if (value == null)
            return null;

        jsonCharArrayBuilder = value.toCharArray();
        jsonCharArrayLength = value.length();

        fieldCharArrayBuilder.setLength(0);

        segmentBeginOffset = 0;
        for (jsonCharArrayIndex = 0; jsonCharArrayIndex < jsonCharArrayLength; jsonCharArrayIndex++) {
            c = jsonCharArrayBuilder[jsonCharArrayIndex];
            if (c == '\"') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\\"".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '\\') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\\\".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '/') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\/".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '\t') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\t".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '\f') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\f".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '\b') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\b".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '\n') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\n".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            } else if (c == '\r') {
                segmentLen = jsonCharArrayIndex - segmentBeginOffset;
                if (segmentLen > 0)
                    fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
                fieldCharArrayBuilder.appendCharArray("\\r".toCharArray());
                segmentBeginOffset = jsonCharArrayIndex + 1;
            }
        }
        if (fieldCharArrayBuilder.getLength() > 0 && segmentBeginOffset < jsonCharArrayIndex) {
            segmentLen = jsonCharArrayIndex - segmentBeginOffset;
            if (segmentLen > 0)
                fieldCharArrayBuilder.appendBytesFromOffsetWithLength(jsonCharArrayBuilder, segmentBeginOffset, segmentLen);
        }

        if (fieldCharArrayBuilder.getLength() == 0)
            return value;
        else
            return fieldCharArrayBuilder.toString();
    }

    private int objectToPropertiesString(Object object, FkJsonCharArrayBuilder jsonCharArrayBuilder, int depth) {

        HashMap<Class, Boolean> basicTypeClassMapBoolean = basicTypeClassMapBooleanCache.get();
        Class<?> clazz;
        LinkedList<OkJsonClassField> classFieldList;
        Field[] fields;
        String methodName;
        int fieldIndex;

        int nret = 0;

        clazz = object.getClass();

        classFieldList = classMapFieldListCache.get().get(clazz.getName());
        if (classFieldList == null) {
            classFieldList = new LinkedList<OkJsonClassField>();
            classMapFieldListCache.get().put(clazz.getName(), classFieldList);
        }

        if (classFieldList.isEmpty()) {
            OkJsonClassField classField;

            fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                f.setAccessible(true);

                classField = new OkJsonClassField();
                classField.fieldName = f.getName().toCharArray();
                classField.fieldNameQM = ('\"' + f.getName() + '\"').toCharArray();
                classField.field = f;
                if (f.getType() == String.class)
                    classField.type = ClassFieldType.CLASSFIELDTYPE_STRING;
                else if (f.getType() == LocalDate.class)
                    classField.type = ClassFieldType.CLASSFIELDTYPE_LOCALDATE;
                else if (f.getType() == LocalTime.class)
                    classField.type = ClassFieldType.CLASSFIELDTYPE_LOCALTIME;
                else if (f.getType() == LocalDateTime.class)
                    classField.type = ClassFieldType.CLASSFIELDTYPE_LOCALDATETIME;
                else if (f.getType() == ArrayList.class || f.getType() == LinkedList.class)
                    classField.type = ClassFieldType.CLASSFIELDTYPE_LIST;
                else if (basicTypeClassMapBoolean.get(f.getType()) != null || f.getType().isPrimitive())
                    classField.type = ClassFieldType.CLASSFIELDTYPE_NOT_STRING;
                else
                    classField.type = ClassFieldType.CLASSFIELDTYPE_SUBCLASS;

                try {
                    if (f.getType() == Boolean.class || f.getType().getName().equals("boolean")) {
                        methodName = "is" + f.getName().substring(0, 1).toUpperCase(Locale.getDefault()) + f.getName().substring(1);
                    } else {
                        methodName = "get" + f.getName().substring(0, 1).toUpperCase(Locale.getDefault()) + f.getName().substring(1);
                    }
                    classField.getMethod = clazz.getMethod(methodName);
                    classField.getMethod.setAccessible(true);
                } catch (NoSuchMethodException e) {
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }

                if (f.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                    classField.fkjsonDateTimeFormatter = f.getAnnotation(FkJsonDateTimeFormatter.class);
                } else {
                    classField.fkjsonDateTimeFormatter = null;
                }

                if (Modifier.isPublic(f.getModifiers())) {
                    classFieldList.add(classField);
                } else if (classField.getMethod != null && Modifier.isPublic(classField.getMethod.getModifiers())) {
                    classFieldList.add(classField);
                }
            }
        }

        fieldIndex = 0;
        for (OkJsonClassField classField : classFieldList) {
            fieldIndex++;
            if (fieldIndex > 1) {
                if (prettyFormatEnable) {
                    jsonCharArrayBuilder.appendCharArrayWith3(SEPFIELD_CHAR_PRETTY);
                } else {
                    jsonCharArrayBuilder.appendChar(SEPFIELD_CHAR);
                }
            }

            switch (classField.type) {
                case CLASSFIELDTYPE_STRING:
                    String string = null;
                    if (classField.getMethod != null) {
                        try {
                            string = (String) (classField.getMethod.invoke(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            string = (String) (classField.field.get(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    string = unfoldEscape((String) string);
                    if (string != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndQmStringQmPretty(classField.fieldName, string);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndQmStringQm(classField.fieldName, string);
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
                case CLASSFIELDTYPE_NOT_STRING:
                    Object value = null;
                    if (classField.getMethod != null) {
                        try {
                            value = classField.getMethod.invoke(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            value = classField.field.get(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    if (value != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, value.toString());
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, value.toString());
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
                case CLASSFIELDTYPE_LOCALDATE:
                    LocalDate localDate;
                    String defaultDateTimeFormatter;
                    if (classField.getMethod != null) {
                        try {
                            localDate = (LocalDate) (classField.getMethod.invoke(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            localDate = (LocalDate) (classField.field.get(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    if (classField.fkjsonDateTimeFormatter != null) {
                        defaultDateTimeFormatter = classField.fkjsonDateTimeFormatter.format();
                    } else {
                        defaultDateTimeFormatter = "yyyy-MM-dd";
                    }
                    String localDateString = DateTimeFormatter.ofPattern(defaultDateTimeFormatter).format(localDate);
                    if (localDateString != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndQmStringQmPretty(classField.fieldName, localDateString);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndQmStringQm(classField.fieldName, localDateString);
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
                case CLASSFIELDTYPE_LOCALTIME:
                    LocalTime localTime = null;
                    if (classField.getMethod != null) {
                        try {
                            localTime = (LocalTime) (classField.getMethod.invoke(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            localTime = (LocalTime) (classField.field.get(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    if (classField.fkjsonDateTimeFormatter != null) {
                        defaultDateTimeFormatter = classField.fkjsonDateTimeFormatter.format();
                    } else {
                        defaultDateTimeFormatter = "HH:mm:ss";
                    }
                    String localTimeString = DateTimeFormatter.ofPattern(defaultDateTimeFormatter).format(localTime);
                    if (localTimeString != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndQmStringQmPretty(classField.fieldName, localTimeString);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndQmStringQm(classField.fieldName, localTimeString);
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
                case CLASSFIELDTYPE_LOCALDATETIME:
                    LocalDateTime localDateTime = null;
                    if (classField.getMethod != null) {
                        try {
                            localDateTime = (LocalDateTime) (classField.getMethod.invoke(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            localDateTime = (LocalDateTime) (classField.field.get(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    if (classField.fkjsonDateTimeFormatter != null) {
                        defaultDateTimeFormatter = classField.fkjsonDateTimeFormatter.format();
                    } else {
                        defaultDateTimeFormatter = "yyyy-MM-dd HH:mm:ss";
                    }
                    String localDateTimeString = DateTimeFormatter.ofPattern(defaultDateTimeFormatter).format(localDateTime);
                    if (localDateTimeString != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndQmStringQmPretty(classField.fieldName, localDateTimeString);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndQmStringQm(classField.fieldName, localDateTimeString);
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
                case CLASSFIELDTYPE_LIST:
                    List<Object> array;
                    if (classField.getMethod != null) {
                        try {
                            array = (List<Object>) (classField.getMethod.invoke(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            array = (List<Object>) (classField.field.get(object));
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    if (array != null) {
                        int arrayCount = array.size();
                        if (arrayCount > 0) {
                            if (prettyFormatEnable) {
                                jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndOpenBytePretty(classField.fieldName, '[');
                                nret = objectToListString(array, arrayCount, classField, jsonCharArrayBuilder, depth + 1);
                                if (nret != 0)
                                    return nret;
                                jsonCharArrayBuilder.appendTabs(depth + 1).appendChar(']');
                            } else {
                                jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(classField.fieldName, '[');
                                nret = objectToListString(array, arrayCount, classField, jsonCharArrayBuilder, depth + 1);
                                if (nret != 0)
                                    return nret;
                                jsonCharArrayBuilder.appendCloseByte(']');
                            }
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
                case CLASSFIELDTYPE_SUBCLASS:
                    Object subObject;
                    if (classField.getMethod != null) {
                        try {
                            subObject = classField.getMethod.invoke(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    } else {
                        try {
                            subObject = classField.field.get(object);
                        } catch (Exception e) {
                            e.printStackTrace();
                            return FKJSON_ERROR_EXCEPTION;
                        }
                    }
                    if (subObject != null) {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndOpenBytePretty(classField.fieldName, '{');
                            nret = objectToPropertiesString(subObject, jsonCharArrayBuilder, depth + 1);
                            if (nret != 0)
                                return nret;
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendChar('}');
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndOpenByte(classField.fieldName, '{');
                            nret = objectToPropertiesString(subObject, jsonCharArrayBuilder, depth + 1);
                            if (nret != 0)
                                return nret;
                            jsonCharArrayBuilder.appendCloseByte('}');
                        }
                    } else {
                        if (prettyFormatEnable) {
                            jsonCharArrayBuilder.appendTabs(depth + 1).appendJsonNameAndColonAndStringPretty(classField.fieldName, NULL_STRING);
                        } else {
                            jsonCharArrayBuilder.appendJsonNameAndColonAndString(classField.fieldName, NULL_STRING);
                        }
                    }
                    break;
            }
        }

        if (prettyFormatEnable) {
            jsonCharArrayBuilder.appendChar(ENTER_CHAR);
        }

        return 0;
    }

    public int objectToFile(Object object, String filePath) {
        String jsonString = objectToString(object);
        try {
            Files.write(Paths.get(filePath), jsonString.getBytes(), StandardOpenOption.WRITE);
            return 0;
        } catch (IOException e) {
            return -1;
        }
    }

    public String objectToString(Object object) {

        FkJsonCharArrayBuilder jsonCharArrayBuilder;
        FkJsonCharArrayBuilder fieldCharArrayBuilder;
        HashMap<Class, Boolean> basicTypeClassMapString;

        if (classMapFieldListCache == null) {
            classMapFieldListCache = new ThreadLocal<HashMap<String, LinkedList<OkJsonClassField>>>();
            if (classMapFieldListCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            classMapFieldListCache.set(new HashMap<String, LinkedList<OkJsonClassField>>());
        }

        if (jsonByteArrayBuilderCache == null) {
            jsonByteArrayBuilderCache = new ThreadLocal<FkJsonCharArrayBuilder>();
            if (jsonByteArrayBuilderCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            jsonCharArrayBuilder = new FkJsonCharArrayBuilder(1024);
            if (jsonCharArrayBuilder == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            jsonByteArrayBuilderCache.set(jsonCharArrayBuilder);
        } else {
            jsonCharArrayBuilder = jsonByteArrayBuilderCache.get();
        }
        jsonCharArrayBuilder.setLength(0);

        if (fieldByteArrayBuilderCache == null) {
            fieldByteArrayBuilderCache = new ThreadLocal<FkJsonCharArrayBuilder>();
            if (fieldByteArrayBuilderCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            fieldCharArrayBuilder = new FkJsonCharArrayBuilder(1024);
            if (fieldCharArrayBuilder == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            fieldByteArrayBuilderCache.set(fieldCharArrayBuilder);
        }

        if (basicTypeClassMapBooleanCache == null) {
            basicTypeClassMapBooleanCache = new ThreadLocal<HashMap<Class, Boolean>>();
            if (basicTypeClassMapBooleanCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            basicTypeClassMapString = new HashMap<Class, Boolean>();
            if (basicTypeClassMapString == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            basicTypeClassMapString.put(String.class, new Boolean(true));
            basicTypeClassMapString.put(Byte.class, new Boolean(true));
            basicTypeClassMapString.put(Short.class, new Boolean(true));
            basicTypeClassMapString.put(Integer.class, new Boolean(true));
            basicTypeClassMapString.put(Long.class, new Boolean(true));
            basicTypeClassMapString.put(Float.class, new Boolean(true));
            basicTypeClassMapString.put(Double.class, new Boolean(true));
            basicTypeClassMapString.put(Boolean.class, new Boolean(true));
            basicTypeClassMapString.put(LocalDate.class, new Boolean(true));
            basicTypeClassMapString.put(LocalTime.class, new Boolean(true));
            basicTypeClassMapString.put(LocalDateTime.class, new Boolean(true));
            basicTypeClassMapBooleanCache.set(basicTypeClassMapString);
        }

        if (prettyFormatEnable) {
            jsonCharArrayBuilder.appendCharArray("{\n".toCharArray());
        } else {
            jsonCharArrayBuilder.appendChar('{');
        }

        errorCode = objectToPropertiesString(object, jsonCharArrayBuilder, 0);
        if (errorCode != 0)
            return null;

        if (prettyFormatEnable) {
            jsonCharArrayBuilder.appendCharArray("}\n".toCharArray());
        } else {
            jsonCharArrayBuilder.appendChar('}');
        }

        return jsonCharArrayBuilder.toString();
    }

    public boolean isStrictPolicyEnable() {
        return strictPolicyEnable;
    }

    public void setStrictPolicyEnable(boolean strictPolicyEnable) {
        this.strictPolicyEnable = strictPolicyEnable;
    }

    public boolean isDirectAccessPropertyEnable() {
        return directAccessPropertyEnable;
    }

    public void setDirectAccessPropertyEnable(boolean directAccessPropertyEnable) {
        this.directAccessPropertyEnable = directAccessPropertyEnable;
    }

    public boolean isPrettyFormatEnable() {
        return prettyFormatEnable;
    }

    public void setPrettyFormatEnable(boolean prettyFormatEnable) {
        this.prettyFormatEnable = prettyFormatEnable;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public FkJsonGenerator() {
        this.strictPolicyEnable = false;
        this.directAccessPropertyEnable = false;
        this.prettyFormatEnable = false;
        this.errorCode = 0;
        this.errorDesc = null;
    }
}
