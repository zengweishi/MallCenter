import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

/**
 * @Auther: weishi.zeng
 * @Date: 2020/6/2 10:34
 * @Description:
 */
public class TestPagePath {
    @Value("${pagepath}")
    private String pagePath;
    @Test
    public void test1() {
        System.out.println(pagePath);
    }
}
