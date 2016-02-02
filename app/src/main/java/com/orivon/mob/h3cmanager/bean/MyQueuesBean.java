package com.orivon.mob.h3cmanager.bean;

/**
 * Created by Eenie on 2016/2/1.
 * Case队列
 */
public class MyQueuesBean {
    private int id = 0;
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



    public MyQueuesBean(Builder builder) {
        this.id = builder.ID;
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
    }


    public int getID() {
        return id;
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
}
