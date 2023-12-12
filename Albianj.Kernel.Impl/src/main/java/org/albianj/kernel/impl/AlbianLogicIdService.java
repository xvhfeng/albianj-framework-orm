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
package org.albianj.kernel.impl;

import org.albianj.datetime.AlbianDateTime;
import org.albianj.kernel.IAlbianLogicIdService;
import org.albianj.kernel.KernelSetting;
import org.albianj.net.AlbianHost;
import org.albianj.service.AlbianServiceRant;
import org.albianj.service.FreeAlbianService;
import org.albianj.text.StringHelper;
import org.albianj.verify.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.net.*;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

@AlbianServiceRant(Id = IAlbianLogicIdService.Name, Interface = IAlbianLogicIdService.class)
public class AlbianLogicIdService extends FreeAlbianService implements IAlbianLogicIdService {

    private static final Logger logger = LoggerFactory.getLogger(AlbianLogicIdService.class);
    static AtomicInteger serial = new AtomicInteger(0);
    static AtomicLong id = new AtomicLong(0);
    static AtomicLong jobid = new AtomicLong(0);
    static int seed = 0;

    public String getServiceName() {
        return Name;
    }


    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeStringUNID()
     */
    @Override
    public synchronized String makeStringUNID() {
        return makeStringUNID("Kenerl");
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeStringUNID(java.lang.String)
     */
    @Override
    public synchronized String makeStringUNID(String appName) {
        Random rnd = new Random();
        rnd.setSeed(10000);
        int numb = rnd.nextInt(10000);
        numb = (numb ^ serial.getAndIncrement()) % 10000;
        serial.compareAndSet(10000, 0);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String app = appName;
        if (app.length() < 7) {
            app = StringHelper.padLeft(app, 7);
        }
        if (app.length() > 7) {
            app = app.substring(0, 7);
        }

        return String.format("%1$s-%2$s-%3$s-%4$04d",
                StringHelper.padLeft(KernelSetting.getKernelId(), 4), app,
                dateFormat.format(new Date()), numb);
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#generate32UUID()
     */
    @Override
    @SuppressWarnings("static-access")
    public synchronized String generate32UUID() {
        return UUID.randomUUID().randomUUID().toString().replaceAll("-", "");
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#getAppName(java.lang.String)
     */
    @Override
    public String getAppName(String id) {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {
            return null;
        }
        String[] strs = id.split("-");
        if (4 != strs.length) {
            logger.error("Id:{} is error format ", id);
            return null;
        }
        return StringHelper.censoredZero(strs[1]);

    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#getGenerateDateTime(java.lang.String)
     */
    @Override
    public Date getGenerateDateTime(String id) {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {
            return null;
        }
        String[] strs = id.split("-");
        if (4 != strs.length) {
            logger.error("Id:{} is error formar ", id);
            return null;
        }

        DateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d;
        try {
            d = f.parse(strs[2]);
        } catch (ParseException e) {
            logger.error("Id:{},is error format", id,e);
            return null;
        }
        return d;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#getGenerateTime(java.lang.String)
     */
    @Override
    public Calendar getGenerateTime(String id) {
        if (Validate.isNullOrEmptyOrAllSpace(id)) {
            return null;
        }
        String[] strs = id.split("-");
        if (4 != strs.length) {
            logger.error("Id:{} is error format ", id);
            return null;
        }

        DateFormat f = new SimpleDateFormat("yyyyMMddHHmmss");
        Date d = null;
        try {
            d = f.parse(strs[2]);
        } catch (ParseException e) {
            logger.error("Id:{},is error format ", id,e);
            return null;
        }
        Calendar cal = Calendar.getInstance(); // ?????????????????????????????????date????????????nm?????????
        cal.setTime(d);
        return cal;
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeLoggerId()
     */
    @Override
    public String makeLoggerId() {

        id.compareAndSet(1000000, 0);
        try {
            return String.format("%d-%d-%d", AlbianHost.ipToLong(AlbianHost.getLocalIp()),
                    AlbianDateTime.getDateTimeNow().getTime(),
                    id.getAndIncrement());
        } catch (UnknownHostException e) {
            return String.format("%d-%d", AlbianDateTime.getDateTimeNow()
                    .getTime(), id.getAndIncrement());
        }
    }

    /* (non-Javadoc)
     * @see org.albianj.kernel.impl.AlbianLogicIdServide#makeJobId()
     */
    @Override
    public String makeJobId() {
        id.compareAndSet(1000000, 0);
        try {
            return String.format("%d-%d-%d", AlbianHost.ipToLong(AlbianHost.getLocalIp()),
                    AlbianDateTime.getDateTimeNow().getTime(),
                    jobid.getAndIncrement());
        } catch (UnknownHostException e) {
            return String.format("%d-%d", AlbianDateTime.getDateTimeNow()
                    .getTime(), jobid.getAndIncrement());
        }
    }

    public BigInteger makeSimpleId() {
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        long second = ts.getTime() / 1000;
        BigInteger bi = BigInteger.valueOf(second);
        bi = bi.multiply(new BigInteger("10000"));
        int s = 0;
        synchronized (this) {
            if (seed >= 10000) {
                seed = 0;
            }
            s = seed++;
        }

        bi = bi.add(BigInteger.valueOf(s));
        return bi;
    }

    public String getLocalIP(String networkName) {
        String ip = "";
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals(networkName)) {
                    continue;
                } else {
                    Enumeration<?> e2 = ni.getInetAddresses();
                    while (e2.hasMoreElements()) {
                        InetAddress ia = (InetAddress) e2.nextElement();
                        if (ia instanceof Inet6Address)
                            continue;
                        ip = ia.getHostAddress();
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            logger.error("AlbianLogicIdService getLocalIP is SocketException error ",e);
            System.exit(-1);
        }
        return ip;
    }

    private static volatile long idSeed = 0;
    private static long ipSuff = 0;
    private static long tsSeek = 0;
    private Object locker = new Object();

    public BigInteger genId(String networkName) {
        if (ipSuff == 0) {
            synchronized (this) {
                if (ipSuff == 0) {
                    String ip = getLocalIP(networkName);
                    if (Validate.isNullOrEmptyOrAllSpace(ip) || "0.0.0.0".equals(ip) || "127.0.0.1".equals(ip)) {
                        ip = getLocalFirstConfigIp();
                    }

                    long iip = ipToLong(ip);
                    ipSuff = (iip & 0xFFFF); //取最后的2段
                }
            }
        }

        long ts = System.currentTimeMillis() / 1000 - tsSeek; // seconds
        long idCnt = 0;
        synchronized (locker) {
            idSeed = (++idSeed) % 10000;
            idCnt = idSeed;
        }

        BigInteger id = new BigInteger(String.format("%d000000000", ts))
                .add(BigInteger.valueOf((long) (ipSuff * Math.pow(10, 4))))
                .add(BigInteger.valueOf(idCnt));
        return id;
    }

    private long ipToLong(String ipAddress) {
        long result = 0;
        String[] ipAddressInArray = ipAddress.split("\\.");
        for (int i = 3; i >= 0; i--) {
            long ip = Long.parseLong(ipAddressInArray[3 - i]);
            result |= ip << (i * 8);
        }
        return result;
    }
    /**
     * 得到本地第一个非0.0.0.0和127.0.0.1的ip
     *
     * @return
     */
    private String getLocalFirstConfigIp() {
        String ip = "";
        try {
            Enumeration<?> e1 = NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                Enumeration<?> e2 = ni.getInetAddresses();
                while (e2.hasMoreElements()) {
                    InetAddress ia = (InetAddress) e2.nextElement();
                    if (ia instanceof Inet6Address)
                        continue;
                    ip = ia.getHostAddress();
                    if ("0.0.0.0".equals(ip) || "127.0.0.1".equals(ip)) {
                        continue;
                    }
                    break;
                }
            }
        } catch (SocketException e) {
            logger.error("AlbianLogicIdService getLocalFirstConfigIp is SocketException error ",e);
            System.exit(-1);
        }
        return ip;
    }


}
