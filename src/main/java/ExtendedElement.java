import lombok.Data;

@Data
public class ExtendedElement extends Element {

    private int followIndex;

    public ExtendedElement(double value, int index) {
        super(value, index);
    }

    public ExtendedElement(double value, int index, int followIndex) {
        super(value, index);
        this.followIndex = followIndex;
    }
}
