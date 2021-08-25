import Specififactions.RequestSpecifications;
import Specififactions.ResponseSpecifications;
import io.restassured.response.Response;
import model.Comments;
import org.hamcrest.Matchers;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommentsTests extends Base{

    @Test(description = "This test aims to create a new comment")
    public void createCommentTest(){

        Comments testComment = new Comments("some_name", "some_comment");

        given().spec(RequestSpecifications.userBasicAuthentication())
                .body(testComment)
                .when()
                .post("/v1/comment/" + postId)
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Comment created"));
    }

    @Test(description = "This test fails to create a new comment")
    public void createCommentInvalidTest(){

        Comments testComment = new Comments("some_name", "some_comment");

        given().spec(RequestSpecifications.userBasicAuthentication())
                .body(testComment)
                .when()
                .post("/v1/comment/" + (postId * invalidId))
                .then()
                .log().all()
                .statusCode(406)
                .body("message", Matchers.equalTo("Comment could not be created"));
    }

    @Test(description = "This test aims to get all comments", groups = "usePost")
    public void getAllCommentsTest(){

        given().spec(RequestSpecifications.userBasicAuthentication())
                .when()
                .get("/v1/comments/" + postId)
                .then()
                .log().all()
                .statusCode(200)
                .body("results[0].data[0].id", Matchers.equalTo(commentId));
    }

    @Test(description = "This test fails to get all comments", groups = "usePost")
    public void getAllCommentsInvalidTest(){

        given().spec(RequestSpecifications.userBasicAuthentication())
                .when()
                .get("/v1/comments/" + (postId * invalidId))
                .then()
                .log().all()
                .statusCode(200)
                .body("results[0].meta.total", Matchers.equalTo(0));
    }

    @Test(description = "This test aims to get one comment")
    public void getOneCommentTest(){

        Response response = given().spec(RequestSpecifications.userBasicAuthentication())
                .when()
                .get("/v1/comment/" + postId + "/" + commentId);

        assertThat(response.path("data.id"), Matchers.equalTo(commentId));

    }

    @Test(description = "This test fails to get one comment")
    public void getOneCommentInvalidTest(){

        Response response = given().spec(RequestSpecifications.userBasicAuthentication())
                .when()
                .get("/v1/comment/" + postId + "/" + (commentId * invalidId));

        assertThat(response.path("Message"), Matchers.equalTo("Comment not found"));
        assertThat(response.path("error"), Matchers.equalTo("sql: no rows in result set"));

    }

    @Test(description = "This test aims to update a specific comment")
    public void putUpdateCommentTest(){

        Comments testComment = new Comments("some_name", "some_comment");

        given().spec(RequestSpecifications.userBasicAuthentication())
                .body(testComment)
                .when()
                .put("/v1/comment/" + postId + "/" + commentId)
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Comment updated"));
    }

    @Test(description = "This test aims to update a specific comment")
    public void putUpdateCommentInvalidTest(){

        Comments testComment = new Comments("some_name", "some_comment");

        given().spec(RequestSpecifications.userBasicAuthentication())
                .body(testComment)
                .when()
                .put("/v1/comment/" + postId + "/" + (commentId * invalidId))
                .then()
                .log().all()
                .statusCode(406)
                .body("message", Matchers.equalTo("Comment could not be updated"))
                .body("error", Matchers.equalTo("Comment not found"));
    }

    @Test(description = "This test aims to delete an specific comment")
    public void deleteCommentTest(){

        given().spec(RequestSpecifications.userBasicAuthentication())
                .when()
                .delete("/v1/comment/" + postId + "/" + commentId)
                .then()
                .log().all()
                .spec(ResponseSpecifications.validatePositiveResponse())
                .body("message", Matchers.equalTo("Comment deleted"));
    }

    @Test(description = "This test fails to delete an specific comment")
    public void deleteCommentInvalidTest(){

        given().spec(RequestSpecifications.userBasicAuthentication())
                .when()
                .delete("/v1/comment/" + postId + "/" + (commentId * invalidId))
                .then()
                .log().all()
                .statusCode(406)
                .body("message", Matchers.equalTo("Comment could not be deleted"))
                .body("error", Matchers.equalTo("Comment not found"));
    }

}
