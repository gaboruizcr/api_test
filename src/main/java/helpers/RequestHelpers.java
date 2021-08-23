package helpers;

import Specififactions.RequestSpecifications;
import Specififactions.ResponseSpecifications;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import model.Comments;
import model.Post;
import model.User;
import org.hamcrest.Matchers;

import static io.restassured.RestAssured.given;

public class RequestHelpers {

    public static String TOKEN = "";

    //   get a json token from the login request
    public static String getAuthToken () {

        User testUser = new User(
                "Pablo Juan",
                "Alexie_Robel@yahoo.com",
                "pasword");

        Response response = given().body(testUser).post("/v1/user/login");
        JsonPath jsonPath = response.jsonPath();
        TOKEN = jsonPath.get("token.access_token");
        System.out.println("New token fetched" + TOKEN);
        return TOKEN;
    }

    public static int createRandomPostAndGetID () {
        Post randomPost = new Post("random", "random");
       Response response = given().spec(RequestSpecifications.useJWTAuthentication())
                .body(randomPost)
                .when()
                .log().all()
                .post("/v1/post");

        JsonPath jsonPath = response.jsonPath();
        return jsonPath.get("id");
    }

    public static int createRandomCommentAndGetID (int id) {
        Comments randomComment = new Comments("random", "random");
        Response response = given().spec(RequestSpecifications.userBasicAuthentication())
                .body(randomComment)
                .when()
                .log().all()
                .post("/v1/comment/" + id);

        JsonPath jsonPath = response.jsonPath();
        return jsonPath.get("id");
    }

    public static void cleanUpPost (int id) {
        given().spec(RequestSpecifications.useJWTAuthentication())
                .delete("/v1/post/"+ id);
    }

}
