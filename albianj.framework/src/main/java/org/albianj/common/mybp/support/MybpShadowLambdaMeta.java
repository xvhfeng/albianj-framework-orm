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
package org.albianj.common.mybp.support;

import org.albianj.common.mybp.MybpClassUtils;

/**
 * 基于 {@link MybpLambdaSerializer} 创建的元信息
 * <p>
 * Create by hcl at 2021/7/7
 */
public class MybpShadowLambdaMeta implements MybpLambdaMeta {
    private final MybpLambdaSerializer lambda;

    public MybpShadowLambdaMeta(MybpLambdaSerializer lambda) {
        this.lambda = lambda;
    }

    @Override
    public String getImplMethodName() {
        return lambda.getImplMethodName();
    }

    @Override
    public Class<?> getInstantiatedClass() {
        String instantiatedMethodType = lambda.getInstantiatedMethodType();
        String instantiatedType = instantiatedMethodType.substring(2, instantiatedMethodType.indexOf(";")).replace("/", ".");
        return MybpClassUtils.toClassConfident(instantiatedType, lambda.getCapturingClass().getClassLoader());
    }

}
