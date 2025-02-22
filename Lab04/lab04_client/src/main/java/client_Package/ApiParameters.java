package client_Package;

public class ApiParameters {
    private String province;
    private String city;
    private String benefit;
    private Boolean forChildren;

    public ApiParameters(String province, String city, String benefit, Boolean forChildren) {
        this.province = province;
        this.city = city;
        this.benefit = benefit;
        this.forChildren = forChildren;
    }

    public String getProvince() {
        return province;
    }

    public String getCity() {
        return city;
    }

    public String getBenefit() {
        return benefit;
    }

    public Boolean getForChildren() {
        return forChildren;
    }
}
