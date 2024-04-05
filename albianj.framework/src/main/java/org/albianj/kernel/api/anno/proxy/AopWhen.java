package org.albianj.kernel.api.anno.proxy;


import org.albianj.common.utils.LangUtil;

/**
 * Aop point的设定作用起效点
 */
public final class AopWhen {

    /**
     * 在函数被执行之前起效
     */
    public final static int Brf = 0x01;
    /**
     * 在函数被执行之后起效
     */
    public final static int Aft = 0x02;
    /**
     * 在函数被执行前和执行后都会起效
     */
    public final static int Ard = 0x04;
    /**
     * 当函数被执行的过程中触发异常时起效
     */
    public final static int Thr = 0x08;

    public static boolean isBefore(int mod) {
        return (mod & Brf) != 0;
    }
    public static boolean isAfter(int mod) {
        return (mod & Brf) != 0;
    }
    public static boolean isAround(int mod) {
        return (mod & Brf) != 0;
    }
    public static boolean isThrows(int mod) {
        return (mod & Brf) != 0;
    }

//    public static int set(int... whens){
//        int ret = 0;
//        if(LangUtil.isNotNull(whens)) {
//            for (int when : whens) {
//                ret |= when;
//            }
//        }
//        return ret;
//    }


}
