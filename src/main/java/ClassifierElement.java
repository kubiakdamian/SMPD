import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ClassifierElement {
    private int index;
    private List<Double> values;
    private String name;

    public ClassifierElement(int index, String name) {
        this.index = index;
        this.name = name;
        values = new ArrayList<>();
    }
}
