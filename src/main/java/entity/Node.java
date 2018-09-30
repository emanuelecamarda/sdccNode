package entity;

public class Node {

    private Long id;
    private String ipAddr;

    public Node(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public void update(Node node) {
        if(node.ipAddr !=null)
            this.ipAddr = node.ipAddr;
    }
}
