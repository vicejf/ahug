package com.nc5.generator.service;

import com.nc5.generator.fx.model.BillConfigModel;

/**
 * 单据配置验证服务
 * 负责验证配置数据的完整性和有效性
 */
public class BillConfigValidator {
    
    private final BillConfigModel billConfigModel;
    
    public BillConfigValidator(BillConfigModel billConfigModel) {
        this.billConfigModel = billConfigModel;
    }
    
    /**
     * 验证基本信息必填项是否完整（静默验证，不显示提示）
     */
    public boolean validateBasicInfo() {
        // 单据编码
        if (isNullOrEmpty(billConfigModel.getBillCode())) {
            return false;
        }
        // 单据名称
        if (isNullOrEmpty(billConfigModel.getBillName())) {
            return false;
        }
        // 模块名称（可选）
        // if (isNullOrEmpty(billConfigModel.getModule())) {
        //     return false;
        // }
        // 包路径（可选）
        // if (isNullOrEmpty(billConfigModel.getPackageName())) {
        //     return false;
        // }
        // 仅对多表体类型验证表体编码
        if ("multi".equals(billConfigModel.getBillType())) {
            if (isNullOrEmpty(billConfigModel.getBodyCode())) {
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 验证生成所需的基本信息（带错误详情）
     */
    public ValidationResult validateBasicInfoForGenerate() {
        StringBuilder errors = new StringBuilder();
        
        if (isNullOrEmpty(billConfigModel.getBillCode())) {
            errors.append("- 单据编码不能为空\n");
        }
        if (isNullOrEmpty(billConfigModel.getBillName())) {
            errors.append("- 单据名称不能为空\n");
        }
        
        return new ValidationResult(errors.length() == 0, errors.toString());
    }
    
    /**
     * 判断是否为空字符串
     */
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 验证结果类
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;
        
        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
        
        public boolean hasErrors() {
            return !valid && !errorMessage.isEmpty();
        }
    }
}
