import { Component } from '@angular/core';
import { LoginService } from './login.service';
import { catchError, tap, throwError } from 'rxjs';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UserService } from '../../services/user.service'; // Adjust the path as necessary

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username!: string;
  password!: string;
  isAdmin: boolean = false;  // Store isAdmin status

  constructor(private loginService: LoginService, private router: Router, private http: HttpClient, private userService: UserService) { }

  checkUsernameAndGetID(): void {
    this.http.get<{status: boolean, data: { userId: number, isAdmin: boolean }}>(`http://localhost:7000/api/acc/userid?username=${this.username}`)
      .pipe(
        tap((response) => {
          if (response.status) {
            this.isAdmin = response.data.isAdmin;  // Store isAdmin status
            this.userService.setCurrentUserId(response.data.userId);
            console.log('User ID:', response.data.userId, 'Is Admin:', response.data.isAdmin);
          } else {
            alert('Username does not exist.');
            this.userService.setCurrentUserId(null);
          }
        }),
        catchError(error => {
          alert('Error fetching user ID');
          console.error('Error:', error);
          return throwError(() => new Error('Error fetching user ID'));
        })
      )
      .subscribe();
  }

  loginUser(): void {
    this.loginService.login(this.username, this.password)
      .pipe(
        tap((token: any) => {
          this.loginService.sendAdminStatus(this.isAdmin)
          this.checkUsernameAndGetID();
          this.router.navigate(['/survey']);
         // Send isAdmin status to server
        }),
        catchError((e: any) => {
          alert(e.error.messageInfo);
          return throwError(() => e);
        })
      )
      .subscribe();
  }
}
