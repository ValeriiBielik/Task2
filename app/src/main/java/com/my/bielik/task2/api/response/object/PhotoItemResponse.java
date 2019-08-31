package com.my.bielik.task2.api.response.object;

public class PhotoItemResponse {
    private String id;
    private String secret;
    private String server;
    private String farm;

    public PhotoItemResponse(String id, String secret, String server, String farm) {
        this.id = id;
        this.secret = secret;
        this.server = server;
        this.farm = farm;
    }

    public String getUrl() {
        return "http://farm" + farm + ".static.flickr.com/" + server + "/" + id + "_" + secret + ".jpg";
    }

    public String getId() {
        return id;
    }
}