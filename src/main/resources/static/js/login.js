const loginBtn = document.getElementById('loginBtn');
if(loginBtn != null){
    loginBtn.addEventListener('click', async(evt) => {
        evt.preventDefault();

        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const showResult = document.getElementById('showResult');

        //empty
        if(email.trim() === "" || password.trim() === "" ){
            showResult.textContent = "Kindly enter details first";
            showResult.style.display = 'block';
            setTimeout(() => {
                showResult.style.display = 'none';
            }, 5000);
            return;
        }

        const btnText = document.getElementById('loginBtnText');
        const btnSpinner = document.getElementById('loginBtnSpinner');
        
        try{
            loginBtn.disabled = true;
            btnText.textContent = 'Logging in...';
            btnSpinner.style.display = 'inline';

            const response = await fetch('/req/login/verify' , {
                method:"POST",
                headers:{"Content-Type":"application/json"},
                body:JSON.stringify({email,password}),
                credentials:'include'
            });

            const result = await response.json();

            if(response.ok){
                console.log('btn text is: ',btnText);
                document.getElementById('loginBtnText').textContent = 'Login Success✅';
                btnSpinner.style.display = 'none';
                showResult.textContent = ` You will be redirect to Homepage`;
                showResult.style.display = 'block';
                showResult.style.color = '#45ff45';
                // setTimeout(() => {
                //     window.location.href = '/';
                // }, 5000);
            }else{
                console.error(result.message);
                loginBtn.disabled = false;
                btnText.textContent = 'Log in';
                btnSpinner.style.display = 'none';
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
            loginBtn.disabled = false;
            btnText.textContent = 'Log in';
            btnSpinner.style.display = 'none';
        }    
    })
}