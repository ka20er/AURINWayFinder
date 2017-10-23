package ka20er.aurinwayfinder;

public class RelevantLocation {
    private double savedLongitude;
    private double savedLatitude;
    private String savedTradeName;
    private String savedCategory;
    private String savedTradeHour;
    private String savedAfterHour;
    private String savedAddress;
    private String savedSuburb;
    private int savedPostcode;

    /*
    * For Public Toilet the savedTradeHour stores Male indicator and savedAfterHour stores female
    * For VIC School and VIC Rec and Sport Centre
    * savedTradeHour and savedAfterHour are null or not applicable
    * For VIC Bus, VIC Tram
    * savedAddress is ZONE
    * savedCategory, savedTradeHour, savedAfterHour, savedSuburb, savedPostCode are not applicable
    * For VIC Train,
    * savedAddress is ZONE and savedCategory is STATIONTYPE
    * savedTradeHour, savedAfterHour, savedSuburb, savedPostCode are not applicable
    */
    public RelevantLocation (double longitude, double latitude, String name, String category, String tradeHour,
                             String afterHour, String address, String suburb, int postcode) {
        this.savedLongitude = longitude;
        this.savedLatitude = latitude;
        this.savedTradeName = name;
        this.savedCategory = category;
        this.savedTradeHour = tradeHour;
        this.savedAfterHour = afterHour;
        this.savedAddress = address;
        this.savedSuburb = suburb;
        this.savedPostcode = postcode;
    }

    public double getSavedLongitude() {
        return savedLongitude;
    }

    public double getSavedLatitude() {
        return savedLatitude;
    }

    public String getSavedTradeName() {
        return savedTradeName;
    }

    public String getSavedCategory() {
        return savedCategory;
    }

    public String getSavedTradeHour() {
        return savedTradeHour;
    }

    public String getSavedAfterHour() {
        return savedAfterHour;
    }

    public String getSavedAddress() {
        return savedAddress;
    }

    public String getSavedSuburb() {
        return savedSuburb;
    }

    public int getSavedPostcode() {
        return savedPostcode;
    }
}
