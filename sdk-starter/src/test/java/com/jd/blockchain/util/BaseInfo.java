package com.jd.blockchain.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.binary.Base64;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 构建数据账户存储的常规对象；
 * 构建10个字符串对象，key=20长度，value=100长度；
 * 构建1个List对象，其中存储10个字符串对象；
 */
public class BaseInfo {
    private String attr0_xxx_1234567890;
    private String attr1_xxx_1234567890;
    private String attr2_xxx_1234567890;
    private String attr3_xxx_1234567890;
    private String attr4_xxx_1234567890;
    private String attr5_xxx_1234567890;
    private String attr6_xxx_1234567890;
    private String attr7_xxx_1234567890;
    private String attr8_xxx_1234567890;
    private String attr9_xxx_1234567890;

    //list对象;
    private List<String> attrsList_1234567890;

    public BaseInfo(){
        byte[] arr = new byte[100];
        new Random().nextBytes(arr);
        String value = Base64.encodeBase64String(arr);
        this.setAttr0_xxx_1234567890(value);
        this.setAttr1_xxx_1234567890(value);
        this.setAttr2_xxx_1234567890(value);
        this.setAttr3_xxx_1234567890(value);
        this.setAttr4_xxx_1234567890(value);
        this.setAttr5_xxx_1234567890(value);
        this.setAttr6_xxx_1234567890(value);
        this.setAttr7_xxx_1234567890(value);
        this.setAttr8_xxx_1234567890(value);
        this.setAttr9_xxx_1234567890(value);
        List <String> attrsList = new ArrayList<>();
        attrsList.add(attr0_xxx_1234567890);
        attrsList.add(attr1_xxx_1234567890);
        attrsList.add(attr2_xxx_1234567890);
        attrsList.add(attr3_xxx_1234567890);
        attrsList.add(attr4_xxx_1234567890);
        attrsList.add(attr5_xxx_1234567890);
        attrsList.add(attr6_xxx_1234567890);
        attrsList.add(attr7_xxx_1234567890);
        attrsList.add(attr8_xxx_1234567890);
        attrsList.add(attr9_xxx_1234567890);
    }

    public String getAttr0_xxx_1234567890() {
        return attr0_xxx_1234567890;
    }

    public void setAttr0_xxx_1234567890(String attr0_xxx_1234567890) {
        this.attr0_xxx_1234567890 = attr0_xxx_1234567890;
    }

    public String getAttr1_xxx_1234567890() {
        return attr1_xxx_1234567890;
    }

    public void setAttr1_xxx_1234567890(String attr1_xxx_1234567890) {
        this.attr1_xxx_1234567890 = attr1_xxx_1234567890;
    }

    public String getAttr2_xxx_1234567890() {
        return attr2_xxx_1234567890;
    }

    public void setAttr2_xxx_1234567890(String attr2_xxx_1234567890) {
        this.attr2_xxx_1234567890 = attr2_xxx_1234567890;
    }

    public String getAttr3_xxx_1234567890() {
        return attr3_xxx_1234567890;
    }

    public void setAttr3_xxx_1234567890(String attr3_xxx_1234567890) {
        this.attr3_xxx_1234567890 = attr3_xxx_1234567890;
    }

    public String getAttr4_xxx_1234567890() {
        return attr4_xxx_1234567890;
    }

    public void setAttr4_xxx_1234567890(String attr4_xxx_1234567890) {
        this.attr4_xxx_1234567890 = attr4_xxx_1234567890;
    }

    public String getAttr5_xxx_1234567890() {
        return attr5_xxx_1234567890;
    }

    public void setAttr5_xxx_1234567890(String attr5_xxx_1234567890) {
        this.attr5_xxx_1234567890 = attr5_xxx_1234567890;
    }

    public String getAttr6_xxx_1234567890() {
        return attr6_xxx_1234567890;
    }

    public void setAttr6_xxx_1234567890(String attr6_xxx_1234567890) {
        this.attr6_xxx_1234567890 = attr6_xxx_1234567890;
    }

    public String getAttr7_xxx_1234567890() {
        return attr7_xxx_1234567890;
    }

    public void setAttr7_xxx_1234567890(String attr7_xxx_1234567890) {
        this.attr7_xxx_1234567890 = attr7_xxx_1234567890;
    }

    public String getAttr8_xxx_1234567890() {
        return attr8_xxx_1234567890;
    }

    public void setAttr8_xxx_1234567890(String attr8_xxx_1234567890) {
        this.attr8_xxx_1234567890 = attr8_xxx_1234567890;
    }

    public String getAttr9_xxx_1234567890() {
        return attr9_xxx_1234567890;
    }

    public void setAttr9_xxx_1234567890(String attr9_xxx_1234567890) {
        this.attr9_xxx_1234567890 = attr9_xxx_1234567890;
    }

    public List<String> getAttrsList_1234567890() {
        return attrsList_1234567890;
    }

    public void setAttrsList_1234567890(List<String> attrsList_1234567890) {
        this.attrsList_1234567890 = attrsList_1234567890;
    }

    public static void main(String[] args) {
        BaseInfo baseInfo = new BaseInfo();
        System.out.println(JSONObject.toJSONString(baseInfo));
    }
}
