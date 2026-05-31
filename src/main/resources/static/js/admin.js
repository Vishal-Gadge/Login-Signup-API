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
document.addEventListener('DOMContentLoaded',async () => {
    const body = document.querySelector('#showAllUsersBody');
    if(body != null){
        body.innerHTML = "<h1>User data is getting fetched...</h1>";
        const response = await fetch('/admin/getAllUsers',{
            method:'POST',
            credentials:'include'
        })
        const result = await response.json();
        if(response.ok){
            console.log("response is ok");
            showAllUsers(result,body);
        }else{
            console.error('Cannot get data');
            alert(result.error);
        }
    }
})

function showAllUsers(users,body){
    body.innerHTML="<h1>this is data showing page</h1><div id='resultSet'></div><br>";
    const resultSet = document.querySelector('#resultSet');
    resultSet.innerHTML=
    `<table id='table'>
        <tr><th>id</th><th>username</th><th>email</th></tr>
        <tbody></tbody>
    </table>`;
    const tbody = document.querySelector('#table tbody');
    let rows = '';
        users.forEach(user => {
            rows += `<tr><td>${user.id}</td><td>${user.username}</td><td>${user.email}</td></tr>`;
        });
        tbody.innerHTML = rows;
}

const getAllAdmins = document.querySelector("#showAllAdminsBtn");
const addAdmin = document.querySelector("#addNewAdminBtn");
