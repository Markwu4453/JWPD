package cn.jwjg.jwpd.entity;

import java.io.Serializable;

public class Code implements Serializable {
    private Long Id;
    private String codeNo;
    private Long number;
    private String user;
    private String productState;

    public Code(Long id, String codeNo, Long number, String user, String productState) {
        Id = id;
        this.codeNo = codeNo;
        this.number = number;
        this.user = user;
        this.productState = productState;
    }

    public Code() {
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public String getCodeNo() {
        return codeNo;
    }

    public void setCodeNo(String codeNo) {
        this.codeNo = codeNo;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getProductState() {
        return productState;
    }

    public void setProductState(String productState) {
        this.productState = productState;
    }

    @Override
    public String toString() {
        return "Code{" +
                "Id=" + Id +
                ", codeNo='" + codeNo + '\'' +
                ", number=" + number +
                ", user='" + user + '\'' +
                ", productState='" + productState + '\'' +
                '}';
    }
}
