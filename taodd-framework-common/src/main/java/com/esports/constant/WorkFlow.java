package com.esports.constant;

/**
 * 模拟工作流
 * @author jacky
 *
 */
public class WorkFlow {
    
    /**
     * 提款审核流程
     * @author jacky
     *
     */
    public enum Draw{
        
        /**
         * 提款待审核
         */
        _PENDIGN_AUDIT(0),
        
        /**
         * 出款待审核
         */
        _AUDIT_PAYMENT_OUT(1),
        
        /**
         * 提款审核失败
         */
        _AUDIT_FAILED(2),
        
        /**
         * 出款驳回
         */
        _AUDIT_REVOKE(3),
        
        /**
         * 出款成功
         */
        _AUDIT_SUCCESS(4)
        ;
        
        private Integer code;

        Draw(Integer code) {
            this.code = code;
        }
        /**
         * @return the code
         */
        public Integer getCode() {
            return code;
        }

        /**
         * @param code the code to set
         */
        public void setCode(Integer code) {
            this.code = code;
        }
    }

    public enum Agent{
    
        _PENDIGN_AUDIT(0),
        
        _AUDIT_FAILED(2),
        
        _AUDIT_SUCCESS(1);
        
        private Integer code;

        Agent(Integer code) {
            this.code = code;
        }
        /**
         * @return the code
         */
        public Integer getCode() {
            return code;
        }
        /**
         * @param code the code to set
         */
        public void setCode(Integer code) {
            this.code = code;
        }
    }
    
}
