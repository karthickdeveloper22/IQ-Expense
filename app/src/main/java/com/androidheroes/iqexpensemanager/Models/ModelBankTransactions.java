package com.androidheroes.iqexpensemanager.Models;

public class ModelBankTransactions {
    private String type, category, note, timestamp;
    private int amount, userid, bank_id, bank_trans_id;

    public ModelBankTransactions(String type, String category, String note, String timestamp, int amount, int userid, int bank_id, int bank_trans_id) {
        this.type = type;
        this.category = category;
        this.note = note;
        this.timestamp = timestamp;
        this.amount = amount;
        this.userid = userid;
        this.bank_id = bank_id;
        this.bank_trans_id = bank_trans_id;
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

    public int getBank_id() {
        return bank_id;
    }

    public void setBank_id(int bank_id) {
        this.bank_id = bank_id;
    }

    public int getBank_trans_id() {
        return bank_trans_id;
    }

    public void setBank_trans_id(int bank_trans_id) {
        this.bank_trans_id = bank_trans_id;
    }
}
