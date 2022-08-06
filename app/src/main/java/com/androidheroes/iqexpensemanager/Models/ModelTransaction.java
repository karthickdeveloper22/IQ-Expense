package com.androidheroes.iqexpensemanager.Models;

public class ModelTransaction {

    private String type, category, note, timestamp;
    private int amount, userid, trans_id;

    public ModelTransaction() {
    }

    public ModelTransaction(int trans_id, String type, String category, int amount, String note, int userid, String timestamp) {
        this.type = type;
        this.category = category;
        this.note = note;
        this.timestamp = timestamp;
        this.amount = amount;
        this.userid = userid;
        this.trans_id = trans_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getTrans_id() {
        return trans_id;
    }

    public void setTrans_id(int trans_id) {
        this.trans_id = trans_id;
    }
}
