const baseUrl = 'http://localhost:8082/diningReviews'; //Adjust as needed

//Helper function to handle API response
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

//Add Restaurant function
async function addReview(reviewDetails) {
    try {
        const response = await fetch(`${baseUrl}/addReview`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(reviewDetails),
        });

        if (response.ok) {
            alert('Review added!');
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
        alert(`Adding review failed:\n${error.message}`);
    }
}

//Add Review function
async function handleAddReview() {

    //Collect the form data for the review
    const reviewDetails = {
        submittedBy: document.getElementById('submittedBy').value.trim(),
        restaurantId: document.getElementById('restaurantId').value.trim(),
        peanutScore: parseInt(document.getElementById('peanutRating').value.trim()) || null,
        eggScore: parseInt(document.getElementById('eggRating').value.trim()) || null,
        dairyScore: parseInt(document.getElementById('dairyRating').value.trim()) || null,
        comment: document.getElementById('comment').value.trim()
    };

    try {
        await addReview(reviewDetails);
        alert('Review submitted successfully!');
    } catch (error) {
        alert(`Error submitting review:\n${error.message}`);
    }
}


