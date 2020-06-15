package com.ba.contacts;

public class SimContact {
    private String id;
    private String name;
    private String number;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNumber() {
        return number;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public SimContact(String name, String number) {
        this.name = name;
        this.number = number;
    }

    public SimContact(String id, String name, String number) {
        this.id = id;
        this.name = name;
        this.number = number;
    }

}
