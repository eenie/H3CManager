package com.orivon.mob.h3cmanager.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by Eenie on 2016/2/1.
 * Case队列
 */

@Table(name = "myqueuesbean" ,onCreated = "CREATE UNIQUE INDEX index_name ON myqueuesbean(caseID)")
public class MyQueuesBean {

    @Column(name = "id", isId = true, autoGen = true)
    private int id = 0;

    @Column(name = "caseID")
    private int caseID;
    @Column(name = "Company")
    private String Company = "";
    @Column(name = "Title")
    private String Title = "";
    @Column(name = "ProductDes")
    private String ProductDes = "";
    @Column(name = "SLA")
    private String SLA = "";
    @Column(name = "Queue")
    private String Queue = "";
    @Column(name = "Severity")
    private String Severity = "";
    @Column(name = "Province")
    private String Province = "";
    @Column(name = "Age")
    private String Age = "";
    @Column(name = "url")
    private String url = "";
    @Column(name = "RecordCount")
    private String RecordCount = "";
    @Column(name = "PagerCount")
    private String PagerCount = "";
    @Column(name = "isNew")
    private boolean isNew = true;


    public MyQueuesBean() {

    }

    public MyQueuesBean(Builder builder) {
        this.caseID = builder.ID;
        this.Company = builder.Company;
        this.Title = builder.Title;
        this.ProductDes = builder.ProductDes;
        this.SLA = builder.SLA;
        this.Queue = builder.Queue;
        this.Severity = builder.Severity;
        this.Province = builder.Province;
        this.Age = builder.Age;
        this.url = builder.url;
        this.RecordCount = builder.RecordCount;
        this.PagerCount = builder.PagerCount;
        this.isNew = builder.isNew;
    }


    public static class Builder {

        private int ID = 0;
        private String Company = "";
        private String Title = "";
        private String ProductDes = "";
        private String SLA = "";
        private String Queue = "";
        private String Severity = "";
        private String Province = "";
        private String Age = "";
        private String url = "";
        private String RecordCount = "";
        private String PagerCount = "";
        private boolean isNew = true;



        public MyQueuesBean create() {
            MyQueuesBean bean = new MyQueuesBean(this);
            return bean;
        }

        public Builder setID(int ID) {
            this.ID = ID;
            return this;
        }

        public Builder setCompany(String company) {
            Company = company;
            return this;
        }

        public Builder setTitle(String title) {
            Title = title;
            return this;
        }

        public Builder setProductDes(String productDes) {
            ProductDes = productDes;
            return this;
        }

        public Builder setSLA(String SLA) {
            this.SLA = SLA;
            return this;
        }

        public Builder setQueue(String queue) {
            Queue = queue;
            return this;
        }

        public Builder setSeverity(String severity) {
            Severity = severity;
            return this;
        }

        public Builder setProvince(String province) {
            Province = province;
            return this;
        }

        public Builder setAge(String age) {
            Age = age;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }
        public void setRecordCount(String recordCount) {
            RecordCount = recordCount;
        }

        public void setPagerCount(String pagerCount) {
            PagerCount = pagerCount;
        }

        public void setIsNew(boolean isNew) {
            this.isNew = isNew;
        }
    }


    public int getID() {
        return caseID;
    }

    public String getCompany() {
        return Company;
    }

    public String getTitle() {
        return Title;
    }

    public String getProductDes() {
        return ProductDes;
    }

    public String getSLA() {
        return SLA;
    }

    public String getQueue() {
        return Queue;
    }

    public String getSeverity() {
        return Severity;
    }

    public String getProvince() {
        return Province;
    }

    public String getAge() {
        return Age;
    }

    public String getUrl() {
        return url;
    }

    public String getRecordCount() {
        return RecordCount;
    }

    public String getPagerCount() {
        return PagerCount;
    }

    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }
}
