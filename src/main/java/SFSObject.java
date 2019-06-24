import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SFSObject {
    private List<Element> bestElements = new ArrayList<>();
    private Element activeBestElements;
    private List<Integer> elementsIndexes = new ArrayList<>();
}
