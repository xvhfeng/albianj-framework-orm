/* *******************************************************************
 * Copyright (c) 2002 Palo Alto Research Center, Incorporated (PARC).
 * All rights reserved.
 * This program and the accompanying materials are made available
 * under the terms of the Eclipse Public License v 2.0
 * which accompanies this distribution and is available at
 * https://www.eclipse.org/org/documents/epl-2.0/EPL-2.0.txt
 *
 * Contributors:
 *     Xerox/PARC     initial implementation
 * ******************************************************************/

package org.albianj.common.langs;

import java.io.DataOutputStream;
import java.io.IOException;

public class AspectjTypeSafeEnum {
	private final byte key;
	private final String name;

	public AspectjTypeSafeEnum(String name, int key) {
		this.name = name;
		if (key > Byte.MAX_VALUE || key < Byte.MIN_VALUE) {
			throw new IllegalArgumentException("key doesn't fit into a byte: " + key);
		}
		this.key = (byte) key;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	public byte getKey() {
		return key;
	}

	public void write(DataOutputStream s) throws IOException {
		s.writeByte(key);
	}
	@Override
	public int hashCode() {
		return name.hashCode()*37+key;
	}

	@Override
	public boolean equals(Object o) {
		return (o instanceof AspectjTypeSafeEnum) &&
			((AspectjTypeSafeEnum)o).key == key &&
			((AspectjTypeSafeEnum)o).name.equals(name);
	}
}
