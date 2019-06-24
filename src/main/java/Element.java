import lombok.Data;

@Data
public class Element implements Comparable<Element> {
    private double value;
    private int index;

    public Element(double value, int index) {
        this.value = value;
        this.index = index;
    }

    public int compareTo(Element o) {
        double comperage = o.getValue() - this.getValue();
        double comperage2 = o.getIndex() - this.getIndex();
        if (comperage2 > 0) {
            return 1;
        } else if (comperage2 < 0) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "Element " + index;
    }
}
