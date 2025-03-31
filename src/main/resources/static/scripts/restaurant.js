const baseUrl1 = 'http://localhost:8082/restaurants'; // Adjust as needed

// Helper function to handle API responses
async function handleResponse(response) {
    if (response.ok) {
        return response.json();
    } else {
        let errorMessage = "Something went wrong";
        try {
            const error = await response.json();
            errorMessage = error.message || JSON.stringify(error);
        } catch (e) {
            errorMessage = await response.text();  // Fallback for non-JSON errors
        }

        if (response.status === 400) {
            alert(`Validate all fields: ${errorMessage}`);
        } else if (response.status === 409) {
            alert(`Duplicate Entry: ${errorMessage}`);
        } else {
            alert(errorMessage);
        }

        throw new Error(errorMessage);
    }
}

// Add Restaurant
async function addRestaurant(restaurantDetails) {
    try {
        const response = await fetch(`${baseUrl1}/addRestaurant`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(restaurantDetails),
        });

        const result = await handleResponse(response);

        // Store the restaurant name/id in localStorage
        localStorage.setItem('restaurantName', result.name);
        localStorage.setItem('restaurantId', result.id)

        alert('Restaurant Added!');
        window.location.href = 'addReview.html';
    } catch (error) {
        alert(`Adding restaurant failed:\n${error.message}`);
    }
}

// Add Restaurant Handler
async function handleAddRestaurant() {
    // Collect form data
    const restaurantDetails = {
        name: document.getElementById('name').value.trim(),
        address: document.getElementById('address').value.trim(),
        city: document.getElementById('city').value.trim(),
        state: document.getElementById('state').value.trim(),
        zipCode: document.getElementById('zipCode').value.trim(),
        website: document.getElementById('website').value.trim(),
        phoneNumber: document.getElementById('phoneNumber').value.trim(),
    };

    // Call the function with form data
    await addRestaurant(restaurantDetails);
}

// Search restaurant function 
function performSearch() {
    const searchType = document.getElementById("searchType").value;
    const input1 = document.getElementById("searchInput1").value.trim();
    const input2 = document.getElementById("searchInput2").value.trim();

    if (!searchType) {
        alert("Please select a search method.");
        return;
    }

    let url = `${baseUrl1}/`;

    switch (searchType) {
        case "searchByName":
            if (!input1) {
                alert("Please enter a restaurant name.");
                return;
            }
            url += `searchByName?name=${encodeURIComponent(input1)}`;
            break;

        case "searchByNameAndZipCode":
            if (!input1 || !input2) {
                alert("Please enter both restaurant name and zip code.");
                return;
            }
            url += `searchByNameAndZipCode?name=${encodeURIComponent(input1)}&zipCode=${encodeURIComponent(input2)}`;
            break;

        case "searchByZipCodeAndPeanut":
            if (!input1) {
                alert("Please enter a zip code.");
                return;
            }
            url += `searchByZipCodeAndPeanut?zipCode=${encodeURIComponent(input1)}`;
            break;

        case "searchByZipCodeAndDairy":
            if (!input1) {
                alert("Please enter a zip code.");
                return;
            }
            url += `searchByZipCodeAndDairy?zipCode=${encodeURIComponent(input1)}`;
            break;

        case "searchByZipCodeAndEgg":
            if (!input1) {
                alert("Please enter a zip code.");
                return;
            }
            url += `searchByZipCodeAndEgg?zipCode=${encodeURIComponent(input1)}`;
            break;

        default:
            alert("Invalid search type.");
            return;
    }

    // Fetch restaurant data from backend
    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error("No restaurants found.");
            }
            return response.json();
        })
        .then(data => {
            if (data.length > 0 || (searchType === "searchByNameAndZipCode" && data)) {
                localStorage.setItem("restaurantResults", JSON.stringify(data));

                // Store restaurant name for name-based searches
                if (searchType === "searchByName" || searchType === "searchByNameAndZipCode") {
                    localStorage.setItem("restaurantName", input1);
                }

                // Store restaurant ID if available
                if (Array.isArray(data) && data.length > 0) {
                    localStorage.setItem("restaurantId", data[0].id);
                } else if (data && data.id) {
                    localStorage.setItem("restaurantId", data.id);
                }

                // Redirect to results page
                window.location.href = "searchResults.html";
            } else {
                alert("No results found for this search.");
            }
        })
        .catch(error => {
            alert(error.message);
        });
}

function updateSearchUI() {
    const searchType = document.getElementById("searchType").value;
    const input1 = document.getElementById("searchInput1");
    const input2 = document.getElementById("searchInput2");
    const searchButton = document.getElementById("searchButton");

    // Hide inputs and button initially
    input1.style.display = "none";
    input2.style.display = "none";
    searchButton.style.display = "none";

    // Determine which inputs should be shown
    if (searchType === "searchByName") {
        input1.placeholder = "Enter Restaurant Name";
        input1.style.display = "block";
    } else if (searchType === "searchByNameAndZipCode") {
        input1.placeholder = "Enter Restaurant Name";
        input2.placeholder = "Enter Zip Code";
        input1.style.display = "block";
        input2.style.display = "block";
    } else if (searchType.includes("searchByZipCode")) {
        input1.placeholder = "Enter Zip Code";
        input1.style.display = "block";
    }

    // Show search button when a method is selected
    if (searchType) {
        searchButton.style.display = "block";
    }
}
