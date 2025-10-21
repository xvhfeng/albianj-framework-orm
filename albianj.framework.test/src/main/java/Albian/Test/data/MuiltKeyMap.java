package Albian.Test.data;

import java.sql.Ref;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.BiPredicate;

public class MuiltKeyMap {
    private IdentityHashMap<Object,ValRef> valMap;
    private HashMap<Object,ValRef> map;

    public MuiltKeyMap(){
        valMap = new IdentityHashMap<>();
        map = new HashMap<>();
    }

    public void  put(Object key,Object value){
        if(map.containsKey(key)) {
            map.get(key).setValue(value);
        } else {
            if(valMap.containsKey(value)) {
                ValRef valRef = valMap.get(value);
                map.put(key,valRef);
                valRef.add(key);
            } else{
                ValRef valRef = new ValRef(key,value);
                valMap.put(value,valRef);
                map.put(key,valRef);
            }
        }
    }

    public void  putIfAbsent(Object key,Object value){
        if(!map.containsKey(key)) {
            if(valMap.containsKey(value)) {
                ValRef valRef = valMap.get(value);
                map.putIfAbsent(key,valRef);
                valRef.add(key);
            } else{
                ValRef valRef = new ValRef(key,value);
                valMap.put(value,valRef);
                map.putIfAbsent(key,valRef);
            }
        }
    }

    public Object get(Object key){
        ValRef valRef = map.get(key);
        if(valRef != null) {
            return valRef.getValue();
        }
        return null;
    }

    public void remove(Object key,boolean delAll){
        if(!delAll){
            ValRef valRef = map.get(key);
            if(null != valRef) {
                valRef.getKeys().remove(key);
                map.remove(key);
            }
            return;
        }

        ValRef valRef = map.get(key);
        if(null != valRef) {
            Set<Object> keys = valRef.getKeys();
            for(Object k : keys){
                map.remove(k);
            }
            valRef.setValue(null);
            valRef = null;
        }
    }


    class ValRef {
        private Object value;
        private Set<Object> keys;
        ValRef(Object key,Object value){
            this.value = value;
            keys = new HashSet<>();
            keys.add(value);
        }

        public void setValue(Object value){
            this.value = value;
        }

        public Object getValue(){
            return value;
        }

        public Set<Object> getKeys(){
            return this.keys;
        }

        public void add(Object key){
            keys.add(key);
        }
    }

    public static void main(String[] args) {
        MuiltKeyMap obj = new MuiltKeyMap();
         obj.put(2,"a");
         Object val =  obj.get(2);
         System.out.println("a  --  " + val);
         obj.put(4,val);
         obj.put(5,val);
        System.out.println("a  --  " + obj.get(4));
        System.out.println("a  --  " + obj.get(5));
        obj.put(4,"b");
        System.out.println("b  --  " + obj.get(5));
        System.out.println("b  --  " + obj.get(2));
    }
}
