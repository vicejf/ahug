package nc.ui.ui.ahu.aujz.refmodel;

import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.bd.ref.RefColumnInfo;

/**
 * 生产入库单 参照模型
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public class RefModel extends AbstractRefModel {

    private static final long serialVersionUID = 1L;

    @Override
    public String[] getFieldCodes() {
        return new String[]{"code", "name"};
    }

    @Override
    public String[] getFieldNames() {
        return new String[]{"编码", "名称"};
    }

    @Override
    public RefColumnInfo[] getRefColumnInfo() {
        return new RefColumnInfo[]{
            new RefColumnInfo("code", "编码", 100),
            new RefColumnInfo("name", "名称", 200)
        };
    }

    @Override
    public String getPkFieldCode() {
        return "pk";
    }

    @Override
    public String getRefTitle() {
        return "生产入库单";
    }

    @Override
    public String getTableName() {
        return "ahu_aujz_h";
    }
}
