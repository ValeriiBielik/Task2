package com.my.bielik.task2.response_objects;

public class PhotoItem {
    private String id;
    private String secret;
    private String server;
    private String farm;

    public PhotoItem(String id, String secret, String server, String farm) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
    }

    public String getUrl() {
        return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

}