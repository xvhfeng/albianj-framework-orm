/* *******************************************************************
 * Copyright (c) 2005-2008 Contributors.
 * All rights reserved.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v 2.0
 * which accompanies this distribution and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt
 *
 * ******************************************************************/
package org.albianj.common.langs;

import java.util.ArrayList;
import java.util.List;

/**
 * Parses the generic signature attribute as defined in the JVM spec.
 *
 * @author Adrian Colyer
 * @author Andy Clement
 */
public class AspectjGenericSignatureParser {

	private String inputString;
	private String[] tokenStream; // for parse in flight
	private int tokenIndex = 0;

	/**
	 * AMC. Parse the signature string interpreting it as a ClassSignature according to the grammar defined in Section 4.4.4 of the
	 * JVM specification.
	 */
	public AspectjGenericSignature.ClassSignature parseAsClassSignature(String sig) {
		this.inputString = sig;
		tokenStream = tokenize(sig);
		tokenIndex = 0;
		AspectjGenericSignature.ClassSignature classSig = new AspectjGenericSignature.ClassSignature();
		// FormalTypeParameters-opt
		if (maybeEat("<")) {
			List<AspectjGenericSignature.FormalTypeParameter> formalTypeParametersList = new ArrayList<>();
			do {
				formalTypeParametersList.add(parseFormalTypeParameter());
			} while (!maybeEat(">"));
			classSig.formalTypeParameters = new AspectjGenericSignature.FormalTypeParameter[formalTypeParametersList.size()];
			formalTypeParametersList.toArray(classSig.formalTypeParameters);
		}
		classSig.superclassSignature = parseClassTypeSignature();
		List<AspectjGenericSignature.ClassTypeSignature> superIntSigs = new ArrayList<>();
		while (tokenIndex < tokenStream.length) {
			superIntSigs.add(parseClassTypeSignature());
		}
		classSig.superInterfaceSignatures = new AspectjGenericSignature.ClassTypeSignature[superIntSigs.size()];
		superIntSigs.toArray(classSig.superInterfaceSignatures);
		return classSig;
	}

	/**
	 * AMC. Parse the signature string interpreting it as a MethodTypeSignature according to the grammar defined in Section 4.4.4 of
	 * the JVM specification.
	 */
	public AspectjGenericSignature.MethodTypeSignature parseAsMethodSignature(String sig) {
		this.inputString = sig;
		tokenStream = tokenize(sig);
		tokenIndex = 0;
		AspectjGenericSignature.FormalTypeParameter[] formals = AspectjGenericSignature.FormalTypeParameter.NONE;
		AspectjGenericSignature.TypeSignature returnType = null;
		// FormalTypeParameters-opt
		if (maybeEat("<")) {
			List<AspectjGenericSignature.FormalTypeParameter> formalTypeParametersList = new ArrayList<>();
			do {
				formalTypeParametersList.add(parseFormalTypeParameter());
			} while (!maybeEat(">"));
			formals = new AspectjGenericSignature.FormalTypeParameter[formalTypeParametersList.size()];
			formalTypeParametersList.toArray(formals);
		}
		// Parameters
		eat("(");
		List<AspectjGenericSignature.TypeSignature> paramList = new ArrayList<>();
		while (!maybeEat(")")) {
			AspectjGenericSignature.FieldTypeSignature fsig = parseFieldTypeSignature(true);
			if (fsig != null) {
				paramList.add(fsig);
			} else {
				paramList.add(new AspectjGenericSignature.BaseTypeSignature(eatIdentifier()));
			}
		}
		AspectjGenericSignature.TypeSignature[] params = new AspectjGenericSignature.TypeSignature[paramList.size()];
		paramList.toArray(params);
		// return type
		returnType = parseFieldTypeSignature(true);
		if (returnType == null)
			returnType = new AspectjGenericSignature.BaseTypeSignature(eatIdentifier());
		// throws
		List<AspectjGenericSignature.FieldTypeSignature> throwsList = new ArrayList<>();
		while (maybeEat("^")) {
			AspectjGenericSignature.FieldTypeSignature fsig = parseFieldTypeSignature(false);
			throwsList.add(fsig);
		}
		AspectjGenericSignature.FieldTypeSignature[] throwsSigs = new AspectjGenericSignature.FieldTypeSignature[throwsList.size()];
		throwsList.toArray(throwsSigs);
		return new AspectjGenericSignature.MethodTypeSignature(formals, params, returnType, throwsSigs);
	}

