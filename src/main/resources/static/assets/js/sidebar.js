document.addEventListener('DOMContentLoaded', function() {
    // Get the logout link element
    const logoutLink = document.querySelector('a[href="/logout"]');

    // Add a click event listener to the logout link
    if (logoutLink) {
        logoutLink.addEventListener('click', function() {
            // Clear sessionStorage and localStorage
            sessionStorage.clear();
            localStorage.clear();

            console.log("Session and local storage cleared on logout.");
        });
    }

    // Sidebar toggle functionality
    document.getElementById('toggleSidebar').addEventListener('click', function() {
        const sidebar = document.getElementById('sidebar');
        const toggleBtn = document.getElementById('toggleSidebar');

        // Toggle sidebar visibility
        if (sidebar.style.left === '0px') {
            sidebar.style.left = '-250px';
            toggleBtn.classList.remove('toggle-active');
        } else {
            sidebar.style.left = '0px';
            toggleBtn.classList.add('toggle-active');
        }
    });
});