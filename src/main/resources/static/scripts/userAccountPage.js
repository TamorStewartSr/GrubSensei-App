// TODO [x] create a functions for viewing all reviews use go to viewReviews.js
// TODO [x] create functions for updating user credentials
// TODO [x] create a function to delete a rejected review
// TODO [] crate a function to update review
// TODO [x] crate a function to delete a user credentials

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
            <td>
                <button onclick="deleteReview('${review.id}')">Delete</button>
                
                <button onclick="window.location.href='updateDiningReview.html'">Update</button>
                
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


// Automatically call the function when the page loads
document.addEventListener("DOMContentLoaded", viewReviews);

//update Review function
async function updateReview(reviewId, reviewDetails) {
    try {
        const response = await fetch(`${baseUrl1}/updateReview/${reviewId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(reviewDetails),
        });

        if (response.ok) {
            alert('Review updated!');
            window.location.href = 'loggedInPage.html';
        } else {
            const errorData = await response.json();

            // Check if there are field-specific validation errors
            if (typeof errorData === 'object' && !Array.isArray(errorData)) {
                // Format all error messages into a single string
                const errorMessages = Object.values(errorData).join('\n');
                throw new Error(errorMessages);
            } else {
                // Generic error message if structure is unexpected
                throw new Error(errorData.message || 'Please fill input field.');
            }
        }
    } catch (error) {
        alert(`Updating review failed:\n${error.message}`);
    }
}
//Handle Update Review function
async function handleUpdateReview() {
    //Collect the form data for the review
    const reviewId = document.getElementById('reviewId').value.trim();
    const reviewDetails = {
        submittedBy: document.getElementById('submittedBy').value.trim(),
        restaurantId: document.getElementById('restaurantId').value.trim(),
        peanutScore: parseInt(document.getElementById('peanutRating').value.trim()) || null,
        eggScore: parseInt(document.getElementById('eggRating').value.trim()) || null,
        dairyScore: parseInt(document.getElementById('dairyRating').value.trim()) || null,
        comment: document.getElementById('comment').value.trim()
    };
    try {
        await updateReview(reviewId, reviewDetails);
        alert('Review updated successfully!');
    } catch (error) {
        alert(`Error updating review:\n${error.message}`);
    }
}


