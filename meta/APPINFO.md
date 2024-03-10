## Part 1.1: App Description

The ApiApp is a JavaFX desktop application that allows users to search for
information about books using the open Library API. Users can enter a search
query, and the application retrieves data from the Open Library API to display
details about the two most relevant books matching the query. The displayed
information includes book titles, authors, publication dates, ratings, and cover
images.

Primary Functions:
- Search: Users can enter a search query in the provided search bar.
- Display Information: The application displays details about the two most
    relevant books based on the search query.
- Visual Representation: The retrieved information includes book titles, authors,
    publication dates, ratings, and cover images.
- Error Handling: The application provides error alerts if there are issues with
    the API response or the search results.

## Part 1.2: APIs

### API 1

https://openlibrary.org/search.json?q=Harry+Potter+and+the+Sorcerer%27s+Stone&mode=everything

Above URL is an example of the first API that finds two most relevant books with
their titles, authors, published years, ratings, and cover images.
URL Format:
    Endpoint + "?q" + EncodedSearchQuery + "&mode=everything"
Note:
    Endpoint = https://openlibrary.org/search.json

### API 2

https://api.genderize.io?name=John

Above URL is an example of the second API that uses the first API to retrieve the author's
(first author's, if more than 1) first name which will be used to predict the author's gender.
URL Format:
    Endpoint + "?name=" + EncodedFirstName
Note:
    Endpoint = https://api.genderize.io;