package io.bloco.cardcase.data.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@DatabaseTable(tableName = "cards")
public class Card {

    @DatabaseField(generatedId = true)
    private UUID id;
    @DatabaseField(canBeNull = false)
    private String name;
    @DatabaseField
    private String company;
    @DatabaseField
    private String position;
    @DatabaseField
    private String address;
    @DatabaseField
    private String website;
    @DatabaseField
    private String email;
    @DatabaseField
    private String phone;
    @DatabaseField
    private String avatarPath;
    @DatabaseField
    private String linkedinURL;
    @DatabaseField
    private String instagramURL;
    @DatabaseField
    private String vklink;


    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private ArrayList<String> fields;
    @DatabaseField
    private transient Date createdAt;
    @DatabaseField
    private transient Date updatedAt;
    @DatabaseField
    private transient boolean isUser;
    @DatabaseField
    private UUID categoryId;


    public Card() {
        fields = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return id.equals(card.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public Card copy() {
        Card card = new Card();
        card.id = id;
        card.name = name;
        card.company = company;
        card.position = position;
        card.address = address;
        card.website = website;
        card.email = email;
        card.phone = phone;
        card.vklink = vklink;
        card.avatarPath = avatarPath;
        card.fields = new ArrayList<>(getFields());
        card.createdAt = createdAt;
        card.updatedAt = updatedAt;
        card.isUser = isUser;
        card.categoryId = categoryId;
        card.linkedinURL = linkedinURL;
        card.instagramURL = instagramURL;
        return card;
    }

    public void setVklink(String link){
        this.vklink = link;
    }

    public String getVklink(){
        return vklink;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany(){
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition(){
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getAddress(){
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite(){
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public File getAvatar() {
        return new File(avatarPath);
    }

    public void setAvatar(File avatar) {
        this.avatarPath = avatar.getAbsolutePath();
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public boolean hasAvatar() {
        return avatarPath != null;
    }

    public ArrayList<String> getFields() {
        if (fields == null) {
            fields = new ArrayList<>();
        }
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isUser() {
        return isUser;
    }

    public void setIsUser(boolean isUser) {
        this.isUser = isUser;
    }

    public boolean isValid() {
        return !getName().isEmpty();
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getLinkedinURL() {
        return linkedinURL;
    }

    public void setLinkedinURL(String linkedinURL) {
        this.linkedinURL = linkedinURL;
    }

    public String getInstagramURL() {
        return instagramURL;
    }

    public void setInstagramURL(String instagramURL) {
        this.instagramURL = instagramURL;
    }

    public boolean matchQuery(String query) {
        String queryNormalized = query.toLowerCase().trim();

        List<String> fieldsToMatch = new ArrayList<>(fields);
        fieldsToMatch.add(name);
        fieldsToMatch.add(company);
        fieldsToMatch.add(position);
        fieldsToMatch.add(address);
        fieldsToMatch.add(website);
        fieldsToMatch.add(email);
        fieldsToMatch.add(phone);
        fieldsToMatch.add(vklink);
        fieldsToMatch.add(linkedinURL);
        fieldsToMatch.add(instagramURL);

        for (String field : fieldsToMatch) {
            if (field != null && field.toLowerCase().contains(queryNormalized)) {
                return true;
            }
        }

        return false;
    }
}
