//call from homepage
const adminBtn = document.querySelector("#adminBtn");

if(adminBtn != null){
    adminBtn.addEventListener('click',async (evt) => {
        evt.preventDefault();

        const response = await fetch("/admin/verify",{
            method:'GET',
            headers:{"Content-Type":"application/json"},
            credentials:'include'
        })

        try{
            const result = await response.json();
            if(response.ok){
                alert(result.message);
                window.location.href='/admin/showAdminPanel';
            }else{
                alert("You are not Admin");
            }
        }catch(excp){
            console.log("exception occurred "+excp);
            alert("You are not Admin");
        }
    })
}


//admin.html
//to show all users
document.addEventListener('DOMContentLoaded', async () => {
    const container = document.querySelector('#showAllUsersDiv');
    if(container != null){
        container.innerHTML = "<h2>Data is getting fetched...</h2>";
        const response = await fetch('/admin/getAllUsers',{
            credentials:'include'
        })
        const result = await response.json();
        if(response.ok){
            showAllUsersData(container,result);
        }else{
            console.error('Cannot get data');
            alert(result.error);
        }
    }
})

function showAllUsersData(container,data){
    if(data.length !== 0){
        container.innerHTML="<h1>All Users data</h1><div id='resultSet'></div><br>";
        const resultSet = document.querySelector('#resultSet');
        resultSet.innerHTML=
        `<table id='table'>
            <thead>
                <tr><th>id</th><th>username</th><th>email</th></tr>
            </thead>
            <tbody></tbody>
        </table>`;
        const tbody = document.querySelector('#table tbody');
        let rows = '';
        data.forEach(user => {
            rows += `<tr><td>${user.id}</td><td>${user.username}</td><td>${user.email}</td></tr>`;
        });
        tbody.innerHTML = rows;
    }else{
        console.error("Didnt get any data");
        return;
    }
}



//to get all admins
document.addEventListener('DOMContentLoaded',async () => {
    const container = document.getElementById('showAllAdminsDiv');
    if(container != null){
        container.innerHTML ='<h2>Data is getting fetched...</h2>';
        const response = await fetch('/admin/getAllAdmins',{
            credentials:'include'
        })
        const result = await response.json();
        if(response.ok){
            showAllAdmins(container,result);
        }else{
            console.error('result.error');
            return;
        }
    }
})

async function showAllAdmins(container,data){
    if(data.length !== 0){
        container.innerHTML='<h2>Admin Data is:</h2><div id="resultSet"></div>';
        const resultSet = document.getElementById('resultSet');
        resultSet.innerHTML=`<table><thead>
            <tr><th>id</th><th>username</th><th>email</th></tr>
        </thead><tbody></tbody></table>`;
        const tableBody = document.querySelector('tbody');
        data.forEach(row => {
            tableBody.innerHTML += `<tr>
            <td>${row.id}</td>
            <td>${row.username}</td>
            <td>${row.email}</td></tr>`;
        })
    }else{
        console.error('Data is not present for that request');
        return;
    }
}



//to add new admin
const addNewAdminBtn = document.querySelector('#addNewAdminBtn');
if(addNewAdminBtn !== null){
    addNewAdminBtn.addEventListener('click',async (evt) => {
        evt.preventDefault();
    
        let admin = {
            username:document.querySelector('#name').value,
            email:document.querySelector('#email').value,
            password:document.querySelector('#password').value
        }

        let confirmPassword = document.querySelector('#confirmPassword').value;

        if(admin.username.trim() === "" ||
            admin.email.trim() === "" ||
            admin.password.trim() === "" ||
            confirmPassword.trim() === ""){
                alert("Kindly enter details first");
                return;
        }

        if(admin.password !== confirmPassword){
            alert("Password don't match Confirm password")
            return;
        }

        const response = await fetch('/admin/addAdmin',{
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body:JSON.stringify(admin),
            credentials:'include'
        });

        const result = await response.json();

        if(response.ok){
            alert("Admin Saved");
            window.location.href='/admin/showAdminPanel';
        }else{
            alert("Admin saved failed");
            console.error("Admin saved failed");
        }
    })
}