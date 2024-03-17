package Albian.Test.Fns;

import org.albianj.common.mybp.LambdaUtils;
import org.albianj.common.mybp.support.LambdaMeta;
import org.albianj.common.mybp.support.SFunction;
import org.albianj.common.utils.ReflectUtil;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class GetterFnTest {

    public <T,R> String getGetterFn(SFunction<T,R> fn){
      LambdaMeta meta = LambdaUtils.extract(fn);
      String methodFullName = meta.getInstantiatedClass().getName() + "." +  meta.getImplMethodName();
      System.out.println("fn fullname -> " + methodFullName);
      String name =    fn.getClass().getName();
      return name;
    }

    public void refPropGetter(){
        try {
            Class<?> clzz = ObjTest.class;
            Field idFd = clzz.getDeclaredField("id");
            PropertyDescriptor pd =  ReflectUtil.getBeanPropertyDescriptor(clzz,idFd.getName());
            Method method = pd.getReadMethod();
            String getterFullname = clzz.getName() + "." + method.getName();

            System.out.println("fn fullname -> " + getterFullname);

        }catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void testParserGetterFn(){
        getGetterFn(ObjTest::getId);
    }

    public static void main(String[] args){
        GetterFnTest test = new GetterFnTest();
        test.testParserGetterFn();
        test.refPropGetter();
    }
}
