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

        if(response.ok){
            console.log("Login successful");
            window.location.href = '/';        
        }else{
            console.error("Invalid Credentials");
            alert("Invalid Credentials");
        }
    })
}