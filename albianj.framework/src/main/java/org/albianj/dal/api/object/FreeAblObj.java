/*
Copyright (c) 2016, Shanghai YUEWEN Information Technology Co., Ltd. 
All rights reserved.
Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, 
* this list of conditions and the following disclaimer.
* Redistributions in binary form must reproduce the above copyright notice, 
* this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
* Neither the name of Shanghai YUEWEN Information Technology Co., Ltd. 
* nor the names of its contributors may be used to endorse or promote products derived from 
* this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY SHANGHAI YUEWEN INFORMATION TECHNOLOGY CO., LTD. 
AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, 
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. 
IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; 
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

Copyright (c) 2016 著作权由上海阅文信息技术有限公司所有。著作权人保留一切权利。

这份授权条款，在使用者符合以下三条件的情形下，授予使用者使用及再散播本软件包装原始码及二进位可执行形式的权利，无论此包装是否经改作皆然：

* 对于本软件源代码的再散播，必须保留上述的版权宣告、此三条件表列，以及下述的免责声明。
* 对于本套件二进位可执行形式的再散播，必须连带以文件以及／或者其他附于散播包装中的媒介方式，重制上述之版权宣告、此三条件表列，以及下述的免责声明。
* 未获事前取得书面许可，不得使用柏克莱加州大学或本软件贡献者之名称，来为本软件之衍生物做任何表示支持、认可或推广、促销之行为。

免责声明：本软件是由上海阅文信息技术有限公司及本软件之贡献者以现状提供，本软件包装不负任何明示或默示之担保责任，
包括但不限于就适售性以及特定目的的适用性为默示性担保。加州大学董事会及本软件之贡献者，无论任何条件、无论成因或任何责任主义、
无论此责任为因合约关系、无过失责任主义或因非违约之侵权（包括过失或其他原因等）而起，对于任何因使用本软件包装所产生的任何直接性、间接性、
偶发性、特殊性、惩罚性或任何结果的损害（包括但不限于替代商品或劳务之购用、使用损失、资料损失、利益损失、业务中断等等），
不负任何责任，即在该种使用已获事前告知可能会造成此类损害的情形下亦然。
*/
package org.albianj.dal.api.object;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.dal.api.object.rants.AblEntityFieldRant;
import org.albianj.dal.api.service.AlbianEntityMetadata;
import org.albianj.dal.api.context.dactx.AblObjWarp;
import org.albianj.common.comment.SpecialWarning;
import org.albianj.common.utils.SetUtil;
import org.albianj.kernel.api.logger.LogLevel;

import java.util.HashMap;
import java.util.Map;

public abstract class FreeAblObj implements IAblObj {

    @AblEntityFieldRant(IsSave = false, Ignore = true)
    private static final long serialVersionUID = 1608573290358087720L;

    @AblEntityFieldRant(IsSave = false, Ignore = true)
    protected transient HashMap<String, Object> dic = null;

    @AblEntityFieldRant(IsSave = false, Ignore = true)
    protected transient Map<String, AblObjWarp> chainEntity = null;

    @AblEntityFieldRant(IsSave = false, Ignore = true)
    private transient boolean isAlbianNew = true;

    protected FreeAblObj() {
        chainEntity = new HashMap<>();
    }


//    @AlbianObjectMemberRant(IsSave = false, Ignore = true)
    public boolean getIsAlbianNew() {
        return this.isAlbianNew;
    }

//    @AlbianObjectMemberRant(IsSave = false, Ignore = true)
    public void setIsAlbianNew(boolean isAlbianNew) {
        this.isAlbianNew = isAlbianNew;
    }

//    @AlbianObjectMemberRant(IsSave = false, Ignore = true)
    public void setOldAlbianObject(String key, Object v) {
        if (null == dic) {
            dic = new HashMap<String, Object>();
        }
        dic.put(key, v);
    }

//    @AlbianObjectMemberRant(IsSave = false, Ignore = true)
    public Object getOldAlbianObject(String key) {
        if (null == dic) {
            return null;
        }
        return dic.get(key);
    }

//    @Deprecated
//    @org.albianj.common.comment.SpecialWarning("不推荐使用，推荐使用带sessionid参数的同名函数")
//    public boolean needUpdate(Object sessionId) throws AlbianDataServiceException {
//        return needUpdate(sessionId);
//    }

    @SpecialWarning("不推荐使用，推荐使用带sessionid,itf的同名函数")
    public boolean needUpdate(Object sessionId)  {
        String className = this.getClass().getName();
//        String itf = AlbianEntityMetadata.type2Interface(className);
        return needUpdate(sessionId, className);
    }

    public boolean needUpdate(Object sessionId, Class<? extends IAblObj> implClzz)  {
        return needUpdate(sessionId, implClzz.getName());
    }

    private boolean needUpdate(Object sessionId, String implClzzName)  {
        String className = this.getClass().getName();
        AblEntityAttr entiryAttr = AlbianEntityMetadata.getEntityMetadata(implClzzName);
        if (null == entiryAttr) {
            throw new AblThrowable(
                "PersistenceService is error. albian-object:" + className + " attribute is not found.");
        }

        Map<String, AblEntityFieldAttr> fields = entiryAttr.getFields();
        if (SetUtil.isNullOrEmpty(fields)) {
            throw new AblThrowable(
                "PersistenceService is error. albian-object:" + className + " PropertyDescriptor is not found.");
        }
        try {

            for (AblEntityFieldAttr fieldAttr : fields.values()) {
                if (!fieldAttr.isSave())
                    continue;
                Object newVal = fieldAttr.getEntityField().get(this);
                Object oldValue = getOldAlbianObject(fieldAttr.getPropertyName());

                if ((null == newVal && null == oldValue)) {
                    continue;
                }
                if (null != newVal && newVal.equals(oldValue)) {
                    continue;
                }
                if (null != oldValue && oldValue.equals(newVal)) {
                    continue;
                }
                return true;
            }
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(sessionId,  LogLevel.Error,e,
                    "PersistenceService is error. invoke bean read method is error.the property is:{} ",
                    entiryAttr.getType());
        }
        return false;
    }
}

