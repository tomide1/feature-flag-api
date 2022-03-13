package com.iw.featureflag.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.iw.featureflag.model.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class FeatureService {

    private Firestore firestore;

    @Autowired
    public FeatureService(Firestore firestore) {
        this.firestore = firestore;
    }

    public String saveFeature(Feature feature) {
        try {
            ApiFuture<WriteResult> collectionsApiFuture = firestore.collection("features")
                    .document(feature.getId())
                    .set(feature);
            return collectionsApiFuture.get().getUpdateTime().toString();
        } catch(Exception ex) {
            return "Something wet wrong";
        }
    }

    public Feature getFeature(String featureId) {
        try {
            DocumentReference documentReference = firestore.collection("features")
                    .document(featureId);
            ApiFuture<DocumentSnapshot> future = documentReference.get();
            DocumentSnapshot document = future.get();

            if (document.exists()) {
                return document.toObject(Feature.class);
            }
        } catch (Exception ex) {
            return null;
        }

        return null;
    }
}
