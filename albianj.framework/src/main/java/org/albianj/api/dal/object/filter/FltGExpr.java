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
import org.albianj.common.mybp.support.MybpSFunction;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式表达式组
 *
 * @author seapeak
 * @since v2.1
 */
public class FltGExpr implements IFltGExpr {

    private BOpt _ro = BOpt.Normal;
    private ExprOpt _exprOpt = ExprOpt.FilterGroup;

    private List<IChaExpr> _chains = new ArrayList<>();

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#getRelationalOperator()
     */
    @Override
    public BOpt getBoolOpt() {
        // TODO Auto-generated method stub
        return this._ro;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#setRelationalOperator(org.albianj.persistence.object.RelationalOperator)
     */
    @Override
    public void setBoolOpt(BOpt bOpt) {
        // TODO Auto-generated method stub
        this._ro = bOpt;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#getStyle()
     */
    @Override
    public ExprOpt getExprOpt() {
        // TODO Auto-generated method stub
        return this._exprOpt;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#setStyle(int)
     */
    @Override
    public void setExprOpt(ExprOpt _exprOpt) {
        // TODO Auto-generated method stub
        this._exprOpt = _exprOpt;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFltGExpr and(IFltExpr fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BOpt.And);
        this._chains.add(fe);
        return this;

    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFltGExpr or(IFltExpr fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BOpt.OR);
        this._chains.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFltGExpr addAddition(IFltExpr fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BOpt.Normal);
        this._chains.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr and(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, lo, value);
        this.and(fe);
        return this;
    }

    public <T,R> IFltGExpr and(MybpSFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, lo, value);
        this.and(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr and(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, aliasName, lo, value);
        this.and(fe);
        return this;
    }

    public <T,R> IFltGExpr and(MybpSFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, aliasName, lo, value);
        this.and(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr or(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, lo, value);
        this.or(fe);
        return this;
    }
    public <T,R> IFltGExpr or(MybpSFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, lo, value);
        this.or(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr or(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, aliasName, lo, value);
        this.or(fe);
        return this;
    }

    public <T,R> IFltGExpr or(MybpSFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, aliasName, lo, value);
        this.or(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr addAddition(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, lo, value);
        this.addAddition(fe);
        return this;
    }

    public <T,R> IFltGExpr addAddition(MybpSFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, lo, value);
        this.addAddition(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr addAddition(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, aliasName, lo, value);
        this.addAddition(fe);
        return this;
    }

    public <T,R> IFltGExpr addAddition(MybpSFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, aliasName, lo, value);
        this.addAddition(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(org.albianj.persistence.object.filter.IFilterGroupExpression)
     */
    @Override
    public IFltGExpr and(IFltGExpr fge) {
        // TODO Auto-generated method stub
        fge.setBoolOpt(BOpt.And);
        this._chains.add(fge);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(org.albianj.persistence.object.filter.IFilterGroupExpression)
     */
    @Override
    public IFltGExpr or(IFltGExpr fge) {
        // TODO Auto-generated method stub
        fge.setBoolOpt(BOpt.OR);
        this._chains.add(fge);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#add(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFltGExpr add(IFltExpr fe) {
        fe.setBoolOpt(BOpt.Normal);
        // TODO Auto-generated method stub
        this._chains.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IFilterGroupExpression#addFilterGroup(org.albianj.persistence.object.filter.IFilterGroupExpression)
     */
    @Override
    public IFltGExpr addFilterGroup(IFltGExpr fge) {
        fge.setBoolOpt(BOpt.Normal);
        // TODO Auto-generated method stub
        this._chains.add(fge);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#add(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr add(String fieldName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, lo, value);
        this.add(fe);
        return this;
    }

    public <T,R> IFltGExpr add(MybpSFunction<T,R> getter, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, lo, value);
        this.add(fe);
        return this;
    }


    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#add(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    @Deprecated(since = "自从你看见开始，不再推荐使用，优先使用getter版本")
    public IFltGExpr add(String fieldName, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(fieldName, aliasName, lo, value);
        this.add(fe);
        return this;
    }

    public <T,R> IFltGExpr add(MybpSFunction<T,R> getter, String aliasName, OOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFltExpr fe = new FltExpr(getter, aliasName, lo, value);
        this.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#getChainExpression()
     */
    public List<IChaExpr> getChainExpression() {
        return this._chains;
    }

    public IChaExpr addAutoIdExpr() {
        IFltExpr fe = new FltExpr();
        fe.setAutoId(true);
        this.add(fe);
        return this;
    }


}