	/**
	 * AMC. Parse the signature string interpreting it as a FieldTypeSignature according to the grammar defined in Section 4.4.4 of
	 * the JVM specification.
	 */
	public AspectjGenericSignature.FieldTypeSignature parseAsFieldSignature(String sig) {
		this.inputString = sig;
		tokenStream = tokenize(sig);
		tokenIndex = 0;
		return parseFieldTypeSignature(false);
	}

	private AspectjGenericSignature.FormalTypeParameter parseFormalTypeParameter() {
		AspectjGenericSignature.FormalTypeParameter ftp = new AspectjGenericSignature.FormalTypeParameter();
		// Identifier
		ftp.identifier = eatIdentifier();
		// ClassBound
		eat(":");
		ftp.classBound = parseFieldTypeSignature(true);
		if (ftp.classBound == null) {
			ftp.classBound = new AspectjGenericSignature.ClassTypeSignature("Ljava/lang/Object;", "Ljava/lang/Object");
		}
		// Optional InterfaceBounds
		List<AspectjGenericSignature.FieldTypeSignature> optionalBounds = new ArrayList<>();
		while (maybeEat(":")) {
			optionalBounds.add(parseFieldTypeSignature(false));
		}
		ftp.interfaceBounds = new AspectjGenericSignature.FieldTypeSignature[optionalBounds.size()];
		optionalBounds.toArray(ftp.interfaceBounds);
		return ftp;
	}

	private AspectjGenericSignature.FieldTypeSignature parseFieldTypeSignature(boolean isOptional) {
		if (isOptional) {
			// anything other than 'L', 'T' or '[' and we're out of here
			if (!tokenStream[tokenIndex].startsWith("L") && !tokenStream[tokenIndex].startsWith("T")
					&& !tokenStream[tokenIndex].startsWith("[")) {
				return null;
			}
		}
		if (maybeEat("[")) {
			return parseArrayTypeSignature();
		} else if (tokenStream[tokenIndex].startsWith("L")) {
			return parseClassTypeSignature();
		} else if (tokenStream[tokenIndex].startsWith("T")) {
			return parseTypeVariableSignature();
		} else {
			throw new IllegalStateException("Expecting [,L, or T, but found " + tokenStream[tokenIndex] + " while unpacking "
					+ inputString);
		}
	}

	private AspectjGenericSignature.ArrayTypeSignature parseArrayTypeSignature() {
		// opening [ already eaten
		AspectjGenericSignature.FieldTypeSignature fieldType = parseFieldTypeSignature(true);
		if (fieldType != null) {
			return new AspectjGenericSignature.ArrayTypeSignature(fieldType);
		} else {
			// must be BaseType array
			return new AspectjGenericSignature.ArrayTypeSignature(new AspectjGenericSignature.BaseTypeSignature(eatIdentifier()));
		}
	}

