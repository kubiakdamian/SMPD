import java.util.Comparator;

public class DistanceComparator implements Comparator<Distance> {
    @Override
    public int compare(Distance o1, Distance o2) {
        return Double.compare(o1.getDistance(), o2.getDistance());
    }
}
