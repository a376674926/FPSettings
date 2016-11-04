package cn.stj.settings.entity;
//add by hhj@20160712 for sim setting
public class SimInfo {

    private int cardSlot;
    private String carrier;
    private String phoneNum;
    
    public SimInfo() {
        super();
    }
    public SimInfo(int cardSlot, String carrier, String phoneNum) {
        super();
        this.cardSlot = cardSlot;
        this.carrier = carrier;
        this.phoneNum = phoneNum;
    }
    public int getCardSlot() {
        return cardSlot;
    }
    public void setCardSlot(int cardSlot) {
        this.cardSlot = cardSlot;
    }
    public String getCarrier() {
        return carrier;
    }
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    
    
    
}
