import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        FeatureFinder featureFinder = new FeatureFinder();

        featureFinder.findBestWithoutSFS();
        featureFinder.findBestWithSFS();
    }
}
