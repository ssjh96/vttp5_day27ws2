package vttp5.paf.day27ws2.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.bson.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp5.paf.day27ws2.repository.ReviewRepo;
import vttp5.paf.day27ws2.utils.Constant;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepo reviewRepo;

    public JsonObject insertReview(String gameId, String user, Double rating, String comment)
    {
        // Fetch the Game document
        Optional<Document> optGameDoc = reviewRepo.getGameById(gameId);

        if (optGameDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No game found for given game id")
                                    .build();
            
            return jError;
        }

        Document gameDoc = optGameDoc.get();

        // Create review JSON
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        JsonObject jReview = Json.createObjectBuilder()
                                .add("_id", gameDoc.getObjectId("_id").toString())
                                .add("user", user)
                                .add("rating", rating)
                                .add("comment", comment != null ? comment : "")
                                .add("ID", gameDoc.getObjectId("_id").toString())
                                .add("posted", sdf.format(new Date()))
                                .add("name", gameDoc.getString("name"))
                                .build();
                             
        // Insert review into MongoDB                       
        Document docReviewToInsert = Document.parse(jReview.toString());
        reviewRepo.insertReview(docReviewToInsert);
       

        // Return success response
        JsonObject jSuccess = Json.createObjectBuilder()
                                    .add("success", "Review Submmited")
                                    .build();
            
        return jSuccess;
    }
}
