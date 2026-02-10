package nc.bs.ahu.aujz;

import nc.bs.framework.common.NCLocator;
import nc.itf.ahu.IAUJZ;
import nc.vo.pub.BusinessException;

/**
 * 生产入库单 删除动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class DeleteAction {

    public void delete(String[] pkArray) throws BusinessException {
        IAUJZ service = NCLocator.getInstance().lookup(IAUJZ.class);
        service.delete(pkArray);
    }
}
