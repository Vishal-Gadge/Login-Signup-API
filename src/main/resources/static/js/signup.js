const baseUrl = "http://localhost:8080";
const signupBtn = document.getElementById("signupBtn");
if(signupBtn != null){
    signupBtn.addEventListener("click" , async (evt) => {
        evt.preventDefault();
        const userDetails = {
            username : document.getElementById('name').value,
            email : document.getElementById('email').value,
            password : document.getElementById('password').value
        }

        const confPassword = document.getElementById('confirmPassword').value;

        if(userDetails.username.trim() === "" ||
            userDetails.email.trim() === "" ||
            userDetails.password.trim() === "" ||
            confPassword.trim() === ""){
                alert("Kindly enter details first");
                return;
            }

        if(userDetails.password !== confPassword){
            alert("Passwords don't match")
            return;
        }

        let signupBtnDiv = document.getElementById('signupBtnDiv');
        signupBtnDiv.innerHTML = "<p>Generating a Verification link...</p>";
        
        const response = await fetch("/req/signup/save" , {
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify(userDetails)
        })

        signupBtnDiv.innerHTML = `<button id="signupBtn" type="submit">Sign up</button>`;

        //to do values null no mater what response

        const result = await response.json();

        if(response.ok){
            alert(`User saved and verification link has been sent to email ${userDetails.email}`);
            window.location.href = "/req/login";
        }else{
            console.error("Signup failed : "+result.message);
            alert(result.message);
            window.location.href = "/req/signup";
        }
    })
}


//resend email
const resendEmailBtn = document.getElementById('resendEmailButton');
if(resendEmailBtn !== null){
    resendEmailBtn.addEventListener('click', async (evt) => {
        evt.preventDefault();
        
        const resendEmail = document.getElementById('resendVerificationEmail').value;

        const response = await fetch("/resend-email",{
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify({email:resendEmail})
        })

        const result = await response.json();

        if(response.ok){
            alert(result.message);
        }else{
            console.error(result.message);
            alert(result.message);
        }
    })
}
