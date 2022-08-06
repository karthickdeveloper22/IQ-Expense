package com.androidheroes.iqexpensemanager.Models;

public class ModelBudgets {
    String title, description, timestamp;
    int amount, budget_id, userid, spend;

    public ModelBudgets(String title, String description, String timestamp, int amount, int budget_id, int userid, int spend) {
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.amount = amount;
        this.budget_id = budget_id;
        this.userid = userid;
        this.spend = spend;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public int getBudget_id() {
        return budget_id;
    }

    public void setBudget_id(int budget_id) {
        this.budget_id = budget_id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getSpend() {
        return spend;
    }

    public void setSpend(int spend) {
        this.spend = spend;
    }
}
