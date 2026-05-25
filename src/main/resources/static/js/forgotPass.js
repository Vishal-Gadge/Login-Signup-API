const forgotPassBtn = document.getElementById('forgotPassBtn');

if(forgotPassBtn !== null){
    forgotPassBtn.addEventListener('click', async(evt) => {
        console.log("event listner for forgot password starts");
        evt.preventDefault();

        const username = document.getElementById('username').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPass = document.getElementById('confirmPass').value;

        if(username.trim() === "" || email.trim() === "" || password === "" || confirmPass === ""){
            alert("Kindly enter Credentials first");
            return;
        }

        if(password !== confirmPass){
            alert("Confirm Password doesn't match");
            return;
        }

        try{
        const response = await fetch('/req/forgotPass',{
            method:'POST',
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify({username, email, password})
        })

        const result = await response.json();

        if(response.ok){
            alert(result.message);
            window.location.href='/req/login';
        }else{
            alert(result.error);
        }
        }catch(e){
            console.error(e);
        }
    })
}