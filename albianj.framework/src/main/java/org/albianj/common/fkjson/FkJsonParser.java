package org.albianj.common.fkjson;

import java.io.IOException;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

class FkJsonParser {
    private boolean strictPolicyEnable;
    private boolean directAccessPropertyEnable;
    private boolean prettyFormatEnable;

    private Integer errorCode;
    private String errorDesc;

    enum TokenType {
        TOKEN_TYPE_LEFT_BRACE, // {
        TOKEN_TYPE_RIGHT_BRACE, // }
        TOKEN_TYPE_LEFT_BRACKET, // [
        TOKEN_TYPE_RIGHT_BRACKET, // ]
        TOKEN_TYPE_COLON, // :
        TOKEN_TYPE_COMMA, // ,
        TOKEN_TYPE_STRING, // "ABC"
        TOKEN_TYPE_INTEGER, // 123
        TOKEN_TYPE_DECIMAL, // 123.456
        TOKEN_TYPE_BOOL, // true or false
        TOKEN_TYPE_NULL // null
    }

    private static ThreadLocal<HashMap<String, HashMap<String, Field>>> stringMapFieldsCache;
    private static ThreadLocal<HashMap<String, HashMap<String, Method>>> stringMapMethodsCache;
    private static ThreadLocal<StringBuilder> fieldStringBuilderCache;

    private int jsonOffset;
    private int jsonLength;

    private TokenType tokenType;
    private int beginOffset;
    private int endOffset;
    private boolean booleanValue;

    final public static int FKJSON_ERROR_END_OF_BUFFER = 1;
    final public static int FKJSON_ERROR_UNEXPECT = -4;
    final public static int FKJSON_ERROR_EXCEPTION = -8;
    final public static int FKJSON_ERROR_INVALID_BYTE = -11;
    final public static int FKJSON_ERROR_FIND_FIRST_LEFT_BRACE = -21;
    final public static int FKJSON_ERROR_NAME_INVALID = -22;
    final public static int FKJSON_ERROR_EXPECT_COLON_AFTER_NAME = -23;
    final public static int FKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE = -24;
    final public static int FKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT = -26;
    final public static int FKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT = -28;
    final public static int FKJSON_ERROR_NEW_OBJECT = -31;

