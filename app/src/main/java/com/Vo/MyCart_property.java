package com.Vo;

/**
 * Created by hitesh.singh on 7/6/2016.
 */
public class MyCart_property {

    private int id;
    private String category_id;
    private String brand_id;
    private String p_name;
    private String p_rate;
    private String p_margin;
    private String offer_price;
    private String order_qty;
    private String quantity;
    private String delivery_txt;
    private String image;
    private String p_id;
    private String u_id;
    private String desc;
    private String delivery;
    private String promo;
    private String tax_id;
    private String tax_charge;
    private String foodId;
    private String subFoodId;
    private String regular;
    private String spice;
    private String typevalue;
    private int clientId;
    private int orderId;



    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }
    public String getSubFoodId() {
        return subFoodId;
    }

    public void setSubFoodId(String subFoodId) {
        this.subFoodId = subFoodId;
    }

    public String getRegular() {
        return regular;
    }

    public void setRegular(String regular) {
        this.regular = regular;
    }
    public String getTypevalue() {
        return typevalue;
    }

    public void setTypevalue(String typevalue) {
        this.typevalue = typevalue;
    }






    public MyCart_property() {

    }

    /*  public MyCart_property(String c_id, String b_id, String name, String rate, String margin, String offer, String s, String pack, String des, String image, String pID, String u_id,String desc,String delivery,String promo) {
          this.category_id = c_id;
          this.brand_id = b_id;
          this.p_name = name;
          this.p_rate = rate;
          this.p_margin = margin;
          this.offer_price = offer;
          this.order_qty = s;
          this.quantity = pack;
          this.delivery_txt = des;
          this.image = image;
          this.p_id = pID;
          this.u_id = u_id;
         this.desc=desc;
         this.setDelivery(delivery);
         this.setPromo(promo);
      }
  */
    public MyCart_property(String productName, String cid, String id, String description, String rate, String oder_qut, String quantity, String home_delivery, String imageName, String pid, String member_id, String promo_code, String tax_id, String tax_charge,String foodId,String subFoodId,String regular, String spice,String typevalue) {
        this.p_name = productName;
        this.category_id = cid;
        this.brand_id = id;
        this.desc = description;
        this.p_rate = rate;
        this.order_qty = oder_qut;
        this.quantity = quantity;
        this.delivery = home_delivery;
        this.image = imageName;
        this.p_id = pid;
        this.u_id = member_id;
        this.promo = promo_code;
        this.tax_id = tax_id;
        this.setTax_charge(tax_charge);
        this.foodId = foodId;
        this.subFoodId = subFoodId;
        this.regular = regular;
     this.spice=spice;
        this.typevalue=typevalue;

    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getBrand_id() {
        return brand_id;
    }

    public void setBrand_id(String brand_id) {
        this.brand_id = brand_id;
    }

    public String getP_name() {
        return p_name;
    }

    public void setP_name(String p_name) {
        this.p_name = p_name;
    }

    public String getP_rate() {
        return p_rate;
    }

    public void setP_rate(String p_rate) {
        this.p_rate = p_rate;
    }



    public String getOrder_qty() {
        return order_qty;
    }

    public void setOrder_qty(String order_qty) {
        this.order_qty = order_qty;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getP_id() {
        return p_id;
    }

    public void setP_id(String p_id) {
        this.p_id = p_id;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDelivery() {
        return delivery;
    }

    public void setDelivery(String delivery) {
        this.delivery = delivery;
    }

    public String getPromo() {
        return promo;
    }

    public void setPromo(String promo) {
        this.promo = promo;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getTax_charge() {
        return tax_charge;
    }

    public void setTax_charge(String tax_charge) {
        this.tax_charge = tax_charge;
    }

    public String getSpice() {
        return spice;
    }

    public void setSpice(String spice) {
        this.spice = spice;
    }
}
