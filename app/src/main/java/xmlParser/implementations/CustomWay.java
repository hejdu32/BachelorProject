package xmlParser.implementations;

import java.util.List;

public class CustomWay {
    private Long id;
    private List<Long> nodeIdList;
    private String tagId;

    public CustomWay(Long id, List<Long> nodeIdList, String tagId) {
        this.id = id;
        this.nodeIdList = nodeIdList;
        this.tagId = tagId;
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

}
