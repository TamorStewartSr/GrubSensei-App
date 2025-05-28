// TODO [x] create a functions for viewing all reviews use go to viewReviews.js
// TODO [x] create functions for updating user credentials
// TODO [x] create a function to delete a rejected review
// TODO [x] crate a function to delete a user credentials

// Inject custom button styles for the review action buttons
const style = document.createElement('style');
style.textContent = `
button {
  padding: 6px 14px;
  border: none;
  border-radius: 4px;
  color: #fff;
  margin: 2px;
  cursor: pointer;
  font-size: 14px;
}
button[onclick^="deleteReview"] {
  background-color: #e74c3c; /* Red for delete */
}
button[onclick^="editReview"] {
  background-color: #888888; /* Gray for update */
}
button[onclick^="viewReview"] {
  background-color: #007bff; /* Blue for view */
}
`;
document.head.appendChild(style);

// Base URL for the dining reviews API
const baseUrl1 = "http://localhost:8082/diningReviews"; 

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
            <td>
                <button onclick="deleteReview('${review.id}')">Delete</button>
                
                <button onclick="editReview('${review.id}')">Update</button>

                <button onclick="viewReview('${review.id}')">View</button>
            </td>
        </tr>
    `).join('');
}

async function deleteReview(reviewId) {
    if (confirm("Are you sure you want to delete this review?")) {
        try {
            const response = await fetch(`${baseUrl1}/delete/${reviewId}`, {
                method: 'DELETE'
            });
            if (response.ok) {
                alert("Review deleted successfully.");
                viewReviews(); // Refresh the reviews list
            } else {
                alert("Failed to delete review.");
            }
        } catch (error) {
            console.error("Error deleting review:", error);
            alert("An error occurred while deleting the review.");
        }
    }
}

function editReview(reviewId) {
    localStorage.setItem('reviewId', reviewId);
    window.location.href = 'updateDiningReview.html';
}

// Automatically call the function when the page loads
document.addEventListener("DOMContentLoaded", viewReviews);