	// L PackageSpecifier* SimpleClassTypeSignature ClassTypeSignature* ;
	private AspectjGenericSignature.ClassTypeSignature parseClassTypeSignature() {
		AspectjGenericSignature.SimpleClassTypeSignature outerType = null;
		AspectjGenericSignature.SimpleClassTypeSignature[] nestedTypes = new AspectjGenericSignature.SimpleClassTypeSignature[0];
		StringBuilder ret = new StringBuilder();
		String identifier = eatIdentifier();
		ret.append(identifier);
		while (maybeEat("/")) {
			ret.append("/"); // dont forget this...
			ret.append(eatIdentifier());
		}
		identifier = ret.toString();
		// now we have either a "." indicating the start of a nested type,
		// or a "<" indication type arguments, or ";" and we are done.
		while (!maybeEat(";")) {
			if (tokenStream[tokenIndex].equals(".")) {
				// outer type completed
				outerType = new AspectjGenericSignature.SimpleClassTypeSignature(identifier);
				nestedTypes = parseNestedTypesHelper(ret);
			} else if (tokenStream[tokenIndex].equals("<")) {
				ret.append("<");
				AspectjGenericSignature.TypeArgument[] tArgs = maybeParseTypeArguments();
				for (AspectjGenericSignature.TypeArgument tArg : tArgs) {
					ret.append(tArg.toString());
				}
				ret.append(">");
				outerType = new AspectjGenericSignature.SimpleClassTypeSignature(identifier, tArgs);
				nestedTypes = parseNestedTypesHelper(ret);
			} else {
				throw new IllegalStateException("Expecting .,<, or ;, but found " + tokenStream[tokenIndex] + " while unpacking "
						+ inputString);
			}
		}
		ret.append(";");
		if (outerType == null)
			outerType = new AspectjGenericSignature.SimpleClassTypeSignature(ret.toString());
		return new AspectjGenericSignature.ClassTypeSignature(ret.toString(), outerType, nestedTypes);
	}

	/**
	 * Helper method to digest nested types, slightly more complex than necessary to cope with some android related
	 * incorrect classes (see bug 406167)
	 */
	private AspectjGenericSignature.SimpleClassTypeSignature[] parseNestedTypesHelper(StringBuilder ret) {
		boolean brokenSignature = false;
		AspectjGenericSignature.SimpleClassTypeSignature[] nestedTypes;
		List<AspectjGenericSignature.SimpleClassTypeSignature> nestedTypeList = new ArrayList<>();
		while (maybeEat(".")) {
			ret.append(".");
			AspectjGenericSignature.SimpleClassTypeSignature sig = parseSimpleClassTypeSignature();
			if (tokenStream[tokenIndex].equals("/")) {
				if (!brokenSignature) {
					System.err.println("[See bug 406167] Bad class file signature encountered, nested types appear package qualified, ignoring those incorrect pieces. Signature: "+inputString);
				}
				brokenSignature = true;
				// hit something like: Lcom/a/a/b/t<TK;TV;>.com/a/a/b/af.com/a/a/b/ag;
				// and we are looking at the '/' after the com
				tokenIndex++; // pointing at the next identifier
				while (tokenStream[tokenIndex+1].equals("/")) {
					tokenIndex+=2; // jump over an 'identifier' '/' pair
				}
				// now tokenIndex is the final bit of the name (which we'll treat as the inner type name)
				sig = parseSimpleClassTypeSignature();
			}
			ret.append(sig.toString());
			nestedTypeList.add(sig);
		};
		nestedTypes = new AspectjGenericSignature.SimpleClassTypeSignature[nestedTypeList.size()];
		nestedTypeList.toArray(nestedTypes);
		return nestedTypes;
	}

	private AspectjGenericSignature.SimpleClassTypeSignature parseSimpleClassTypeSignature() {
		String identifier = eatIdentifier();
		AspectjGenericSignature.TypeArgument[] tArgs = maybeParseTypeArguments();
		if (tArgs != null) {
			return new AspectjGenericSignature.SimpleClassTypeSignature(identifier, tArgs);
		} else {
			return new AspectjGenericSignature.SimpleClassTypeSignature(identifier);
		}
	}

	private AspectjGenericSignature.TypeArgument parseTypeArgument() {
		boolean isPlus = false;
		boolean isMinus = false;
		if (maybeEat("*")) {
			return new AspectjGenericSignature.TypeArgument();
		} else if (maybeEat("+")) {
			isPlus = true;
		} else if (maybeEat("-")) {
			isMinus = true;
		}
		AspectjGenericSignature.FieldTypeSignature sig = parseFieldTypeSignature(false);
		return new AspectjGenericSignature.TypeArgument(isPlus, isMinus, sig);
	}

