package vttp5.paf.day27ws2.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp5.paf.day27ws2.repository.ReviewRepo;

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

        // Create review JSON, (2 options)
        // 1. Document docReviewToInsert = new Document().append("_id", new ObjectId()).append("user", user)
        // 2. Use JsonObjectBuilder and append as required after converting to Document
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        JsonObject jReview = Json.createObjectBuilder()
                                // .add("_id", gameDoc.getObjectId("_id").toString())  
                                // This would not give ObjectId("") because it is save as a string and not a ObjectId object, but just the ""

                                .add("user", user)
                                .add("rating", rating)
                                .add("comment", comment != null ? comment : "")
                                .add("ID", gameDoc.getObjectId("_id").toString())
                                .add("posted", sdf.format(new Date()))
                                .add("name", gameDoc.getString("name"))
                                .build();
                             
        // Insert review into MongoDB                       
        Document docReviewToInsert = Document.parse(jReview.toString());
        // docReviewToInsert.append("_id", gameDoc.getObjectId("_id")); // This will appear on top as it is unique primary
        docReviewToInsert.append("_id", new ObjectId()); // Create new unique ObjectId for review

        reviewRepo.insertReview(docReviewToInsert);
       

        // Return success response
        JsonObject jSuccess = Json.createObjectBuilder()
                                    .add("success", "Review Submmited")
                                    .build();
            
        return jSuccess;
    }





    public JsonObject updateReview(String reviewId, Double newRating, String newComment)
    {
        // Validate input rating
        if (newRating < 0 || newRating > 10)
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "New rating must be between 0 and 10")
                                    .build();
            
            return jError;
        }
        
        // Fetch existing review document
        Optional<Document> optReviewDoc = reviewRepo.getReviewById(reviewId);

        if (optReviewDoc.isEmpty())
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No review found for given review id")
                                    .build();
            
            return jError;
        }

        // Create update document
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String postedDate = sdf.format(new Date());

        // Document existingReviewDoc = optReviewDoc.get();
        Document editedEntryDoc = new Document()
            // .append("comment", newComment != null ? newComment : existingReviewDoc.getString("comment"))
            // .append("rating", newRating != null ? newRating : existingReviewDoc.getDouble("rating"))
            .append("comment", newComment)
            .append("rating", newRating)
            .append("posted", postedDate);
        
        // Perform the update
        long modifiedCount = reviewRepo.updateReview(reviewId, editedEntryDoc);
        
        if (modifiedCount == 0)
        {
            JsonObject jError = Json.createObjectBuilder()
                                    .add("error", "No modification done")
                                    .build();
            
            return jError;
        }
        
        JsonObject jSuccess = Json.createObjectBuilder()
                                    .add("success", "Update completed")
                                    .build();
            
        return jSuccess;
    }
}
