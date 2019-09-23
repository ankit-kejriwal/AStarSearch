import java.util.Comparator;

public class FCostComparator implements Comparator<Node> {
    @Override
    public int compare(Node o1, Node o2) {
        if(o1.getfCost()< o2.getfCost())
            return -1;
        if(o1.getfCost() > o2.getfCost())
            return 1;
        return 0;
    }
}
