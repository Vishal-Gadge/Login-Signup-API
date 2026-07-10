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
            showResult.textContent = "Password dosen't match Confirm password";
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
                signupSpinner.style.display = 'none';
                showResult.textContent = `${result.message}`;
                showResult.style.display = 'block';
                showResult.style.color = '#45ff45';
                setTimeout(() => {
                    window.location.href = '/req/login';
                }, 6000);
            }else{
                console.error(result.message);
                signupBtn.disabled = false;
                signupBtnText.textContent = 'Sign up';
                signupSpinner.style.display = 'none';
                showResult.textContent = `${result.message}`;
                showResult.style.display = 'block';
                setTimeout(() => {
                    showResult.style.display = 'none';
                }, 7000);
            }
        }catch(error){
            console.error(error);
            showResult.textContent = 'Internal server error, Try later';
            showResult.style.display = 'block';
        }finally{
            signupBtn.disabled = false;
            signupBtnText.textContent = 'Sign up';
            signupSpinner.style.display = 'none';
        }
    })
}