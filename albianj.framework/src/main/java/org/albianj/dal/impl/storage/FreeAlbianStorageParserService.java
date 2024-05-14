
package org.albianj.dal.impl.storage;

import org.albianj.AblThrowable;
import org.albianj.ServRouter;
import org.albianj.common.utils.SetUtil;
import org.albianj.common.utils.StringsUtil;
import org.albianj.common.utils.XmlUtil;
import org.albianj.dal.api.object.StgAttr;
import org.albianj.kernel.api.logger.LogLevel;
import org.albianj.kernel.api.service.parser.FreeAlbianParserService;
import org.albianj.dal.api.object.DBOpt;
import org.albianj.dal.api.object.RStgAttr;
import org.albianj.dal.api.service.IAlbianStorageParserService;
import org.dom4j.Document;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.List;

public abstract class FreeAlbianStorageParserService extends FreeAlbianParserService
    implements IAlbianStorageParserService {

    private final static String tagName = "Storages/Storage";
    private String file = "storage.xml";
    private HashMap<String, StgAttr> cached = null;

    public static String generateConnectionUrl(
            RStgAttr rsa) {
        if (null == rsa) {
            ServRouter.log(ServRouter.__StartupSessionId,  LogLevel.Warn,
                    "The argument storageAttribute is null.");
            return null;
        }

        StgAttr stgAttr = rsa.getStgAttr();
        StringBuilder sb = new StringBuilder();
        sb.append("jdbc:");
        // String url =
        // "jdbc:mysql://localhost/baseinfo?useUnicode=true&characterEncoding=8859_1";
        switch (stgAttr.getDatabaseStyle()) {
            case (DBOpt.Oracle): {
                sb.append("oracle:thin:@").append(stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append(":").append(rsa.getDatabase());
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("?").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
            }
            case (DBOpt.SqlServer): {
                sb.append("microsoft:sqlserver://").append(
                        stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append(";").append(rsa.getDatabase());
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("?").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
            }
            case (DBOpt.RedShift) : {
                sb.append("redshift://").append(
                        stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append("/").append(rsa.getDatabase());
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("?").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
            }
            case (DBOpt.MySql):
            default: {
                sb.append("mysql://").append(stgAttr.getServer());
                if (0 != stgAttr.getPort()) {
                    sb.append(":").append(stgAttr.getPort());
                }
                sb.append("/").append(rsa.getDatabase());
                sb.append("?useUnicode=true");
                if (null != stgAttr.getCharset()) {
                    sb.append("&characterEncoding=").append(
                            stgAttr.getCharset());
                }
                int timeout = rsa.getStgAttr().getTimeout();
                if (0 < timeout) {
                    sb.append("&connectTimeout=").append(timeout * 1000).append("&socketTimeout=").append(timeout * 1000);
                }
                sb.append("&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull&maxReconnect=3&autoReconnectForPools=true&rewriteBatchedStatements=true&useSSL=true&serverTimezone=CTT");
                if(!StringsUtil.isNullEmptyTrimmed(rsa.getStgAttr().getUrlParaments())){
                    sb.append("&").append(rsa.getStgAttr().getUrlParaments());
                }
                break;
//                sb.append("&autoReconnect=true&failOverReadOnly=false&zeroDateTimeBehavior=convertToNull");
            }
        }
        return sb.toString();
    }

    public void setConfigFileName(String fileName) {
        this.file = fileName;
    }

    @Override
    public void init()  {
        Document doc = null;
        cached = new HashMap<String, StgAttr>();
        try {
            parserFile(file);
        } catch (Throwable e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "loading the storage.xml is error." );
        }
        return;
    }

    private void parserFile(String filename)  {
        Document doc = null;
        cached = new HashMap<String, StgAttr>();
        try {
            String fname = findConfigFile(filename);
            doc = XmlUtil.load(fname);
        } catch (Exception e) {
            ServRouter.logAndThrowAgain(ServRouter.__StartupSessionId,LogLevel.Error,e,
                    "loading the storage.xml is error.");
        }
        if (null == doc) {
            throw new AblThrowable("loading the storage.xml is error.");
        }

        @SuppressWarnings("rawtypes")
        List nodes = XmlUtil.selectNodes(doc, "Storages/IncludeSet/Include");
        if (!SetUtil.isEmpty(nodes)) {
            for (Object node : nodes) {
                Element elt = XmlUtil.toElement(node);
                String path = XmlUtil.getAttributeValue(elt, "Filename");
                if (StringsUtil.isNullEmptyTrimmed(path)) continue;
                parserFile(path);
            }
        }

        @SuppressWarnings("rawtypes")
        List objNodes = XmlUtil.selectNodes(doc, tagName);
        if (SetUtil.isEmpty(objNodes)) {
            throw new AblThrowable("parser the node tags:" + tagName
                + " in the storage.xml is error. the node of the tags is null or empty.");
        }
        parserStorages(objNodes);
        return;
    }

    protected abstract void parserStorages(
            @SuppressWarnings("rawtypes") List nodes);

    protected abstract StgAttr parserStorage(Element node);

    public void addStorageAttribute(String name, StgAttr sa) {
        cached.put(name, sa);
    }

    public StgAttr getStorageAttribute(String name) {
        return cached.get(name);
    }
}
