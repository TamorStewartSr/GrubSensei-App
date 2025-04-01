// TODO [x] create a functions for viewing all reviews use go to viewReviews.js
// TODO [] create functions for updating user credentials
// TODO [] create a function to delete a rejected review
// TODO [] crate a function to update review
// TODO [] crate a function to delete a user credentials

const baseUrl1 = "http://localhost:8082/diningReviews"; // Adjust if needed

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

async function getReviewsBySubmittedBy(submittedBy) {
    try {
        const response = await fetch(`${baseUrl1}/reviews/submittedBy/${submittedBy}`);
        return await handleResponse(response);
    } catch (error) {
        console.error("Error fetching reviews:", error.message);
        alert("Failed to load reviews. Please try again later.");
        return [];
    }
}

// Function to display reviews on page load
async function viewReviews() {
    const submittedBy =  localStorage.getItem('loggedInUser'); // Retrieve stored user
    const storedRestaurant = localStorage.getItem('restaurantName');// Retrieve stored resataurant name

    if (!submittedBy) {
        console.error("No user name found in localStorage.");
        return;
    }

    const reviews = await getReviewsBySubmittedBy(submittedBy);
    
    const tableBody = document.querySelector("#reviewsTable tbody");
    if (reviews.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='5'>No reviews available.</td></tr>";
        return;
    }

    tableBody.innerHTML = reviews.map(review => `
        <tr>
            <td>${review.submittedBy}</td>
             <td>${storedRestaurant || review.restaurantId}</td> <!-- Show the stored name -->
            <td>${review.peanutScore}</td>
            <td>${review.eggScore}</td>
            <td>${review.dairyScore}</td>
            <td>${review.comment}</td>
            <td>${review.reviewStatus}</td>
            <td>Actions Placeholder</td>
        </tr>
    `).join('');
}

// Automatically call the function when the page loads
document.addEventListener("DOMContentLoaded", viewReviews);

