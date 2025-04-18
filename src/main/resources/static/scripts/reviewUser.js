const baseUrl = 'http://localhost:8082/reviewUsers'; // Adjust the base URL as needed

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

//Login function
async function login(displayName, password) {
    try {
        const response = await fetch(`${baseUrl}/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({ displayName, password }),
        });

        const user = await handleResponse(response);

        //Store the logged-in username in localStorage
        localStorage.setItem('loggedInUser', user.displayName);
        localStorage.setItem('loggedInUserId', user.id)

        alert(`Welcome, ${user.displayName}!`);
        window.location.href = 'loggedInPage.html'; //Redirect after login


    } catch (error) {
        alert(`Login failed: ${error.message}`);
    }
}

//SignUp function
async function signUp(userDetails) {
    try {
        const response = await fetch(`${baseUrl}/signUp`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(userDetails),
        });

        if (response.ok) {
            alert('Account created!');
            window.location.href = 'index.html';
        } else {
            const errorData = await response.json();

            //Check if there are field-specific validation errors
            if (typeof errorData === 'object' && !Array.isArray(errorData)) {
                // Format all error messages into a single string
                const errorMessages = Object.values(errorData).join('\n');
                throw new Error(errorMessages);
            } else {
                //Generic error message if structure is unexpected
                throw new Error(errorData.message || 'Please fill all input fields.');
            }
        }

    } catch (error) {
        alert(`Sign-up failed:\n${error.message}`);
    }
}

//SignUp method
async function handleSignUp() {
    //Collect form data
    const userDetails = {
        displayName: document.getElementById('displayName').value.trim(),
        password: document.getElementById('password').value.trim(),
        city: document.getElementById('city').value.trim(),
        state: document.getElementById('state').value.trim(),
        zipCode: document.getElementById('zipCode').value.trim(),
        peanutAllergies: !!document.getElementById('peanutAllergies').checked,
        eggAllergies: !!document.getElementById('eggAllergies').checked,
        dairyAllergies: !!document.getElementById('dairyAllergies').checked
    };

    //Call the signUp function with the collected data
    signUp(userDetails);
}

//Fetch user by name
async function getUserByName(displayName) {
    try {
        const response = await fetch(`${baseUrl}/${displayName}`, { method: 'GET' });
        const user = await handleResponse(response);
        console.log(`User found: ${user.displayName}, ${user.city}, ${user.state}, ${user.zipCode}`);
        return user;
    } catch (error) {
        alert(`Error fetching user: ${error.message}`);
    }
}

//Fetch user by ID
async function getUserById(id) {
    try {
        const response = await fetch(`${baseUrl}/${id}`, {
            method: 'GET',
        });

        return await handleResponse(response);
    } catch (error) {
        alert(`Error fetching user: ${error.message}`);
    }
}

// Delete user
async function deleteUser(id) {
    try {
        const response = await fetch(`${baseUrl}/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            throw new Error('Failed to delete user');
        }
        alert('User deleted successfully');
        localStorage.removeItem('loggedInUserId');
        window.location.href = 'index.html';
    } catch (error) {
        alert(`Error deleting user: ${error.message}`);
    }
}

//Add event listeners for login
document.getElementById('loginButton').addEventListener('click', () => {
    const displayName = document.getElementById('displayName').value;
    const password = document.getElementById('password').value;

    if (displayName && password) {
        login(displayName, password);
    } else {
        alert('Please fill in both fields.');
    }
});
