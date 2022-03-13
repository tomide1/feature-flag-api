package com.iw.featureflag.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.iw.featureflag.security.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private Firestore firestore;

    @Autowired
    public UserService(Firestore firestore) {
        this.firestore = firestore;
    }

    public String saveUser(User user) {
        try {
            ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("users")
                    .document(user.getUid())
                    .set(user);
            return collectionsApiFuture.get().getUpdateTime().toString();
        } catch(Exception ex) {
            return "Something wet wrong";
        }
    }

    public User getUser(String userUid) {
        try {
            DocumentReference documentReference = firestore.collection("users")
                    .document(userUid);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(User.class);
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }
}
