package nc.itf.ahu;

import nc.vo.ui.ahu.aujz.AggAUJZVO;
import nc.vo.pub.AggregatedValueObject;

/**
 * 生产入库单 服务接口
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public interface IAUJZ {

    AggregatedValueObject insert(AggregatedValueObject billVO) throws Exception;

    AggregatedValueObject update(AggregatedValueObject billVO) throws Exception;

    void delete(String[] pkArray) throws Exception;

    AggregatedValueObject[] save(AggregatedValueObject[] billVOs) throws Exception;

    AggAUJZVO[] query(String[] pkArray) throws Exception;
}
