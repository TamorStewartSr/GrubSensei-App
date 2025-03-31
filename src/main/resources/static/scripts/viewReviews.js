const baseUrl = "http://localhost:8082/diningReviews"; // Adjust if needed
const baseUrl1 = 'http://localhost:8082/admin'; 

// Function to handle API responses
async function handleResponse(response) {
    if (response.ok) {
        return response.json();
    } else {
        let errorMessage = "Something went wrong";
        try {
            const error = await response.json();
            errorMessage = error.message || JSON.stringify(error);
        } catch (e) {
            errorMessage = await response.text(); // Fallback for non-JSON errors
        }
        throw new Error(errorMessage);
    }
}

// Function to fetch reviews from backend
async function getReviewsByRestaurantId(restaurantId) {
    try {
        const response = await fetch(`${baseUrl1}/reviewStatus/${restaurantId}`);
        return await handleResponse(response);
    } catch (error) {
        console.error("Error fetching reviews:", error.message);
        alert("Failed to load reviews. Please try again later.");
        return [];
    }
}

// Function to display reviews on page load
async function viewReviews() {
    const restaurantId = localStorage.getItem("selectedRestaurantId"); // Retrieve stored restaurant ID

    if (!restaurantId) {
        console.error("No restaurant ID found in localStorage.");
        return;
    }

    const reviews = await getReviewsByRestaurantId(restaurantId);
    
    const tableBody = document.querySelector("#reviewsTable tbody");
    if (reviews.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='5'>No reviews available.</td></tr>";
        return;
    }

    tableBody.innerHTML = reviews.map(review => `
        <tr>
            <td>${review.submittedBy}</td>
            <td>${review.peanutScore}</td>
            <td>${review.eggScore}</td>
            <td>${review.dairyScore}</td>
            <td>${review.comment}</td>
        </tr>
    `).join('');
}

// Automatically call the function when the page loads
document.addEventListener("DOMContentLoaded", viewReviews);



