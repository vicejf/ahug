package nc.bs.ahu.aujz;

import nc.bs.framework.ejb.BaseEJB;
import nc.bs.framework.ejb.EjbFactory;
import nc.itf.ahu.IAUJZ;
import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * 生产入库单 服务实现
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class AUJZImpl extends BaseEJB implements IAUJZ {

    private static final long serialVersionUID = 1L;

    @Override
    public AggregatedValueObject insert(AggregatedValueObject billVO) throws Exception {
        return null;
    }

    @Override
    public AggregatedValueObject update(AggregatedValueObject billVO) throws Exception {
        return null;
    }

    @Override
    public void delete(String[] pkArray) throws Exception {
    }

    @Override
    public AggregatedValueObject[] save(AggregatedValueObject[] billVOs) throws Exception {
        return null;
    }

    @Override
    public AggAUJZVO[] query(String[] pkArray) throws Exception {
        return null;
    }
}
