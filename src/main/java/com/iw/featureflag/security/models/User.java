package com.iw.featureflag.security.models;

import com.iw.featureflag.model.Feature;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class User implements Serializable {
    private static final long serialVersionUID = 4408418647685225829L;
    private String uid;
    private String name;
    private String email;
    private List<String> features;

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", features='" + features.toString() + '\'' +
                '}';
    }
}
