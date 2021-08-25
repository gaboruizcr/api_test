import Specififactions.RequestSpecifications;
import Specififactions.ResponseSpecifications;
import io.restassured.response.Response;
import model.Post;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

public class PostTests extends Base{

    @Test(description = "This test aims to create a new post")
    public void createPostTest(){

        Post testPost = new Post("some_title", "some_content");

        given().spec(RequestSpecifications.useJWTAuthentication())
                .body(testPost)
                .when()
                .post("/v1/post")
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Post created"));
    }

    @Test(description = "This test fails to create a new post")
    public void createPostInvalidTest(){

        Post testPost = new Post("some_title", "some_content");

        given().spec(RequestSpecifications.useFakeJWTAuthentication())
                .body(testPost)
                .when()
                .post("/v1/post")
                .then()
                .log().all()
                .statusCode(401)
                .body("message", Matchers.equalTo("Please login first"));
    }

    @Test(description = "This test aims to get all posts", groups = "usePost")
    public void getAllPostTest(){

        given().spec(RequestSpecifications.useJWTAuthentication())
                .when()
                .get("/v1/posts")
                .then()
                .log().all()
                .statusCode(200)
                .body("results[0].data[0].id", Matchers.equalTo(postId));
    }

    @Test(description = "This test fails to get all posts", groups = "usePost")
    public void getAllPostInvalidTest(){

        given().spec(RequestSpecifications.useFakeJWTAuthentication())
                .when()
                .get("/v1/posts")
                .then()
                .log().all()
                .statusCode(401)
                .body("message", Matchers.equalTo("Please login first"));
    }

    @Test(description = "This test aims to get one post")
    public void getOnePostTest(){

        Response response = given().spec(RequestSpecifications.useJWTAuthentication())
                .when()
                .get("/v1/post/" + postId);

        assertThat(response.asString(), matchesJsonSchemaInClasspath("article.schema.json"));
        assertThat(response.path("data.id"), Matchers.equalTo(postId));

    }

    @Test(description = "This test fails to get one post")
    public void getOnePostInvalidTest(){

        Response response = given().spec(RequestSpecifications.useJWTAuthentication())
                .when()
                .get("/v1/post/" + (postId * invalidId));

        assertThat(response.path("Message"), Matchers.equalTo("Post not found"));
        assertThat(response.path("error"), Matchers.equalTo("sql: no rows in result set"));

    }

    @Test(description = "This test aims to update a specific post")
    public void putUpdatePostTest(){

        Post testPost = new Post("some_title", "some_content");

        given().spec(RequestSpecifications.useJWTAuthentication())
                .body(testPost)
                .when()
                .put("/v1/post/" + postId)
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Post updated"));
    }

    @Test(description = "This test aims to update a specific post")
    public void putUpdatePostInvalidTest(){

        Post testPost = new Post("some_title", "some_content");

        given().spec(RequestSpecifications.useJWTAuthentication())
                .body(testPost)
                .when()
                .put("/v1/post/" + (postId * invalidId))
                .then()
                .log().all()
                .statusCode(406)
                .body("message", Matchers.equalTo("Post could not be updated"))
                .body("error", Matchers.equalTo("Post not found"));
    }

    @Test(description = "This test aims to delete an specific post")
    public void deletePostTest(){

        given().spec(RequestSpecifications.useJWTAuthentication())
                .when()
                .delete("/v1/post/" + postId)
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Post deleted"));
    }

    @Test(description = "This test fails to delete an specific post")
    public void deletePostInvalidTest(){

        given().spec(RequestSpecifications.useJWTAuthentication())
                .when()
                .delete("/v1/post/" + (postId * invalidId))
                .then()
                .log().all()
                .statusCode(406)
                .body("message", Matchers.equalTo("Post could not be deleted"))
                .body("error", Matchers.equalTo("Post not found"));
    }

}
