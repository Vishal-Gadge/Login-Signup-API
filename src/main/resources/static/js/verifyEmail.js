document.addEventListener('DOMContentLoaded',async (evt) => {
    const showResult = document.getElementById('showResultDiv');
    const showMessage = document.getElementById('showMessage');
    const showLink = document.getElementById('showLink');
    const token = new URLSearchParams(window.location.search).get('token');

    if(!token){
        showMessage.textContent = 'No verification token found😒';
        showLink.href = "/html/resend-verification.html";
        showLink.textContent = 'Resend verification email';
        return;
    }

    try {
        const response = await fetch(`/verify/email?token=${token}`,{
            method:'POST'
        });
    
        const result = await response.json();
    
        if(response.ok || response.status === 409){  //200 or 409
            showResult.style.color = 'green';
            showMessage.textContent = `${result.message}✅ You will be redirect to login...⏳`;
            setTimeout(() => {
                window.location.href = '/req/login';
            }, 5000);
        }else {   //InvalidToken or Expired 
            showMessage.textContent = `${result.message}😕`;
            showLink.href = "/html/resend-verification.html";
            showLink.textContent = "Resend verification email";
        }
    } catch (error) {
        console.error('Fetch error: ',error);
        showMessage.textContent = `Network error. Try again later😞`;
    }
})

