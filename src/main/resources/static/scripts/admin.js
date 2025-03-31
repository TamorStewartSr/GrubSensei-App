const baseUrl = 'http://localhost:8082/admin'; // Adjust the base URL as needed

//Helper function to handle API responses
async function handleResponse(response) {
    if (response.ok) {
        return response.json();
    } else {
        let errorMessage = 'Something went wrong';
        try {
            const error = await response.json();
            errorMessage = error.message || JSON.stringify(error);
        } catch (e) {
            errorMessage = await response.text();  // Fallback for non-JSON errors
        }
        throw new Error(errorMessage);
    }
}

// Fetch PENDING dining reviews
async function getReviewsByStatus(reviewStatus = "PENDING") {
    try {
        const response = await fetch(`${baseUrl}/reviews?review_status=${reviewStatus.toUpperCase()}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json"
            }
        });

        if (!response.ok) {
            const errorMessage = await response.text();
            throw new Error(`Error ${response.status}: ${errorMessage}`);
        }

        const reviews = await response.json();
        console.log("Fetched Reviews:", reviews);
        return reviews;
    } catch (error) {
        console.error("Failed to fetch reviews:", error.message);
        return []; // Return empty list to avoid rendering errors
    }
}

async function handleAdminLogin() {
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (!username || !password) {
        alert('Please enter both username and password.');
        return;
    }

    try {
        const response = await fetch(`${baseUrl}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: new URLSearchParams({ username, password })
        });

        if (response.ok) {
            alert('Login successful!');
            window.location.href = 'adminDashboard.html'; //Redirect to admin dashboard or appropriate page
        } else {
            const errorMsg = await response.text();
            alert(`Login failed: ${errorMsg}`);
        }
    } catch (error) {
        console.error('Error during login:', error);
        alert('An error occurred during login. Please try again later.');
    }
}

// Function to perform the approve/reject action on a review
async function performReviewAction(reviewId, accepted) {
    try {
        const response = await fetch(`${baseUrl}/reviews/${reviewId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ accepted: accepted })
        });

        if (response.ok) {
            alert(`Review ${accepted ? 'approved' : 'rejected'} successfully.`);
            loadPendingReviews(); // Refresh reviews after action
        } else if (response.status === 404) {
            alert('Review not found.');
        } else if (response.status === 422) {
            alert('Invalid restaurant associated with the review.');
        } else {
            const errorMessage = await response.text();
            alert(`An unexpected error occurred: ${errorMessage}`);
        }
    } catch (error) {
        console.error('Failed to perform the review action:', error.message);
    }
}

// Render reviews into the HTML table
function renderReviews(reviews) {
    const reviewTableBody = document.getElementById('reviewTableBody');
    reviewTableBody.innerHTML = '';

    reviews.forEach(review => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${review.id}</td>
            <td>${review.submittedBy}</td>
            <td>${review.restaurantId}</td>
            <td>${review.peanutScore ?? 'N/A'}</td>
            <td>${review.eggScore ?? 'N/A'}</td>
            <td>${review.dairyScore ?? 'N/A'}</td>
            <td>${review.comment || 'No comment'}</td>
            <td>
                   <button class="action-btn approve-btn" onclick="performReviewAction(${review.id}, true)">
                    <span>👍</span> Approve
                   </button>
                    <button class="action-btn reject-btn" onclick="performReviewAction(${review.id}, false)">
                   <span>👎</span> Reject
                 </button>
               </td>
        `;
        reviewTableBody.appendChild(row);
    });
}

//Load PENDING reviews on page load and after actions
async function loadPendingReviews() {
    const pendingReviews = await getReviewsByStatus("PENDING");
    renderReviews(pendingReviews);
}

//Load reviews when the page loads
window.onload = loadPendingReviews;

//Logout function
function logout() {
    // Redirect to login page or clear any session info if necessary
    window.location.href = "adminLogin.html"; // Change this to your actual login page
}

//Home function
function home() {
    //redirects to the home page
    window.location.href = "index.html";
}






