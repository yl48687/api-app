package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.text.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * The {@code ApiApp} class is a JavaFX application that interacts with the Open Library API
 * to search for book information and display the results.
 */
public class ApiApp extends Application {
    // HTTP client for making requests to the Open Library API
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2) // uses HTTP protocol version 2 where possible
            .followRedirects(HttpClient.Redirect.NORMAL) // always redirects, except to HTTP
            .build(); // builds and returns a HttpClient object
    private HttpRequest request;
    private HttpResponse<String> response;

    // Open Library API endpoint for searching books
    private static final String OPEN_LIBRARY_SEARCH_ENDPOINT =
        "https://openlibrary.org/search.json";

    // Gson instance for parsing JSON responses
    private static final Gson GSON = new Gson();

    // Results
    private OpenLibraryResult openLibraryResult;
    private GenderizeResult genderizeResult;

    Stage stage;
    Scene scene;
    VBox root;
    HBox searchBar;
    TextField searchField;
    Label searchLabel;
    Label title1, title2;
    Label author1, author2;
    Label rating1, rating2;
    Label gender1, gender2;
    Label publishedDate1, publishedDate2;
    Label intro;
    Label firstLabel;
    Button searchButton;
    ImageView imgView1, imgView2;
    ImageView logoImageView;
    HBox infoBox1, infoBox2;
    VBox detailBox1, detailBox2;
    String urlString;
    String[] titleList = new String[2];
    String[] dateList = new String[2];
    String[] ratingList = new String[2];
    String[] coverList = new String[2];
    String firstName;

    /**
     * Constructs an {@code ApiApp} object.
     */
    public ApiApp() {
        root = new VBox(8);
    } // ApiApp

    /**
     * Initializes and sets up the JavaFX application.
     *
     * @param stage the primary stage for this application, onto which the application
     *              scene can be set
     */
    @Override
    public void start(Stage stage) {
        this.stage = stage;
        init();

        // Setup VBox
        title1.setPrefHeight(50);
        title1.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        title1.setTextAlignment(TextAlignment.CENTER);
        author1.setPrefHeight(50);
        publishedDate1.setPrefHeight(50);
        rating1.setPrefHeight(50);
        gender1.setPrefHeight(50);
        detailBox1.getChildren().addAll(title1, author1, publishedDate1, rating1, gender1);
        detailBox1.setPrefWidth(342);
        detailBox1.setAlignment(Pos.CENTER);
        title2.setPrefHeight(50);
        title2.setFont(Font.font("Monospaced", FontWeight.BOLD, 14));
        title2.setTextAlignment(TextAlignment.CENTER);
        author2.setPrefHeight(50);
        publishedDate2.setPrefHeight(50);
        rating2.setPrefHeight(50);
        gender2.setPrefHeight(50);
        detailBox2.getChildren().addAll(title2, author2, publishedDate2, rating2, gender2);
        detailBox2.setPrefWidth(342);
        detailBox2.setAlignment(Pos.CENTER);

        // Set label to wrap text
        title1.setWrapText(true);
        author1.setWrapText(true);
        publishedDate1.setWrapText(true);
        rating1.setWrapText(true);
        title2.setWrapText(true);
        author2.setWrapText(true);
        publishedDate2.setWrapText(true);
        rating2.setWrapText(true);
        gender1.setWrapText(true);
        gender2.setWrapText(true);

        infoBox1.getChildren().addAll(imgView1, detailBox1);
        infoBox2.getChildren().addAll(imgView2, detailBox2);

        createSearchBar();

        intro = new Label("Search to find the two most relevant information!\n" +
                          "We will also predict (first) author's gender by first name!");
        intro.setTextAlignment(TextAlignment.CENTER);
        root.getChildren().addAll(this.logoImageView, searchBar, intro, infoBox1, infoBox2);
        root.setAlignment(Pos.CENTER);
        scene = new Scene(root);

        // Setup stage
        stage.setTitle("ApiApp!");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();
    } // start

    /**
     * Initializes the application by setting up the UI components and default values.
     */
    public void init() {
        infoBox1 = new HBox();
        detailBox1 = new VBox();
        title1 = new Label("Title");
        author1 = new Label("Author");
        rating1 = new Label("Rating");
        gender1 = new Label("Gender of the Author");
        publishedDate1 = new Label("Published Year");
        infoBox2 = new HBox(8);
        detailBox2 = new VBox();
        title2 = new Label("Title");
        author2 = new Label("Author");
        rating2 = new Label("Rating");
        gender2 = new Label("Gender of the Author");
        publishedDate2 = new Label("Published Year");
        firstLabel = new Label("Search");

        Image defaultImage = new Image("file:resources/default.png");
        imgView1 = new ImageView(defaultImage);
        imgView1.setFitWidth(200);
        imgView1.setFitHeight(250);
        imgView2 = new ImageView(defaultImage);
        imgView2.setFitWidth(200);
        imgView2.setFitHeight(250);

        Image openLibraryLogo = new Image("file:resources/openlibrary_logo.png");
        logoImageView = new ImageView(openLibraryLogo);
        logoImageView.setFitWidth(250);
        logoImageView.setPreserveRatio(true);
    } // init

    /**
     * Creates the search bar UI component, allowing users to enter search queries.
     */
    public void createSearchBar() {
        searchBar = new HBox(8);
        searchButton = new Button("Search");
        searchField = new TextField("Harry Potter and the Sorcerer's Stone");
        searchBar.setHgrow(searchField, Priority.ALWAYS);
        searchButton.setOnAction(event -> {
            String query = searchField.getText();
            HttpResponse<String> response = fetchInfo(query);
            getInfo(response);
        });
        searchBar.getChildren().addAll(firstLabel, searchField, searchButton);
        searchBar.setAlignment(Pos.CENTER_LEFT);
    } // createSearchBar

    /**
     * Parses the response from the Open Library API and updates the application's UI with
     * the search results.
     *
     * @param response the HTTP response from the Open Library API
     */
    public void getInfo(HttpResponse<String> response) {
        if (response == null) {
            System.out.println("Response is null");
            return;
        } // if
        String responseBody = response.body().trim();
        if (responseBody.startsWith("{")) {
            openLibraryResult = GSON.fromJson(responseBody, OpenLibraryResult.class);
            OpenLibraryDoc[] docs = openLibraryResult.docs;
            if (docs.length >= 2) {
                updateInfo(docs[0].title, docs[0].publishYear, docs[0].averageRating,
                           docs[1].title, docs[1].publishYear, docs[1].averageRating);
                List<String> authors1 = new ArrayList<String>();
                if (docs[0].authorName != null) {
                    for (String author : docs[0].authorName) {
                        authors1.add(author);
                    } // for
                } // if
                List<String> authors2 = new ArrayList<String>();
                if (docs[1].authorName != null) {
                    for (String author : docs[1].authorName) {
                        authors2.add(author);
                    } // for
                } // if
                updateAuthor(authors1, authors2);
                HttpResponse<String> response2 = fetchGender(authors1);
                getGender(response2, this.gender1);
                HttpResponse<String> response3 = fetchGender(authors2);
                getGender(response3, this.gender2);
                updateImage(imgView1, docs[0].coverImage);
                updateImage(imgView2, docs[1].coverImage);
            } else {
                createAlert("There must be at least two results found. Search Again.");
                return;
            } // if
        } // if
    } // getInfo

    /**
     * Sends a request to the Open Library API with the given search query and returns the response.
     *
     * @param searchQuery the search query used to query the Open Library API
     * @return the HTTP response containing information about the search results
     */
    public HttpResponse<String> fetchInfo(String searchQuery) {
        String searchURL = OPEN_LIBRARY_SEARCH_ENDPOINT + "?q=";
        String encodedQuery = URLEncoder.encode(searchQuery, StandardCharsets.UTF_8);
        HttpResponse<String> response = null;
        searchURL += encodedQuery + "&mode=everything";
        encodedQuery = URLEncoder.encode(searchURL, StandardCharsets.UTF_8);
        this.urlString = searchURL;
        try {
            request = HttpRequest.newBuilder()
                .uri(URI.create(searchURL))
                .build();
            this.response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (this.response.statusCode() != 200) {
                throw new IOException();
            } // if
        } catch (IOException e) {
            createAlert("java.io.IOException");
        } catch (InterruptedException e) {
            createAlert("java.lang.InterruptedException");
        } // try
        return this.response;
    } // fetchInfo

    /**
     * Parses the response from the Genderize API and updates the application's UI with
     * the search results.
     *
     * @param response the HTTP response from the Genderize API
     * @param gender the label which will be updated
     */
    public void getGender(HttpResponse<String> response, Label gender) {
        if (response == null) {
            System.out.println("Response is null");
            return;
        } // if
        String responseBody = response.body().trim();
        if (responseBody.startsWith("{")) {
            genderizeResult = GSON.fromJson(responseBody, GenderizeResult.class);
            String genderResult = genderizeResult.gender;
            gender.setText(this.firstName + " is likely " + genderResult);
        } // if
    } // getGender

    /**
     * Sends a request to the Genderize API with the given search query and returns the response.
     *
     * @param authors the list that contains the names of authors
     * @return the HTTP response containing information about the search results
     */
    public HttpResponse<String> fetchGender(List<String> authors) {
        String genderUrl = "https://api.genderize.io?name=";
        this.firstName = authors.isEmpty() ? "" : authors.get(0).split("\\s+")[0];

        // Build the URL with the first author's first name
        String url = genderUrl + URLEncoder.encode(firstName, StandardCharsets.UTF_8);
        try {
            request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();
            this.response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (this.response.statusCode() != 200) {
                throw new IOException();
            } // if
        } catch (IOException e) {
            createAlert("java.io.IOException");
        } catch (InterruptedException e) {
            createAlert("java.lang.InterruptedException");
        } // try
        return this.response;
    } // fetchInfo

    /**
     * Updates the displayed book information based on the search results.
     *
     * @param title1 title of the first book
     * @param date1 publish date of the first book
     * @param rating1 average rating of the first book
     * @param title2 title of the second book
     * @param date2 publish date of the second book
     * @param rating2 average rating of the second book
     */
    public void updateInfo(String title1, String date1, double rating1,
                           String title2, String date2, double rating2) {
        this.title1.setText(title1);
        this.title2.setText(title2);
        if (date1 == null) {
            this.publishedDate1.setText("First Published Date Not Available");
        } else {
            this.publishedDate1.setText("First Published in " + date1);
        } // if
        if (date2 == null) {
            this.publishedDate2.setText("First Published Date Not Available");
        } else {
            this.publishedDate2.setText("First Published in " + date2);
        } // if
        String stringRating1 = String.format("%.1f", rating1);
        String stringRating2 = String.format("%.1f", rating2);
        if (rating1 == 0) {
            this.rating1.setText("No Rating");
        } else {
            this.rating1.setText(stringRating1);
        } // if
        if (rating2 == 0) {
            this.rating2.setText("No Rating");
        } else {
            this.rating2.setText(stringRating2);
        } // if
    } // updateInfo

    /**
     * Updates the displayed authors based on the search results.
     *
     * @param authors1 list of authors for the first book
     * @param authors2 list of authors for the second book
     */
    public void updateAuthor(List<String> authors1, List<String> authors2) {
        if (authors1 == null) {
            this.author1.setText("Author Not Available");
        } else {
            this.author1.setText("by " + authors1);
        } // if
        if (authors2 == null) {
            this.author2.setText("Author Not Available");
        } else {
            this.author2.setText("by " + authors2);
        } // if
    } // updateAuthor

    /**
     * Updates the displayed cover image based on the cover ID.
     *
     * @param imageView the ImageView to update
     * @param coverId the cover ID used to construct the image URL
     */
    public void updateImage(ImageView imageView, String coverId) {
        Image image;
        if (coverId == null) {
            image = new Image("file:resources/noimage.png");
        } else {
            String imageUrl = "https://covers.openlibrary.org/b/id/" + coverId + "-L.jpg";
            image = new Image(imageUrl, 200, 250, false, false);
        } // if
        imageView.setImage(image);
    } // updateImage

    /**
     * Creates an alert dialog to display an error message.
     *
     * @param message the error message to display
     */
    public void createAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setResizable(true);
        alert.getDialogPane().setPrefHeight(300);
        alert.getDialogPane().setPrefWidth(600);
        alert.setTitle("Error");
        alert.setContentText(message);
        alert.showAndWait();
    } // createAlert

    /**
     * Represents a document retrieved from the Open Library API.
     */
    public static class OpenLibraryDoc {
        String title;
        @SerializedName("author_name")
        String[] authorName;
        @SerializedName("first_publish_year")
        String publishYear;
        @SerializedName("cover_i")
        String coverImage;
        @SerializedName("ratings_average")
        double averageRating;
    } // OpenLibraryDoc

    /**
     * Represents the result of a search query on the Open Library API.
     */
    public static class OpenLibraryResult {
        int numFound;
        OpenLibraryDoc[] docs;
    } // OpenLibraryResult

    /**
     * Represents the result of Genderize API.
     */
    public static class GenderizeResult {
        String gender;
    } // GenderizeResult
} // ApiApp
