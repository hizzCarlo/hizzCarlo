import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'  // This ensures the service is a singleton
})
export class UserService {
  private currentUserId: number | null = null;

  constructor() { }

  setCurrentUserId(id: number | null): void {
    this.currentUserId = id;
    localStorage.setItem('currentUserId', id ? id.toString() : 'null'); // Optionally store in local storage
  }

   getIsAdmin(): boolean {
    const isAdmin = localStorage.getItem('isAdmin');
    return isAdmin === 'true';
  }
}