    private int tokenJsonString(char[] jsonCharArray) {
        StringBuilder fieldStringBuilder;
        char ch;

        fieldStringBuilder = fieldStringBuilderCache.get();
        fieldStringBuilder.setLength(0);

        jsonOffset++;
        beginOffset = jsonOffset;
        while (jsonOffset < jsonLength) {
            ch = jsonCharArray[jsonOffset];
            if (ch == '"') {
                tokenType = TokenType.TOKEN_TYPE_STRING;
                if (jsonOffset > beginOffset) {
                    fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset);
                }
                endOffset = jsonOffset - 1;
                jsonOffset++;
                return 0;
            } else if (ch == '\\') {
                jsonOffset++;
                if (jsonOffset >= jsonLength) {
                    return FKJSON_ERROR_END_OF_BUFFER;
                }
                ch = jsonCharArray[jsonOffset];
                if (ch == '"') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('"');
                    beginOffset = jsonOffset + 1;
                } else if (ch == '\\') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append("\\");
                    beginOffset = jsonOffset + 1;
                } else if (ch == '/') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('/');
                    beginOffset = jsonOffset + 1;
                } else if (ch == 'b') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('\b');
                    beginOffset = jsonOffset + 1;
                } else if (ch == 'f') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('\f');
                    beginOffset = jsonOffset + 1;
                } else if (ch == 'n') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('\n');
                    beginOffset = jsonOffset + 1;
                } else if (ch == 'r') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('\r');
                    beginOffset = jsonOffset + 1;
                } else if (ch == 't') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append('\t');
                    beginOffset = jsonOffset + 1;
                } else if (ch == 'u') {
                    if (jsonOffset > beginOffset + 1)
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    jsonOffset++;
                    if (jsonOffset >= jsonLength) {
                        return FKJSON_ERROR_END_OF_BUFFER;
                    }
                    ch = jsonCharArray[jsonOffset];
                    if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')) {
                        jsonOffset++;
                        if (jsonOffset >= jsonLength) {
                            return FKJSON_ERROR_END_OF_BUFFER;
                        }
                        ch = jsonCharArray[jsonOffset];
                        if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')) {
                            jsonOffset++;
                            if (jsonOffset >= jsonLength) {
                                return FKJSON_ERROR_END_OF_BUFFER;
                            }
                            ch = jsonCharArray[jsonOffset];
                            if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')) {
                                jsonOffset++;
                                if (jsonOffset >= jsonLength) {
                                    return FKJSON_ERROR_END_OF_BUFFER;
                                }
                                ch = jsonCharArray[jsonOffset];
                                if (('0' <= ch && ch <= '9') || ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z')) {
                                    String unicodeString = "0x" + jsonCharArray[jsonOffset - 3] + jsonCharArray[jsonOffset - 2] + jsonCharArray[jsonOffset - 1] + jsonCharArray[jsonOffset];
                                    int unicodeInt = Integer.decode(unicodeString).intValue();
                                    if (fieldStringBuilder.length() == 0)
                                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - 4 - beginOffset - 1);
                                    fieldStringBuilder.append((char) unicodeInt);
                                    beginOffset = jsonOffset + 1;
                                } else {
                                    fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset);
                                    beginOffset = jsonOffset;
                                    jsonOffset--;
                                }
                            } else {
                                fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset);
                                beginOffset = jsonOffset;
                                jsonOffset--;
                            }
                        } else {
                            fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset);
                            beginOffset = jsonOffset;
                            jsonOffset--;
                        }
                    } else {
                        fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset);
                        beginOffset = jsonOffset;
                        jsonOffset--;
                    }
                } else {
                    fieldStringBuilder.append(jsonCharArray, beginOffset, jsonOffset - beginOffset - 1);
                    fieldStringBuilder.append(ch);
                }
            }

            jsonOffset++;
        }

        return FKJSON_ERROR_END_OF_BUFFER;
    }

    private int tokenJsonNumber(char[] jsonCharArray) {
        char ch;
        boolean decimalPointFlag;

        beginOffset = jsonOffset;

        ch = jsonCharArray[jsonOffset];
        if (ch == '-') {
            jsonOffset++;
        }

        decimalPointFlag = false;
        while (jsonOffset < jsonLength) {
            ch = jsonCharArray[jsonOffset];
            if ('0' <= ch && ch <= '9') {
                jsonOffset++;
            } else if (ch == '.') {
                decimalPointFlag = true;
                jsonOffset++;
            } else if (ch == 'e' || ch == 'E') {
                jsonOffset++;
                if (jsonOffset >= jsonLength) {
                    return FKJSON_ERROR_END_OF_BUFFER;
                }
                ch = jsonCharArray[jsonOffset];
                if (ch == '-' || ch == '+') {
                    jsonOffset++;
                } else if ('0' <= ch && ch <= '9') {
                    jsonOffset++;
                }
            } else {
                if (decimalPointFlag == true)
                    tokenType = TokenType.TOKEN_TYPE_DECIMAL;
                else
                    tokenType = TokenType.TOKEN_TYPE_INTEGER;
                endOffset = jsonOffset - 1;
                return 0;
            }
        }

        return FKJSON_ERROR_END_OF_BUFFER;
    }

    private int tokenJsonWord(char[] jsonCharArray) {
        char ch;

        while (jsonOffset < jsonLength) {
            ch = jsonCharArray[jsonOffset];
            if (ch == ' ' || ch == '\b' || ch == '\t' || ch == '\f' || ch == '\r' || ch == '\n') {
                jsonOffset++;
            } else if (ch == '{') {
                tokenType = TokenType.TOKEN_TYPE_LEFT_BRACE;
                beginOffset = jsonOffset;
                endOffset = jsonOffset;
                jsonOffset++;
                return 0;
            } else if (ch == '}') {
                tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACE;
                beginOffset = jsonOffset;
                endOffset = jsonOffset;
                jsonOffset++;
                return 0;
            } else if (ch == '[') {
                tokenType = TokenType.TOKEN_TYPE_LEFT_BRACKET;
                beginOffset = jsonOffset;
                endOffset = jsonOffset;
                jsonOffset++;
                return 0;
            } else if (ch == ']') {
                tokenType = TokenType.TOKEN_TYPE_RIGHT_BRACKET;
                beginOffset = jsonOffset;
                endOffset = jsonOffset;
                jsonOffset++;
                return 0;
            } else if (ch == '"') {
                return tokenJsonString(jsonCharArray);
            } else if (ch == ':') {
                tokenType = TokenType.TOKEN_TYPE_COLON;
                beginOffset = jsonOffset;
                endOffset = jsonOffset;
                jsonOffset++;
                return 0;
            } else if (ch == ',') {
                tokenType = TokenType.TOKEN_TYPE_COMMA;
                beginOffset = jsonOffset;
                endOffset = jsonOffset;
                jsonOffset++;
                return 0;
            } else if (ch == '-' || ('0' <= ch && ch <= '9')) {
                return tokenJsonNumber(jsonCharArray);
            } else if (ch == 't') {
                beginOffset = jsonOffset;
                jsonOffset++;
                if (jsonOffset >= jsonLength) {
                    return FKJSON_ERROR_END_OF_BUFFER;
                }
                ch = jsonCharArray[jsonOffset];
                if (ch == 'r') {
                    jsonOffset++;
                    if (jsonOffset >= jsonLength) {
                        return FKJSON_ERROR_END_OF_BUFFER;
                    }
                    ch = jsonCharArray[jsonOffset];
                    if (ch == 'u') {
                        jsonOffset++;
                        if (jsonOffset >= jsonLength) {
                            return FKJSON_ERROR_END_OF_BUFFER;
                        }
                        ch = jsonCharArray[jsonOffset];
                        if (ch == 'e') {
                            tokenType = TokenType.TOKEN_TYPE_BOOL;
                            booleanValue = true;
                            endOffset = jsonOffset;
                            jsonOffset++;
                            return 0;
                        }
                    }
                }
            } else if (ch == 'f') {
                beginOffset = jsonOffset;
                jsonOffset++;
                if (jsonOffset >= jsonLength) {
                    return FKJSON_ERROR_END_OF_BUFFER;
                }
                ch = jsonCharArray[jsonOffset];
                if (ch == 'a') {
                    jsonOffset++;
                    if (jsonOffset >= jsonLength) {
                        return FKJSON_ERROR_END_OF_BUFFER;
                    }
                    ch = jsonCharArray[jsonOffset];
                    if (ch == 'l') {
                        jsonOffset++;
                        if (jsonOffset >= jsonLength) {
                            return FKJSON_ERROR_END_OF_BUFFER;
                        }
                        ch = jsonCharArray[jsonOffset];
                        if (ch == 's') {
                            jsonOffset++;
                            if (jsonOffset >= jsonLength) {
                                return FKJSON_ERROR_END_OF_BUFFER;
                            }
                            ch = jsonCharArray[jsonOffset];
                            if (ch == 'e') {
                                tokenType = TokenType.TOKEN_TYPE_BOOL;
                                booleanValue = false;
                                endOffset = jsonOffset;
                                jsonOffset++;
                                return 0;
                            }
                        }
                    }
                }
            } else if (ch == 'n') {
                beginOffset = jsonOffset;
                jsonOffset++;
                if (jsonOffset >= jsonLength) {
                    return FKJSON_ERROR_END_OF_BUFFER;
                }
                ch = jsonCharArray[jsonOffset];
                if (ch == 'u') {
                    jsonOffset++;
                    if (jsonOffset >= jsonLength) {
                        return FKJSON_ERROR_END_OF_BUFFER;
                    }
                    ch = jsonCharArray[jsonOffset];
                    if (ch == 'l') {
                        jsonOffset++;
                        if (jsonOffset >= jsonLength) {
                            return FKJSON_ERROR_END_OF_BUFFER;
                        }
                        ch = jsonCharArray[jsonOffset];
                        if (ch == 'l') {
                            tokenType = TokenType.TOKEN_TYPE_NULL;
                            booleanValue = true;
                            endOffset = jsonOffset;
                            jsonOffset++;
                            return 0;
                        }
                    }
                }
            } else {
                errorDesc = "Invalid byte '" + ch + "'";
                return FKJSON_ERROR_INVALID_BYTE;
            }
        }

        return FKJSON_ERROR_END_OF_BUFFER;
    }

    private int addArrayObject(char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field) {

        try {
            Class<?> clazz = field.getType();
            if (clazz == ArrayList.class || clazz == LinkedList.class) {
                Type type = field.getGenericType();
                ParameterizedType pt = (ParameterizedType) type;
                Class<?> typeClass = (Class<?>) pt.getActualTypeArguments()[0];
                if (typeClass == String.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                        String value = new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1);
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Byte.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                        Byte value = Byte.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Short.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                        Short value = Short.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Integer.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                        Integer value = Integer.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Long.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                        Long value = Long.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Float.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_DECIMAL) {
                        Float value = Float.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Double.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_DECIMAL) {
                        Double value = Double.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                        ((List<Object>) object).add(value);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == Boolean.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_BOOL) {
                        ((List<Object>) object).add(booleanValue);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == LocalDate.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                        FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
                        String defaultDateTimeFormatter;
                        LocalDate localDate;
                        if (field.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                            fkjsonDateTimeFormatter = field.getAnnotation(FkJsonDateTimeFormatter.class);
                            defaultDateTimeFormatter = fkjsonDateTimeFormatter.format();
                        } else {
                            defaultDateTimeFormatter = "yyyy-MM-dd";
                        }
                        localDate = LocalDate.parse(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                        ((List<Object>) object).add(localDate);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == LocalTime.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                        FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
                        String defaultDateTimeFormatter;
                        LocalTime localTime;
                        if (field.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                            fkjsonDateTimeFormatter = field.getAnnotation(FkJsonDateTimeFormatter.class);
                            defaultDateTimeFormatter = fkjsonDateTimeFormatter.format();
                        } else {
                            defaultDateTimeFormatter = "HH:mm:ss";
                        }
                        localTime = LocalTime.parse(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                        ((List<Object>) object).add(localTime);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (typeClass == LocalDateTime.class) {
                    if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                        FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
                        String defaultDateTimeFormatter;
                        LocalDateTime localDateTime;
                        if (field.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                            fkjsonDateTimeFormatter = field.getAnnotation(FkJsonDateTimeFormatter.class);
                            defaultDateTimeFormatter = fkjsonDateTimeFormatter.format();
                        } else {
                            defaultDateTimeFormatter = "yyyy-MM-dd HH:mm:ss";
                        }
                        localDateTime = LocalDateTime.parse(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                        ((List<Object>) object).add(localDateTime);
                    } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                        ;
                    }
                } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
                    ;
                } else {
                    if (strictPolicyEnable == true)
                        return FKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
                }
            } else {
                if (strictPolicyEnable == true)
                    return FKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FKJSON_ERROR_EXCEPTION;
        }

        return 0;
    }

    private int stringToArrayObject(char[] jsonCharArray, Object object, Field field) {

        TokenType valueTokenType;
        int valueBeginOffset;
        int valueEndOffset;

        int nret;

        while (true) {
            // token "value" or '{'
            nret = tokenJsonWord(jsonCharArray);
            if (nret == FKJSON_ERROR_END_OF_BUFFER) {
                break;
            }
            if (nret != 0) {
                return nret;
            }

            if (tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE) {
                try {
                    if (field != null) {
                        Class<?> clazz = field.getType();
                        if (clazz == ArrayList.class || clazz == LinkedList.class) {
                            Type type = field.getGenericType();
                            ParameterizedType pt = (ParameterizedType) type;
                            Class<?> typeClazz = (Class<?>) pt.getActualTypeArguments()[0];
                            Object childObject = typeClazz.newInstance();
                            nret = stringToObjectProperties(jsonCharArray, childObject);
                            if (nret != 0)
                                return nret;

                            ((List<Object>) object).add(childObject);
                        }
                    } else {
                        nret = stringToObjectProperties(jsonCharArray, null);
                        if (nret != 0)
                            return nret;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            } else if (tokenType == TokenType.TOKEN_TYPE_STRING || tokenType == TokenType.TOKEN_TYPE_INTEGER || tokenType == TokenType.TOKEN_TYPE_DECIMAL || tokenType == TokenType.TOKEN_TYPE_BOOL) {
                ;
            } else {
                int beginPos = endOffset - 16;
                if (beginPos < 0)
                    beginPos = 0;
                errorDesc = "unexpect \"" + String.copyValueOf(jsonCharArray, beginOffset, endOffset - beginOffset + 1) + "\"";
                return FKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE;
            }

            valueTokenType = tokenType;
            valueBeginOffset = beginOffset;
            valueEndOffset = endOffset;

            // token ',' or ']'
            nret = tokenJsonWord(jsonCharArray);
            if (nret == FKJSON_ERROR_END_OF_BUFFER) {
                break;
            }
            if (nret != 0) {
                return nret;
            }

            if (tokenType == TokenType.TOKEN_TYPE_COMMA || tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET) {
                if (object != null && field != null) {
                    errorCode = addArrayObject(jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, object, field);
                    if (errorCode != 0)
                        return errorCode;
                }

                if (tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET)
                    break;
            } else {
                errorDesc = "unexpect \"" + String.copyValueOf(jsonCharArray, beginOffset, endOffset - beginOffset + 1) + "\"";
                return FKJSON_ERROR_UNEXPECT_TOKEN_AFTER_LEFT_BRACE;
            }
        }

        return 0;
    }

    private int setObjectProperty(char[] jsonCharArray, TokenType valueTokenType, int valueBeginOffset, int valueEndOffset, Object object, Field field, Method method) {

        StringBuilder fieldStringBuilder;

        fieldStringBuilder = fieldStringBuilderCache.get();

        if (field.getType() == String.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                try {
                    String value;
                    if (fieldStringBuilder.length() > 0) {
                        value = fieldStringBuilder.toString();
                    } else {
                        value = new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1);
                    }
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Byte.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                try {
                    Byte value = Byte.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Short.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                try {
                    Short value = Short.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Integer.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                try {
                    Integer value = Integer.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Long.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
                try {
                    Long value = Long.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Float.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_DECIMAL) {
                try {
                    Float value = Float.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Double.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_DECIMAL) {
                try {
                    Double value = Double.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == Boolean.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_BOOL) {
                try {
                    Boolean value = Boolean.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1));
                    if (method != null) {
                        method.invoke(object, value);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, value);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType().getName().equals("byte") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
            try {
                byte value = Integer.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1)).byteValue();
                if (method != null) {
                    method.invoke(object, value);
                } else if (directAccessPropertyEnable == true) {
                    field.setByte(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType().getName().equals("short") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
            try {
                short value = Integer.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1)).shortValue();
                if (method != null) {
                    method.invoke(object, value);
                } else if (directAccessPropertyEnable == true) {
                    field.setShort(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType().getName().equals("int") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
            try {
                int value = Integer.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1)).intValue();
                if (method != null) {
                    method.invoke(object, value);
                } else if (directAccessPropertyEnable == true) {
                    field.setInt(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType().getName().equals("long") && valueTokenType == TokenType.TOKEN_TYPE_INTEGER) {
            try {
                long value = Long.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1)).longValue();
                if (method != null) {
                    method.invoke(object, value);
                } else if (directAccessPropertyEnable == true) {
                    field.setLong(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType().getName().equals("float") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL) {
            try {
                float value = Float.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1)).floatValue();
                if (method != null) {
                    method.invoke(object, value);
                } else if (directAccessPropertyEnable == true) {
                    field.setFloat(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType().getName().equals("double") && valueTokenType == TokenType.TOKEN_TYPE_DECIMAL) {
            try {
                double value = Double.valueOf(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1)).doubleValue();
                if (method != null) {
                    method.invoke(object, value);
                } else if (directAccessPropertyEnable == true) {
                    field.setDouble(object, value);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType().getName().equals("boolean") && valueTokenType == TokenType.TOKEN_TYPE_BOOL) {
            try {
                if (method != null) {
                    method.invoke(object, booleanValue);
                } else if (directAccessPropertyEnable == true) {
                    field.setBoolean(object, booleanValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else if (field.getType() == LocalDate.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                try {
                    FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
                    String defaultDateTimeFormatter;
                    LocalDate localDate;
                    if (field.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                        fkjsonDateTimeFormatter = field.getAnnotation(FkJsonDateTimeFormatter.class);
                        defaultDateTimeFormatter = fkjsonDateTimeFormatter.format();
                    } else {
                        defaultDateTimeFormatter = "yyyy-MM-dd";
                    }
                    if (fieldStringBuilder.length() > 0) {
                        localDate = LocalDate.parse(fieldStringBuilder.toString(), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                    } else {
                        localDate = LocalDate.parse(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                    }
                    if (method != null) {
                        method.invoke(object, localDate);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, localDate);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == LocalTime.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                try {
                    FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
                    String defaultDateTimeFormatter;
                    LocalTime localTime;
                    if (field.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                        fkjsonDateTimeFormatter = field.getAnnotation(FkJsonDateTimeFormatter.class);
                        defaultDateTimeFormatter = fkjsonDateTimeFormatter.format();
                    } else {
                        defaultDateTimeFormatter = "HH:mm:ss";
                    }
                    if (fieldStringBuilder.length() > 0) {
                        localTime = LocalTime.parse(fieldStringBuilder.toString(), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                    } else {
                        localTime = LocalTime.parse(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                    }
                    if (method != null) {
                        method.invoke(object, localTime);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, localTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (field.getType() == LocalDateTime.class) {
            if (valueTokenType == TokenType.TOKEN_TYPE_STRING) {
                try {
                    FkJsonDateTimeFormatter fkjsonDateTimeFormatter;
                    String defaultDateTimeFormatter;
                    LocalDateTime localDateTime;
                    if (field.isAnnotationPresent(FkJsonDateTimeFormatter.class)) {
                        fkjsonDateTimeFormatter = field.getAnnotation(FkJsonDateTimeFormatter.class);
                        defaultDateTimeFormatter = fkjsonDateTimeFormatter.format();
                    } else {
                        defaultDateTimeFormatter = "yyyy-MM-dd HH:mm:ss";
                    }
                    if (fieldStringBuilder.length() > 0) {
                        localDateTime = LocalDateTime.parse(fieldStringBuilder.toString(), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                    } else {
                        localDateTime = LocalDateTime.parse(new String(jsonCharArray, valueBeginOffset, valueEndOffset - valueBeginOffset + 1), DateTimeFormatter.ofPattern(defaultDateTimeFormatter));
                    }
                    if (method != null) {
                        method.invoke(object, localDateTime);
                    } else if (directAccessPropertyEnable == true) {
                        field.set(object, localDateTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            }
        } else if (valueTokenType == TokenType.TOKEN_TYPE_NULL) {
            try {
                if (method != null) {
                    method.invoke(object, null);
                } else if (directAccessPropertyEnable == true) {
                    field.set(object, null);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return FKJSON_ERROR_EXCEPTION;
            }
        } else {
            if (strictPolicyEnable == true)
                return FKJSON_ERROR_PORPERTY_TYPE_NOT_MATCH_IN_OBJECT;
        }

        return 0;
    }

    private int stringToObjectProperties(char[] jsonCharArray, Object object) {

        Class clazz;
        HashMap<String, Field> stringMapFields;
        HashMap<String, Method> stringMapMethods;
        Field[] fields;
        Field field;
        Method method = null;
        TokenType fieldNameTokenType;
        int fieldNameBeginOffset;
        int fieldNameEndOffset;
        String fieldName;
        TokenType valueTokenType;
        int valueBeginOffset;
        int valueEndOffset;

        int nret;

        if (object != null) {
            clazz = object.getClass();

            stringMapFields = stringMapFieldsCache.get().get(clazz.getName());
            if (stringMapFields == null) {
                stringMapFields = new HashMap<String, Field>();
                stringMapFieldsCache.get().put(clazz.getName(), stringMapFields);
            }

            stringMapMethods = stringMapMethodsCache.get().get(clazz.getName());
            if (stringMapMethods == null) {
                stringMapMethods = new HashMap<String, Method>();
                stringMapMethodsCache.get().put(clazz.getName(), stringMapMethods);
            }

            if (stringMapFields.isEmpty()) {
                fields = clazz.getDeclaredFields();
                for (Field f : fields) {
                    f.setAccessible(true);

                    fieldName = f.getName();

                    method = null;
                    try {
                        method = clazz.getMethod("set" + fieldName.substring(0, 1).toUpperCase(Locale.getDefault()) + fieldName.substring(1), f.getType());
                        method.setAccessible(true);
                    } catch (NoSuchMethodException e2) {
                        ;
                    } catch (SecurityException e2) {
                        ;
                    }


                    if (method != null && Modifier.isPublic(method.getModifiers())) {
                        stringMapMethods.put(fieldName, method);
                        stringMapFields.put(fieldName, f);
                    } else if (Modifier.isPublic(f.getModifiers())) {
                        stringMapFields.put(fieldName, f);
                    }
                }
            }
        } else {
            stringMapFields = null;
            stringMapMethods = null;
        }

        while (true) {
            // token "name"
            nret = tokenJsonWord(jsonCharArray);
            if (nret == FKJSON_ERROR_END_OF_BUFFER) {
                break;
            }
            if (nret != 0) {
                return nret;
            }

            fieldNameTokenType = tokenType;
            fieldNameBeginOffset = beginOffset;
            fieldNameEndOffset = endOffset;
            fieldName = new String(jsonCharArray, fieldNameBeginOffset, fieldNameEndOffset - fieldNameBeginOffset + 1);

            if (object != null) {
                field = stringMapFields.get(fieldName);
                if (field == null) {
                    if (strictPolicyEnable == true)
                        return FKJSON_ERROR_NAME_NOT_FOUND_IN_OBJECT;
                }

                method = stringMapMethods.get(fieldName);
            } else {
                field = null;
                method = null;
            }

            if (tokenType != TokenType.TOKEN_TYPE_STRING) {
                errorDesc = "expect a name but \"" + String.copyValueOf(jsonCharArray, beginOffset, endOffset - beginOffset + 1) + "\"";
                return FKJSON_ERROR_NAME_INVALID;
            }

            // token ':' or ',' or '}' or ']'
            nret = tokenJsonWord(jsonCharArray);
            if (nret == FKJSON_ERROR_END_OF_BUFFER) {
                break;
            }
            if (nret != 0) {
                return nret;
            }

            if (tokenType == TokenType.TOKEN_TYPE_COLON) {
                ;
            } else if (tokenType == TokenType.TOKEN_TYPE_COMMA || tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE) {
                clazz = field.getType();
                if (clazz == ArrayList.class || clazz == LinkedList.class) {
                    nret = addArrayObject(jsonCharArray, fieldNameTokenType, fieldNameBeginOffset, fieldNameEndOffset, object, field);
                    if (nret != 0)
                        return nret;

                    if (tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE)
                        break;
                }
            } else if (tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET) {
                break;
            } else {
                errorDesc = "expect ':' but \"" + String.copyValueOf(jsonCharArray, beginOffset, endOffset - beginOffset + 1) + "\"";
                return FKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
            }

            // token '{' or '[' or "value"
            nret = tokenJsonWord(jsonCharArray);
            if (nret == FKJSON_ERROR_END_OF_BUFFER) {
                break;
            }
            if (nret != 0) {
                return nret;
            }

            valueTokenType = tokenType;
            valueBeginOffset = beginOffset;
            valueEndOffset = endOffset;

            if (tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE || tokenType == TokenType.TOKEN_TYPE_LEFT_BRACKET) {
                try {
                    Object childObject;

                    if (field != null) {
                        childObject = field.getType().newInstance();
                        if (childObject == null)
                            return FKJSON_ERROR_UNEXPECT;
                    } else {
                        childObject = null;
                    }

                    if (tokenType == TokenType.TOKEN_TYPE_LEFT_BRACE) {
                        nret = stringToObjectProperties(jsonCharArray, childObject);
                    } else {
                        nret = stringToArrayObject(jsonCharArray, childObject, field);
                    }
                    if (nret != 0)
                        return nret;

                    if (field != null) {
                        field.set(object, childObject);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return FKJSON_ERROR_EXCEPTION;
                }
            } else {
                if (object != null && field != null) {
                    nret = setObjectProperty(jsonCharArray, valueTokenType, valueBeginOffset, valueEndOffset, object, field, method);
                    if (nret != 0)
                        return nret;
                }
            }

            // token ',' or '}' or ']'
            nret = tokenJsonWord(jsonCharArray);
            if (nret == FKJSON_ERROR_END_OF_BUFFER) {
                break;
            }
            if (nret != 0) {
                return nret;
            }

            if (tokenType == TokenType.TOKEN_TYPE_COMMA) {
                ;
            } else if (tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACE) {
                break;
            } else if (tokenType == TokenType.TOKEN_TYPE_RIGHT_BRACKET) {
                break;
            } else {
                errorDesc = "expect ',' or '}' or ']' but \"" + String.copyValueOf(jsonCharArray, beginOffset, endOffset - beginOffset + 1) + "\"";
                return FKJSON_ERROR_EXPECT_COLON_AFTER_NAME;
            }
        }

        return 0;
    }

    public <T> T fileToObject(String filePath, T object) {
        String jsonString = null;

        try {
            jsonString = new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            return null;
        }

        return stringToObject(jsonString, object);
    }

    public <T> T stringToObject(String jsonString, T object) {
        char[] jsonCharArray;

        jsonCharArray = jsonString.toCharArray();
        jsonOffset = 0;
        jsonLength = jsonCharArray.length;

        if (stringMapFieldsCache == null) {
            stringMapFieldsCache = new ThreadLocal<HashMap<String, HashMap<String, Field>>>();
            if (stringMapFieldsCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            stringMapFieldsCache.set(new HashMap<String, HashMap<String, Field>>());
        }

        if (stringMapMethodsCache == null) {
            stringMapMethodsCache = new ThreadLocal<HashMap<String, HashMap<String, Method>>>();
            if (stringMapMethodsCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            stringMapMethodsCache.set(new HashMap<String, HashMap<String, Method>>());
        }

        if (fieldStringBuilderCache == null) {
            fieldStringBuilderCache = new ThreadLocal<StringBuilder>();
            if (fieldStringBuilderCache == null) {
                errorDesc = "New object failed for clazz";
                errorCode = FKJSON_ERROR_NEW_OBJECT;
                return null;
            }
            fieldStringBuilderCache.set(new StringBuilder(1024));
        }

        errorCode = tokenJsonWord(jsonCharArray);
        if (errorCode != 0) {
            return null;
        }

        if (tokenType != TokenType.TOKEN_TYPE_LEFT_BRACE) {
            errorCode = FKJSON_ERROR_FIND_FIRST_LEFT_BRACE;
            return null;
        }

        errorCode = stringToObjectProperties(jsonCharArray, object);
        if (errorCode != 0)
            return null;

        return object;
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

    public FkJsonParser() {
        this.strictPolicyEnable = false;
        this.directAccessPropertyEnable = false;
        this.prettyFormatEnable = false;
        this.errorCode = 0;
        this.errorDesc = null;
    }
}
