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
package org.albianj.kernel.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.*;
import java.util.Enumeration;

public class HostUtil {
    private static final Logger logger = LoggerFactory.getLogger(HostUtil.class);

    @Deprecated
    public static InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getLocalHost();
    }

    @Deprecated
    public static String getHostIp(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); // get the ip address
        return ip;
    }

    public static String getHostName(InetAddress netAddress) {
        if (null == netAddress) {
            return null;
        }
        String name = netAddress.getHostName(); // get the host address
        return name;
    }

    @Deprecated
    public static String getLocalIp() throws UnknownHostException {
        InetAddress netAddress = getInetAddress();
        if (null == netAddress) {
            return null;
        }
        String ip = netAddress.getHostAddress(); // get the ip address
        return ip;
    }

    @Deprecated
    public static String getLocalName() throws UnknownHostException {
        InetAddress netAddress = getInetAddress();
        if (null == netAddress) {
            return null;
        }
        String name = netAddress.getHostName(); // get the host address
        return name;
    }

    public static long ipToLong(String sIp) {
        long[] ip = new long[4];
        int position1 = sIp.indexOf(".");
        int position2 = sIp.indexOf(".", position1 + 1);
        int position3 = sIp.indexOf(".", position2 + 1);
        ip[0] = Long.parseLong(sIp.substring(0, position1));
        ip[1] = Long.parseLong(sIp.substring(position1 + 1, position2));
        ip[2] = Long.parseLong(sIp.substring(position2 + 1, position3));
        ip[3] = Long.parseLong(sIp.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }

    public static String longToIp(int n) {
        StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf((n >>> 24)));
        sb.append(".");
        sb.append(String.valueOf((n & 0x00FFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((n & 0x0000FFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf((n & 0x000000FF)));
        return sb.toString();
    }

    /* 一个将字节转化为十六进制ASSIC码的函数 */
    public static String byteHEX(byte ib) {
        char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        char[] ob = new char[2];
        ob[0] = Digit[(ib >>> 4) & 0X0F];
        ob[1] = Digit[ib & 0X0F];
        String s = new String(ob);
        return s;
    }

    public static String getMacAddr() {
        return getMacAddrByName("eth1");
    }

    public static String getLocalIP() {
        return getLocalIPByName("eth1");
    }

    public static String getMacAddrByName(String ntkName) {
        String MacAddr = "";
        String str = "";
        try {
            NetworkInterface NIC = NetworkInterface.getByName(ntkName);
            byte[] buf = NIC.getHardwareAddress();
            for (int i = 0; i < buf.length; i++) {
                str = str + byteHEX(buf[i]);
            }
            MacAddr = str.toUpperCase();
        } catch (SocketException e) {
            logger.error("AlbianHost getMacAddrByName is SocketException error ",e);
            System.exit(-1);
        }
        return MacAddr;
    }

    public static String getLocalIPByName(String ntkName) {
        String ip = "";
        try {
            Enumeration<?> e1 = (Enumeration<?>) NetworkInterface.getNetworkInterfaces();
            while (e1.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e1.nextElement();
                if (!ni.getName().equals(ntkName)) {
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
            logger.error("AlbianHost getLocalIPByName is SocketException error ",e);
        }
        return ip;
    }


}
