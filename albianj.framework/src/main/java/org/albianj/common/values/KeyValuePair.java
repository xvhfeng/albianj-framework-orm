package org.albianj.common.values;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KeyValuePair<K,V> {
    private K key;
    private V value;
}
