/*
 * Copyright (c) 2011-2023, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.albianj.common.mybp;

import org.albianj.AblThrowable;
import org.albianj.common.utils.StringsUtil;

/**
 * 异常辅助工具类
 *
 * @author HCL
 * @since 2018-07-24
 */
public final class MybpExceptionUtils {

    private MybpExceptionUtils() {
    }

    /**
     * 返回一个新的异常，统一构建，方便统一处理
     *
     * @param msg 消息
     * @param t   异常信息
     * @return 返回异常
     */
    public static AblThrowable mpe(String msg, Throwable t, Object... params) {
        return new AblThrowable(StringsUtil.nonIdxFmt(msg, params), t);
    }

    /**
     * 重载的方法
     *
     * @param msg 消息
     * @return 返回异常
     */
    public static AblThrowable mpe(String msg, Object... params) {
        return new AblThrowable(StringsUtil.nonIdxFmt(msg, params));
    }

    /**
     * 重载的方法
     *
     * @param t 异常
     * @return 返回异常
     */
    public static AblThrowable mpe(Throwable t) {
        return new AblThrowable(t);
    }

    public static void throwMpe(boolean condition, String msg, Object... params) {
        if (condition) {
            throw mpe(msg, params);
        }
    }
}
