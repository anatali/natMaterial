package healthAdapter.itelData;

public class ImportPayload {
    private String resourceId;
    private String businessId;

    public String getResourceId() {
        return resourceId;
    }
    public String getBusinessId() {
        return businessId;
    }

    public void setResourceId(String id) {
        this.resourceId = id;
    }
    public void setBusinessId(String id) {
        this.businessId = id;
    }

    public String toString(){
        String rep = "{\"resourceId\" : \""+ resourceId + "\", \"businessId\" : \""+businessId+"\"}";
        //System.out.println("ImportPayload rep="+rep);
        return rep;
    }
}