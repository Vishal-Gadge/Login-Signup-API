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

        const response = await fetch(baseUrl+"/req/signup/save" , {
            method:"POST",
            headers:{"Content-Type":"application/json"},
            body:JSON.stringify(userDetails)
        })

        if(response.ok){
            console.log("User Saved");
            window.location.href = "/req/login";
        }else{
            console.error("Signup failed");
        }
    })
}
