/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating movie info from resultData");

    // find the empty h3 body by id "movie_info"
    let movieInfoElement = $("#movie_info");

    // append two html <p> created to the h3 body, which will refresh the page
    movieInfoElement.append("<p>Movie Name: " + resultData["title"] + "</p>" +
        "<p>Year: " + resultData["year"] + "</p>" + "<p>Director: " + resultData["director"] + "</p>");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let starTableBodyElement = $("#star_table tbody");
    let starList = resultData["starList"];

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(10, starList.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<td>" + starList[i]["name"] + "</td>";
        rowHTML += "<td>" + starList[i]["birthYear"] + "</td>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');

// Makes the HTTP GET request and registers on success callback function handleResult
$.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/movie?id=" + movieId, // Setting request url
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully
});