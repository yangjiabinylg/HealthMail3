package com.zjyouth.pojo;

import java.util.Date;

public class Logistics {
    private Integer id;

    private String logisticsCompany;

    private String logisticsCode;

    private String logisticsNo;

    private String logisticsDetail;

    private Date createTime;

    private Date updateTime;

    public Logistics(Integer id, String logisticsCompany, String logisticsCode, String logisticsNo, String logisticsDetail, Date createTime, Date updateTime) {
        this.id = id;
        this.logisticsCompany = logisticsCompany;
        this.logisticsCode = logisticsCode;
        this.logisticsNo = logisticsNo;
        this.logisticsDetail = logisticsDetail;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public Logistics() {
        super();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLogisticsCompany() {
        return logisticsCompany;
    }

    public void setLogisticsCompany(String logisticsCompany) {
        this.logisticsCompany = logisticsCompany == null ? null : logisticsCompany.trim();
    }

    public String getLogisticsCode() {
        return logisticsCode;
    }

    public void setLogisticsCode(String logisticsCode) {
        this.logisticsCode = logisticsCode == null ? null : logisticsCode.trim();
    }

    public String getLogisticsNo() {
        return logisticsNo;
    }

    public void setLogisticsNo(String logisticsNo) {
        this.logisticsNo = logisticsNo == null ? null : logisticsNo.trim();
    }

    public String getLogisticsDetail() {
        return logisticsDetail;
    }

    public void setLogisticsDetail(String logisticsDetail) {
        this.logisticsDetail = logisticsDetail == null ? null : logisticsDetail.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}