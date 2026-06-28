//step 1
const forgotPassBtn = document.getElementById('forgotPassBtn');
if(forgotPassBtn !== null){
    forgotPassBtn.addEventListener('click', async(evt) => {
        evt.preventDefault();
    
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const confirmPass = document.getElementById('confirmPass').value;

        const showResult = document.getElementById('showResult');

        const forgotPassText = document.getElementById('forgotPassText');
        const forgotPassSpinner = document.getElementById('forgotPassSpinner');

        //empty check so that empty values should not be send
        if(email.trim() === "" || password === "" || confirmPass === ""){
            showResult.style.display = 'inline';
            showResult.textContent = `Kindly enter credentials first`;
            setTimeout(() =>showResult.style.display = 'none', 5000);
            return;
        }

        //both passwords should be same to pass
        if(password !== confirmPass){
            showResult.style.display = 'inline';
            showResult.textContent = `Confirm Password doesn't match`;
            setTimeout(() =>showResult.style.display = 'none', 5000);
            return;
        }

        //disable button click to prevent multiple click and also change its text
        forgotPassBtn.disabled = true;
        forgotPassText.textContent = `Generating OTP...`;
        forgotPassSpinner.style.display = 'inline';

        //try catch finally block so that any error cause button will be reset to original
        try{
            const response = await fetch('/req/forgotPass',{
                method:'POST',
                headers:{"Content-Type":"application/json"},
                body:JSON.stringify({email, password})
            })
        
            const data = await response.json();        

            //verification work if all ok => step 2
            if(response.ok){
                forgotPassBtn.textContent = 'OTP Sent ✅';
                showResult.textContent = `Check email for OTP`;
                showResult.style.display = 'inline';
                setTimeout(() => {}, 4000);

                //hide step 1 and show step 2
                document.getElementById('step1').style.display = 'none';
                document.getElementById('step2').style.display = 'block';

                const changePassBtn = document.getElementById('changePassBtn');
                if(changePassBtn !== null){
                    changePassBtn.addEventListener('click',async (evt) => {
                        evt.preventDefault();
                        const otp = document.getElementById('forgotPassOtp').value;
                        const changePassText = document.getElementById('changePassText');
                        const changePassSpinner = document.getElementById('changePassSpinner');

                        //otp should not be empty
                        if(otp === ""){
                            showResult.style.display = 'inline';
                            showResult.textContent = 'Please Enter Otp first 😕';
                            setTimeout(() => {
                                showInnerResult.style.display = 'none';
                            }, 5000);
                            return;
                        }

                        //disable btn and change text to prevent multiple clicks
                        changePassBtn.disabled = true;
                        changePassText.textContent = 'Verifying OTP...';
                        changePassSpinner.style.display = 'inline';

                        //try catch finally to prevent button being disabled after an error
                        try {
                            const response = await fetch('/verify/forgotPass',{
                                method:"POST",
                                headers:{"Content-Type":"application/json"},
                                body:JSON.stringify({otp})
                            })
                            const data = await response.json();

                            if(response.ok){
                                changePassText.textContent = `Password Updated ✅`;
                                changePassSpinner.style.display = 'none';
                                showResult.textContent = 'You will be redirected to login in 5sec';
                                setTimeout(() => {
                                    window.location.href = '/req/login';
                                }, 5000);
                            }else{
                                //button as in starting and show error
                                changePassText.textContent = `Change Password`;
                                changePassBtn.disabled = false;
                                changePassSpinner.style.display = 'none';
                                showResult.textContent = `${data.message}`;
                                showResult.style.display = 'inline';
                            }                
                        } catch (err) {
                            console.error('error occured'+err.message);
                            changePassText.textContent = 'Change Password';
                            showResult.textContent = err.message;
                            showResult.style.display = 'inline';
                        } finally{
                            changePassSpinner.style.display = 'none';
                            changePassBtn.disabled = false;
                        }
                        
                    })
                }
            }else{
                //button as in starting and show error
                forgotPassBtn.disabled = false;
                forgotPassSpinner.style.display = 'none';
                forgotPassText.textContent = 'Resend OTP';
                showResult.textContent = data.message;
                showResult.style.display = 'inline';
            }
        }catch(err){
            console.error('error occured'+err.message);
            forgotPassText.textContent = 'Resend OTP';
            showResult.textContent = err.message;
            showResult.style.display = 'inline';
        }finally{
            forgotPassSpinner.style.display = 'none';
            forgotPassBtn.disabled = false;
        }
    })
}