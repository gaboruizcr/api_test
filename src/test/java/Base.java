import helpers.RequestHelpers;
import io.restassured.RestAssured;
import org.testng.annotations.*;

public class Base {

    protected int postId;
    protected int commentId;
    protected int invalidId = 200;


    @Parameters("host")
    @BeforeSuite(alwaysRun = true)
    public void setup(@Optional("https://api-coffee-testing.herokuapp.com") String host) {

        System.out.println(String.format("Test Host: %s", host));

        RestAssured.baseURI = host;
    }

    // before groups

    @BeforeMethod(groups = "usePost")
    void createPost(){
        postId = RequestHelpers.createRandomPostAndGetID();
        commentId = RequestHelpers.createRandomCommentAndGetID(postId);
    }

    @AfterMethod(groups = "usePost")
    void deletePost(){
        RequestHelpers.cleanUpPost(postId);
    }

}
