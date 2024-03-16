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
package org.albianj.dal.object.filter;

import org.albianj.dal.object.OperatorOpt;
import org.albianj.dal.object.BoolOpt;

import java.util.ArrayList;
import java.util.List;

/**
 * 链式表达式组
 *
 * @author seapeak
 * @since v2.1
 */
public class FilterGroupExpression implements IFilterGroupExpression {

    private BoolOpt _ro = BoolOpt.Normal;
    private int _style = STYLE_FILTER_GROUP;

    private List<IChainExpression> _chains = new ArrayList<>();

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#getRelationalOperator()
     */
    @Override
    public BoolOpt getBoolOpt() {
        // TODO Auto-generated method stub
        return this._ro;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#setRelationalOperator(org.albianj.persistence.object.RelationalOperator)
     */
    @Override
    public void setBoolOpt(BoolOpt boolOpt) {
        // TODO Auto-generated method stub
        this._ro = boolOpt;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#getStyle()
     */
    @Override
    public int getStyle() {
        // TODO Auto-generated method stub
        return this._style;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#setStyle(int)
     */
    @Override
    public void setStyle(int style) {
        // TODO Auto-generated method stub
        this._style = style;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFilterGroupExpression and(IFilterExpression fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BoolOpt.And);
        this._chains.add(fe);
        return this;

    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFilterGroupExpression or(IFilterExpression fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BoolOpt.OR);
        this._chains.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFilterGroupExpression addAddition(IFilterExpression fe) {
        // TODO Auto-generated method stub
        fe.setBoolOpt(BoolOpt.Normal);
        this._chains.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression and(String fieldName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, lo, value);
        this.and(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression and(String fieldName, String aliasName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, aliasName, lo, value);
        this.and(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression or(String fieldName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, lo, value);
        this.or(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression or(String fieldName, String aliasName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, aliasName, lo, value);
        this.or(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression addAddition(String fieldName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, lo, value);
        this.addAddition(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#addAddition(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression addAddition(String fieldName, String aliasName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, aliasName, lo, value);
        this.addAddition(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#and(org.albianj.persistence.object.filter.IFilterGroupExpression)
     */
    @Override
    public IFilterGroupExpression and(IFilterGroupExpression fge) {
        // TODO Auto-generated method stub
        fge.setBoolOpt(BoolOpt.And);
        this._chains.add(fge);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#or(org.albianj.persistence.object.filter.IFilterGroupExpression)
     */
    @Override
    public IFilterGroupExpression or(IFilterGroupExpression fge) {
        // TODO Auto-generated method stub
        fge.setBoolOpt(BoolOpt.OR);
        this._chains.add(fge);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#add(org.albianj.persistence.object.filter.IFilterExpression)
     */
    @Override
    public IFilterGroupExpression add(IFilterExpression fe) {
        fe.setBoolOpt(BoolOpt.Normal);
        // TODO Auto-generated method stub
        this._chains.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IFilterGroupExpression#addFilterGroup(org.albianj.persistence.object.filter.IFilterGroupExpression)
     */
    @Override
    public IFilterGroupExpression addFilterGroup(IFilterGroupExpression fge) {
        fge.setBoolOpt(BoolOpt.Normal);
        // TODO Auto-generated method stub
        this._chains.add(fge);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#add(java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression add(String fieldName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, lo, value);
        this.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#add(java.lang.String, java.lang.String, org.albianj.persistence.object.LogicalOperation, java.lang.Object)
     */
    @Override
    public IFilterGroupExpression add(String fieldName, String aliasName, OperatorOpt lo, Object value) {
        // TODO Auto-generated method stub
        IFilterExpression fe = new FilterExpression(fieldName, aliasName, lo, value);
        this.add(fe);
        return this;
    }

    /* (non-Javadoc)
     * @see org.albianj.persistence.object.filter.IChainExpression#getChainExpression()
     */
    public List<IChainExpression> getChainExpression() {
        return this._chains;
    }

    public IChainExpression addAutoIdExpr() {
        IFilterExpression fe = new FilterExpression();
        fe.setAutoId(true);
        this.add(fe);
        return this;
    }


}
