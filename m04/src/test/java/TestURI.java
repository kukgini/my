import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by soomin on 2015. 12. 4..
 */
public class TestURI {
    public static void main(String[] args) throws URISyntaxException {
        URI uri = new URI("http://example.com/foo/bar/42?param=true");
        String[] sa = uri.getPath().split("/");
        System.out.println(sa[1]);
        System.out.println(uri.getPath());
    }
}
