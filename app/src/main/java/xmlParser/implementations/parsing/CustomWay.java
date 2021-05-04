package xmlParser.implementations.parsing;

import java.util.List;

public class CustomWay {
    private Long id;
    private List<Long> nodeIdList;
    private String tagId;
    private String maxSpeed;
    private boolean oneWay;

    public CustomWay(Long id, List<Long> nodeIdList, String tagId, String maxSpeed) {
        this.id = id;
        this.nodeIdList = nodeIdList;
        this.tagId = tagId;
        this.maxSpeed = maxSpeed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getNodeIdList() {
        return nodeIdList;
    }

    public void setNodeIdList(List<Long> nodeIdList) {
        this.nodeIdList = nodeIdList;
    }

    public String getTagId() {
        return tagId;
    }

    public void setTagId(String tagId) {
        this.tagId = tagId;
    }

    public String getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(String maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public boolean isOneWay() { return oneWay; }

    public void setOneWay(boolean oneWay) { this.oneWay = oneWay; }

}
