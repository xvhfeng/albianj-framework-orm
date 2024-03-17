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
package org.albianj.api.dal.object.filter;

import org.albianj.api.dal.object.OOpt;
import org.albianj.api.dal.object.BOpt;
import org.albianj.common.mybp.support.SFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式表达式过滤项
 *
 * @author seapeak
 * @since v2.1
 */
public class FltExpr implements IFltExpr {

    private BOpt _ro = BOpt.Normal;
    private ExprOpt _exprOpt = ExprOpt.Filter;

    private String _fieldName = null;
    private String _aliasName = null;
    private OOpt _lo = OOpt.eq;
    private Object _value = null;
    private Class<?> _cls = null;
    private boolean _isAddition = false;
    private boolean _isIdentical = false;
    private SFunction _getter = null;

    private List<IChaExpr> _chains = new ArrayList<>();

    public FltExpr() {
        // TODO Auto-generated constructor stub
    }

    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public FltExpr(String fieldName, String aliasName, OOpt lo, Object value) {
        this._fieldName = fieldName;
        this._aliasName = aliasName;
        this._lo = lo;
        this._value = value;
        this._chains.add(this);
    }

    public <T,R> FltExpr(SFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        this._getter = getter;
        this._aliasName = aliasName;
        this._lo = lo;
        this._value = value;
        this._chains.add(this);
    }
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public FltExpr(String fieldName, OOpt lo, Object value) {
        this._fieldName = fieldName;
        this._lo = lo;
        this._value = value;
        this._chains.add(this);
    }

    public <T,R> FltExpr(SFunction<T,R> getter, OOpt lo, Object value) {
        this._getter = getter;
        this._lo = lo;
        this._value = value;
        this._chains.add(this);
    }


    @Override
    public BOpt getBoolOpt() {
        // TODO Auto-generated method stub
        return this._ro;
    }

    @Override
    public void setBoolOpt(BOpt bOpt) {
        // TODO Auto-generated method stub
        this._ro = bOpt;
    }

    @Override
    public ExprOpt getExprOpt() {
        // TODO Auto-generated method stub
        return this._exprOpt;
    }

    @Override
    public void setExprOpt(ExprOpt exprOpt) {
        // TODO Auto-generated method stub
        this._exprOpt = exprOpt;
    }

    @Override
    public IFltExpr and(IFltExpr fe) {
        fe.setBoolOpt(BOpt.And);
        _chains.add(fe);
        return this;
        // TODO Auto-generated method stub
    }

    @Override
    public IFltExpr or(IFltExpr fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BOpt.OR);
        _chains.add(fe);
        return this;
    }

    public IFltExpr addAddition(IFltExpr fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BOpt.Normal);
        fe.setAddition(true);
        _chains.add(fe);
        return this;
    }


    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    @Override
    public IFltExpr and(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(fieldName, lo, value);
        this.and(ce);
        return this;
    }

    public <T,R> IFltExpr and(SFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(getter, lo, value);
        this.and(ce);
        return this;
    }

    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    @Override
    public IFltExpr and(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(fieldName, aliasName, lo, value);
        this.and(ce);
        return this;
    }

    public <T,R> IFltExpr and(SFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(getter, aliasName, lo, value);
        this.and(ce);
        return this;
    }

    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    @Override
    public IFltExpr or(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(fieldName, lo, value);
        this.or(ce);
        return this;
    }
    public <T,R> IFltExpr or(SFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(getter, lo, value);
        this.or(ce);
        return this;
    }

    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltExpr or(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(fieldName, aliasName, lo, value);
        this.or(ce);
        return this;
    }

    public <T,R> IFltExpr or(SFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(getter, aliasName, lo, value);
        this.or(ce);
        return this;
    }

    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltExpr addAddition(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(fieldName, lo, value);
        this.addAddition(ce);
        return this;
    }

    public <T,R> IFltExpr addAddition(SFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(getter, lo, value);
        this.addAddition(ce);
        return this;
    }

    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltExpr addAddition(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(fieldName, aliasName, lo, value);
        this.addAddition(ce);
        return this;
    }

    public <T,R> IFltExpr addAddition(SFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr ce = new FltExpr(getter, aliasName, lo, value);
        this.addAddition(ce);
        return this;
    }

    @Override
    public IFltExpr and(IFltGExpr fge) {
        // TODO Auto-generated method stub
        fge.setExprOpt(ExprOpt.FilterGroup);
        fge.setBoolOpt(BOpt.And);
        _chains.add(fge);
        return this;
    }

    @Override
    public IChaExpr addAutoIdExpr() {
        IFltExpr fe = new FltExpr();
        fe.setAutoId(true);
        this.add(fe);
        return this;
    }

    @Override
    public IFltExpr or(IFltGExpr fge) {
        // TODO Auto-generated method stub
        fge.setExprOpt(ExprOpt.FilterGroup);
        fge.setBoolOpt(BOpt.OR);
        _chains.add(fge);
        return this;
    }

    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltExpr add(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, lo, value);
        this.add(fe);
        return this;
    }
    public <T,R> IFltExpr add(SFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, lo, value);
        this.add(fe);
        return this;
    }

    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltExpr add(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, aliasName, lo, value);
        this.add(fe);
        return this;
    }

    @Override
    public <T,R> IFltExpr add(SFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, aliasName, lo, value);
        this.add(fe);
        return this;
    }


    public IFltExpr add(IFltExpr fe) {
        fe.setBoolOpt(BOpt.Normal);
        // TODO Auto-generated method stub
        this._chains.add(fe);
        return this;
    }


    @Override
    public String getFieldName() {
        // TODO Auto-generated method stub
        return this._fieldName;
    }

    @Override
    public void setFieldName(String fieldName) {
        // TODO Auto-generated method stub
        this._fieldName = fieldName;
    }

    public SFunction getGetter(){
        return this._getter;
    }

    @Override
    public Class<?> getFieldClass() {
        // TODO Auto-generated method stub
        return this._cls;
    }

    @Override
    public void setFieldClass(Class<?> cls) {
        // TODO Auto-generated method stub
        this._cls = cls;
    }

    @Override
    public OOpt getOperatorOpt() {
        // TODO Auto-generated method stub
        return this._lo;
    }

    @Override
    public void setOperatorOpt(OOpt oOpt) {
        // TODO Auto-generated method stub
        this._lo = oOpt;
    }

    @Override
    public Object getValue() {
        // TODO Auto-generated method stub
        return this._value;
    }

    @Override
    public void setValue(Object value) {
        // TODO Auto-generated method stub
        this._value = value;
    }

    @Override
    public boolean isAddition() {
        // TODO Auto-generated method stub
        return this._isAddition;
    }

    @Override
    public void setAddition(boolean isAddition) {
        // TODO Auto-generated method stub
        this._isAddition = isAddition;
    }

    @Override
    public String getAliasName() {
        // TODO Auto-generated method stub
        return this._aliasName;
    }

    @Override
    public void setAliasName(String an) {
        // TODO Auto-generated method stub
        this._aliasName = an;
    }

    public List<IChaExpr> getChainExpression() {
        return this._chains;
    }

    public boolean isAutoId() {
        return this._isIdentical;
    }

    public void setAutoId(boolean identical) {
        this._isIdentical = identical;
    }

}
