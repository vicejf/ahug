package nc.ui.ui.ahu.aujz.controller;

import nc.ui.uif2.NCAction;
import nc.ui.uif2.actions.ActionContainer;

/**
 * 生产入库单 私有按钮接口
 * 创建日期:2026-02-10
 * @author Flynn Chen
 */
public interface IPrivateBtn {
    
    String BTN_ADD = "add";
    String BTN_EDIT = "edit";
    String BTN_DELETE = "delete";
    String BTN_SAVE = "save";
    String BTN_CANCEL = "cancel";
    
    void addActions(ActionContainer container);
}
