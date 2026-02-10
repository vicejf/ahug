package nc.bs.ahu.pub.action;

import nc.bs.framework.common.NCLocator;
import nc.itf.ahu.IAUJZ;
import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * 生产入库单 保存动作
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class N_AUJZ_SAVE {

    public AggregatedValueObject[] save(AggregatedValueObject[] billVOs) throws Exception {
        IAUJZ service = NCLocator.getInstance().lookup(IAUJZ.class);
        return service.save(billVOs);
    }
}
