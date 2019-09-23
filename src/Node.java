public class Node {

    private int[][] state;
    private int parentId;
    private int id;
    private int fCost;
    private int gCost;
    private int hCost;

    public int[][] getState() {
        return state;
    }

    public int getParentId() {
        return parentId;
    }

    public int getId() {
        return id;
    }

    public int getfCost() {
        return fCost;
    }

    public int getgCost() {
        return gCost;
    }

    public int gethCost() {
        return hCost;
    }

    public void setState(int[][] state) {
        this.state = state;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setfCost(int fCost) {
        this.fCost = fCost;
    }

    public void setgCost(int gCost) {
        this.gCost = gCost;
    }

    public void sethCost(int hCost) {
        this.hCost = hCost;
    }
}
