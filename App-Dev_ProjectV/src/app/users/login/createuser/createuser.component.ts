import { Component } from '@angular/core';
import { NewUser } from './newUser';
import { CreateuserService } from './createuser.service';
import Swal from 'sweetalert2';
import { catchError, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-createuser',
  templateUrl: './createuser.component.html',
  styleUrls: ['./createuser.component.css']
})
export class CreateuserComponent {
  public nUser: NewUser = new NewUser();
  confirmPassword!: string;
  StudentArray : any[] = [];
  isResultLoaded = false;
  isUpdateFormActive = false;

  username: string = "";
  password: string = "";
  currentaccID = "";


  constructor(private userService: CreateuserService, private router:Router, private http: HttpClient) 
  { this.getAcc(); }

  getAcc()
  { 
    this.http.get("http://localhost:7000/api/acc")
    .subscribe((resultData: any)=>
    {
        this.isResultLoaded = true;
        console.log(resultData.data);
        this.StudentArray = resultData.data;
    });
  }
  register()
  {
   
    let bodyData = {
      "username": this.nUser.username,
      "password": this.nUser.password,
      "email": this.nUser.email,
      "name": this.nUser.name,
      "isAdmin": this.nUser.isAdmin
    };
    this.http.post("http://localhost:7000/api/acc/add",bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);

        this.getAcc();
    
    });
  }

  setUpdate(data: any) 
  {
   this.username = data.username;
   this.password = data.password
   
  
   this.currentaccID = data.userid;
 
  }
  UpdateRecords()
  {
    let bodyData = 
    {
      "username": this.nUser.username,
      "password": this.nUser.password,
      "email": this.nUser.email,
      "name": this.nUser.name,
      "isAdmin": this.nUser.isAdmin
      
    };
    
    this.http.put("http://localhost:7000/api/acc/update"+ "/"+ this.currentaccID,bodyData).subscribe((resultData: any)=>
    {
        console.log(resultData);
        alert("Student Registered Updated")
        this.getAcc();
      
    });
  }
 
  save()
  {
    if(this.currentaccID == '')
    {
        this.register();
    }
      else
      {
       this.UpdateRecords();
      }       
  }
  setDelete(data: any)
  {
    this.http.delete("http://localhost:7000/api/acc/delete"+ "/"+ data.userid).subscribe((resultData: any)=>
    {
        console.log(resultData);
        alert("Student Deleted")
        this.getAcc();
    });
  }

  createUser(): void {
    if (this.nUser.password !== this.confirmPassword) {
      Swal.fire(
        'Register fails',
        'Passwords do not match.',
        'error'
      );
      return;
    }
  

    console.log(this.nUser);
    this.userService.createUser(this.nUser).pipe(
      catchError((e: any) => {
        Swal.fire(
          'Register fails',
          e.error.messageInfo,
          'error'
        ).then(() => {
          this.router.navigate(['/register']);
        });
        return throwError(() => e);
      })
    ).subscribe(() => {
      Swal.fire(
        'User created',
        'Your user has been created successfully, please log in with the data you provided.',
        'success'
      ).then(() => {
        this.router.navigate(['/login']);
        this.save()
      });
    });
  }
  
}