	private AspectjGenericSignature.TypeArgument[] maybeParseTypeArguments() {
		if (maybeEat("<")) {
			List<AspectjGenericSignature.TypeArgument> typeArgs = new ArrayList<>();
			do {
				AspectjGenericSignature.TypeArgument arg = parseTypeArgument();
				typeArgs.add(arg);
			} while (!maybeEat(">"));
			AspectjGenericSignature.TypeArgument[] tArgs = new AspectjGenericSignature.TypeArgument[typeArgs.size()];
			typeArgs.toArray(tArgs);
			return tArgs;
		} else {
			return null;
		}
	}

	private AspectjGenericSignature.TypeVariableSignature parseTypeVariableSignature() {
		AspectjGenericSignature.TypeVariableSignature tv = new AspectjGenericSignature.TypeVariableSignature(eatIdentifier());
		eat(";");
		return tv;
	}

	private boolean maybeEat(String token) {
		if (tokenStream.length <= tokenIndex)
			return false;
		if (tokenStream[tokenIndex].equals(token)) {
			tokenIndex++;
			return true;
		}
		return false;
	}

	private void eat(String token) {
		if (!tokenStream[tokenIndex].equals(token)) {
			throw new IllegalStateException("Expecting " + token + " but found " + tokenStream[tokenIndex] + " while unpacking "
					+ inputString);
		}
		tokenIndex++;
	}

	private String eatIdentifier() {
		return tokenStream[tokenIndex++];
	}

	/**
	 * non-private for test visibility Splits a string containing a generic signature into tokens for consumption by the parser.
	 */
	public String[] tokenize(String signatureString) {
		char[] chars = signatureString.toCharArray();
		int index = 0;
		List<String> tokens = new ArrayList<>();
		StringBuilder identifier = new StringBuilder();
		boolean inParens = false;
		boolean inArray = false;
		boolean couldSeePrimitive = false;
		do {
			switch (chars[index]) {
			case '<':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				tokens.add("<");
				break;
			case '>':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				tokens.add(">");
				break;
			case ':':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				tokens.add(":");
				break;
			case '/':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				tokens.add("/");
				couldSeePrimitive = false;
				break;
			case ';':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				tokens.add(";");
				couldSeePrimitive = true;
				inArray = false;
				break;
			case '^':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				tokens.add("^");
				break;
			case '+':
				tokens.add("+");
				break;
			case '-':
				tokens.add("-");
				break;
			case '*':
				tokens.add("*");
				break;
			case '.':
				if (identifier.length() > 0)
					tokens.add(identifier.toString());
				identifier = new StringBuilder();
				couldSeePrimitive = false;
				tokens.add(".");
				break;
			case '(':
				tokens.add("(");
				inParens = true;
				couldSeePrimitive = true;
				break;
			case ')':
				tokens.add(")");
				inParens = false;
				break;
			case '[':
				tokens.add("[");
				couldSeePrimitive = true;
				inArray = true;
				break;
			case 'B':
			case 'C':
			case 'D':
			case 'F':
			case 'I':
			case 'J':
			case 'S':
			case 'V':
			case 'Z':
				if ((inParens || inArray) && couldSeePrimitive && identifier.length() == 0) {
					tokens.add(new String("" + chars[index]));
				} else {
					identifier.append(chars[index]);
				}
				inArray = false;
				break;
			case 'L':
				couldSeePrimitive = false;
				// deliberate fall-through
			default:
				identifier.append(chars[index]);
			}
		} while ((++index) < chars.length);
		if (identifier.length() > 0)
			tokens.add(identifier.toString());
		String[] tokenArray = new String[tokens.size()];
		tokens.toArray(tokenArray);
		return tokenArray;
	}

}
