package nc.bs.ahu.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.itf.ahu.IAUJZ;
import nc.vo.pub.BusinessException;

/**
 * 生产入库单 写入动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class N_AUJZ_WRITE {

    public void write(String[] pkArray) throws BusinessException {
        IAUJZ service = NCLocator.getInstance().lookup(IAUJZ.class);
        service.write(pkArray);
    }
}
