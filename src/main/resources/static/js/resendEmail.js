const resendEmailBtn = document.getElementById('resendEmailButton');
if(resendEmailBtn !== null){
    resendEmailBtn.addEventListener('click', async (evt) => {
        evt.preventDefault();
        const resendEmail = document.getElementById('resendVerificationEmail').value;
        const showResult = document.getElementById('result');

        if(resendEmail === ""){
            showResult.textContent = 'Email is Empty';
            showResult.style.display = 'block';
            setTimeout(() => {
                showResult.style.display = 'none';
            }, 5000);
            return;
        }

        const btnText = document.getElementById('resendBtnText');
        const btnSpinner = document.getElementById('resendBtnSpinner');

        try{
            resendEmailBtn.disabled = true;
            btnText.textContent = 'Resending Email...';
            btnSpinner.style.display = 'inline';

            const response = await fetch("/resend-email",{
                method:"POST",
                headers:{"Content-Type":"application/json"},
                body:JSON.stringify({email:resendEmail})
            })

            const result = await response.json();

            if(response.ok){
                btnText.textContent = 'Send link';
                btnSpinner.style.display = 'none';
                showResult.textContent = `${result.message}`;
                showResult.textContent += `. You will be redirect to login`;
                showResult.style.display = 'block';
                showResult.style.color = 'green';
                setTimeout(() => {
                    window.location.href = '/req/login';
                }, 5000);
            }else{
                console.error(result.message);
                resendEmailBtn.disabled = false;
                btnText.textContent = 'Resend link';
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
            resendEmailBtn.disabled = false;
            btnText.textContent = 'Send link';
            btnSpinner.style.display = 'none';
        }    
    })
}
