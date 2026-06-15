const loginBtn = document.getElementById('loginBtn');
if(loginBtn != null){
    loginBtn.addEventListener('click', async(evt) => {
        evt.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        const response = await fetch('/req/login/verify' , {
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify({email,password}),
            credentials:'include'
        });

        const data = await response.json();

        if(response.ok){
            window.location.href = '/';        
        }else if(response.status === 403){
            //email not verified so resend email
            alert(data.error);
        }else{
            console.error(data.error);
            alert(data.error);
        }
    })
}