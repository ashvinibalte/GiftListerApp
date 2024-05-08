package models;

import com.google.firebase.auth.FirebaseAuth;

public class GiftListProduct {
    private String docId;
    private String name;
    private double price;
    private boolean isPledged;
    private double pledgedAmount; // Amount pledged towards this product

    private String pledgedBy;
    private String creatorId;
    private String giftListId;
    private String img_url;

    public GiftListProduct() {
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getGiftListId() {
        return giftListId;
    }

    public void setGiftListId(String giftListId) {
        this.giftListId = giftListId;
    }

    public double getPledgedAmount() {
        return pledgedAmount;
    }

    public void setPledgedAmount(double pledgedAmount) {
        this.pledgedAmount = pledgedAmount;
    }

    // Getters and setters
    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isPledged() {
        return isPledged;
    }

    public void setPledged(boolean pledged) {
        isPledged = pledged;
    }

    public String getPledgedBy() {
        return pledgedBy;
    }

    public void setPledgedBy(String pledgedBy) {
        this.pledgedBy = pledgedBy;
    }

    public boolean isCreatedByCurrentUser() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : "";
        return currentUserId.equals(creatorId);
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
