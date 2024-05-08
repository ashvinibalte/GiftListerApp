package models;

import org.json.JSONObject;

public class Product {
    public String pid, name, img_url;
    public double price;
    public boolean isSelected = false;

    public Product() {
    }

    public Product(JSONObject json) {
        this.pid = json.optString("pid");
        this.name = json.optString("name");
        this.img_url = json.optString("img_url");
        this.price = json.optDouble("price");
        this.isSelected = false;
        /*
    {
            "pid": "3f022383-7663-4efa-890a-f278301f3377",
            "name": "Charlotte 49ers 18oz. Stainless Steel Soft Touch Tumbler",
            "img_url": "https://www.theappsdr.com/items-imgs/charlotte-49ers-18oz-stainless-steel-soft-touch-tumbler.png",
            "price": "22.95"
        },
     */
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
