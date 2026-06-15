document.addEventListener('DOMContentLoaded',async (evt) => {
    const resultSet = document.querySelector('#resultSet');
    resultSet.innerHTML = '<h3>Email is getting verified...</h3>';
    const token = new URLSearchParams(window.location.search).get('token');

    if(!token){
        resultSet.innerHTML = `<p style="color: red;">No Verification token found</p>`;
        return;
    }

    
    try {
        const response = await fetch(`/verify/email?token=${token}`,{
            method:'POST'
        });
    
        const result = await response.json();
    
        if(response.ok){  //200
            resultSet.innerHTML = `
                <p style="color: green;">${result.message}</p>
                <h4>You will be redirect to login in 10 sec</h4><br>
            `;
            setTimeout(() => {
                window.location.href = '/req/login';
            }, 10000);
        }else if(response.status === 400 || response.status === 410){   //InvalidToken or Expired 
            resultSet.innerHTML = `
                <p style="color: red;">${result.message}</p>
                <a href="/resend-verification">Resend verification email</a>
            `;
        }else if(response.status === 409){  //UserAlreadyVerifiedException
            resultSet.innerHTML = `
                <p style="color: orange;">${result.message}</p>
                <h4>You will be redirect to login in 10 sec</h4><br>;
                setTimeout(() => {
                    window.location.href = '/req/login';
                }, 10000);
            `;
        }else{
            resultSet.innerHTML = `<p style="color: red">Something went wrong</p>`;
        }
    } catch (error) {
        console.error('Fetch error: ',error);
        resultSet.innerHTML = `<p style="color: red">Network error. Try again</p>`;
    }
})

