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
        const showResult = document.getElementById('showResult');

        //empty
        if(userDetails.username.trim() === "" ||
            userDetails.email.trim() === "" ||
            userDetails.password.trim() === "" ||
            confPassword.trim() === ""){
                showResult.textContent = "Kindly enter details first";
                showResult.style.display = 'block';
                setTimeout(() => {
                    showResult.style.display = 'none';
                }, 5000);
                return;
            }

        //not match
        if(userDetails.password !== confPassword){
            showResult.textContent = "Password doesn't match Confirm password";
            showResult.style.display = 'block';
            setTimeout(() => {
                showResult.style.display = 'none';
            }, 5000);    
            return;
        }

        const signupBtnText = document.getElementById('signupBtnText');
        const signupSpinner = document.getElementById('signupSpinner');

        try{
            signupBtn.disabled = true;
            signupBtnText.textContent = 'Signing up...';
            signupSpinner.style.display = 'inline';

            const response = await fetch("/req/signup/save" , {
                method:"POST",
                headers:{"Content-Type":"application/json"},
                body:JSON.stringify(userDetails)
            })

            const result = await response.json();

            if(response.ok){
                signupBtnText.textContent = 'Signup Success ✅';
                showResult.textContent = `${result.message}`;
                showResult.style.display = 'block';
                showResult.style.color = 'rgb(0, 226, 255)';
                setTimeout(() => {
                    window.location.href = '/req/login';
                }, 10000);
            }else{
                console.error(result.message);
                signupBtnText.textContent = 'Sign up';
                showResult.textContent = `${result.message}`;
                showResult.style.display = 'block';
                setTimeout(() => {
                    showResult.style.display = 'none';
                }, 10000);
            }
        }catch(error){
            console.error(error);
            showResult.textContent = 'Internal server error, Try again later';
            showResult.style.display = 'block';
            signupBtnText.textContent = 'Sign up';
        }finally{
            signupBtn.disabled = false;
            signupSpinner.style.display = 'none';
        }
    })
}