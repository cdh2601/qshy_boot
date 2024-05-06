package com.qshy.entity;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @Classname RouteScenic
 * @Description TODO
 * @Date 2023/4/17 23:39
 * @Created by senorisky
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteScenic implements Serializable {

    public RouteScenic(Scenic scenic, float longitude, float latitude) {
        this.scenicId = scenic.getScenicId();
        this.scenicName = scenic.getScenicName();
        this.introduction = scenic.getIntroduction();
        this.playTime = scenic.getPlayTime();
        this.latitude = scenic.getLatitude();
        this.location = scenic.getLocation();
        this.ticket = scenic.getTicket();
        this.openTime = scenic.getOpenTime();
        this.phone = scenic.getPhone();
        this.traffic = scenic.getTraffic();
        this.longitude = scenic.getLongitude();
        this.text = scenic.getText();
        this.likesUsers = scenic.getLikesUsers();
        this.collectionUsers = scenic.getCollectionUsers();
        this.area = scenic.getArea();
        this.month = scenic.getMonth();
        this.commentUsers = scenic.getCommentUsers();
        this.stype = scenic.getStype();
        this.score = scenic.getScore();
        this.scenicImages = scenic.getScenicImages();
        this.nextLatitude = latitude;
        this.nextLongtitude = longitude;
    }

    private static final long serialVersionUID = 1L;
    private String scenicId;

    private String scenicName;

    private String introduction;

    private Float latitude;

    private Float longitude;

    private String month;
    private String stype;
    private String phone;
    private double score;
    private String location;
    private String area;

    private String playTime;

    private List<String> scenicImages;

    private List<String> text;

    private List<String> ticket;

    private List<String> collectionUsers;

    private List<String> likesUsers;

    private List<String> commentUsers;
    private List<String> openTime;
    private List<String> traffic;


    private float nextLongtitude;

    private float nextLatitude;
}
