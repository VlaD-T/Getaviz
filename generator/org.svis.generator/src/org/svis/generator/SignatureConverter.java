/*
 * Bytecode Analysis Framework
 * Copyright (C) 2003,2004 University of Maryland
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.svis.generator;

/**
 * Convert part or all of a Java type signature into something closer to what
 * types look like in the source code. Both field and method signatures may be
 * processed by this class. For a field signature, just call parseNext() once.
 * For a method signature, parseNext() must be called multiple times, and the
 * parens around the arguments must be skipped manually (by calling the skip()
 * method).
 *
 * @author David Hovemeyer
 */

public class SignatureConverter {
    private String signature;

    /**
     * Constructor.
     *
     * @param signature
     *            the field or method signature to convert
     */
    public SignatureConverter(String signature) {
        this.signature = signature;
    }

    /**
     * Get the first character of the remaining part of the signature.
     * 
     * @return first character of the remaining part of the signature
     */
    public char getFirst() {
        return signature.charAt(0);
    }

    /**
     * Skip the first character of the remaining part of the signature.
     */
    public void skip() {
        signature = signature.substring(1);
    }

    /**
     * Parse a single type out of the signature, starting at the beginning of
     * the remaining part of the signature. For example, if the first character
     * of the remaining part is "I", then this method will return "int", and the
     * "I" will be consumed. Arrays, reference types, and basic types are all
     * handled.
     *
     * @return the parsed type string
     */
    public String parseNext() {
        StringBuilder result = new StringBuilder();

        if (signature.startsWith("[")) {
            int dimensions = 0;
            do {
                ++dimensions;
                signature = signature.substring(1);
            } while (signature.charAt(0) == '[');
            result.append(parseNext());

            while (dimensions-- > 0) {
                result.append("[]");
            }
        } else if (signature.startsWith("L")) {
            int semi = signature.indexOf(';');
            if (semi < 0) {
                throw new IllegalStateException("missing semicolon in signature " + signature);
            }
            result.append(signature.substring(1, semi).replace('/', '.'));
            signature = signature.substring(semi + 1);
        } else {
            switch (signature.charAt(0)) {
            case 'B':
                result.append("byte");
                break;
            case 'C':
                result.append("char");
                break;
            case 'D':
                result.append("double");
                break;
            case 'F':
                result.append("float");
                break;
            case 'I':
                result.append("int");
                break;
            case 'J':
                result.append("long");
                break;
            case 'S':
                result.append("short");
                break;
            case 'Z':
                result.append("boolean");
                break;
            case 'V':
                result.append("void");
                break;
            default:
                System.out.println("Converter: " + signature);
            	throw new IllegalArgumentException("bad signature " + signature);
            }
            skip();
        }

        return result.toString();
    }

   /**
     * Convenience method for generating a method signature in human readable
     * form.
     *
     * @param className
     *            name of the class containing the method
     * @param methodName
     *            the name of the method
     * @param methodSig
     *            the signature of the method
     * @return human readable method signature
     */
    public static String convertMethodSignature(String className, String methodName, String methodSig) {
        return convertMethodSignature(className, methodName, methodSig, "");
    }

    /**
     * Convenience method for generating a method signature in human readable
     * form.
     *
     * @param className
     *            name of the class containing the method
     * @param methodName
     *            the name of the method
     * @param methodSig
     *            the signature of the method
     * @param pkgName
     *            the name of the package the method is in (used to shorten
     *            class names)
     *            
     * @return human readable method signature
     */
    public static String convertMethodSignature(String className, String methodName, String methodSig, String pkgName) {
        StringBuilder args = new StringBuilder();
        SignatureConverter converter = new SignatureConverter(methodSig);

        converter.skip();
        args.append('(');

        while (converter.getFirst() != ')') {
            if (args.length() > 1) {
                args.append(", ");
            }
            //args.append(shorten(pkgName, converter.parseNext()));
            // do not shorten names
            args.append(converter.parseNext());
        }
        converter.skip();
        args.append(')');

        // Ignore return type

        StringBuilder result = new StringBuilder();
        result.append(className);
        result.append('.');
        result.append(methodName);
        result.append(args.toString());

        return result.toString();
    }

    /**
     * Convenience method for converting a single signature component to
     * human-readable form.
     *
     * @param signature
     *            the signature
     * @return human readable method signature
     */
    public static String convert(String signature) {
        return new SignatureConverter(signature).parseNext();
    }

    public static String shorten(String pkgName, String typeName) {
        int index = typeName.lastIndexOf('.');
        if (index >= 0) {
            String otherPkg = typeName.substring(0, index);
            if (otherPkg.equals(pkgName) || "java.lang".equals(otherPkg)) {
                typeName = typeName.substring(index + 1);
            }
        }
        return typeName;
    }
}

