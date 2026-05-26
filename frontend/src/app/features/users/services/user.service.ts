import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CreateUserRequest } from '../models/create-user-request.model';
import { Observable, tap } from 'rxjs';
import { UserResponse } from '../models/user-response.model';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private http = inject(HttpClient);
  private readonly API = environment.apiUrl; 

  // Method to create a new user
  createUser(user: CreateUserRequest): Observable<UserResponse> {
    console.log('UserService createUser called with data:', user);
    return this.http.post<UserResponse>(
      `${this.API}/users`,
      user
    ).pipe(
        tap(response => {
          console.log('Create User Response: ', response);
        } 
      )
    )
  }

  // Method to get all users
  getAllUsers(): Observable<UserResponse[]> {
    console.log('UserService getAllUsers called');  
    return this.http.get<UserResponse[]>(`${this.API}/users`).pipe(
      tap(response => {
        console.log('Get All Users Response: ', response);
      })
    );
  }

  // Method to get a user by ID 
  getUserById(collegeId: string): Observable<UserResponse> {
    console.log(`UserService getUserById called with id: ${collegeId}`);  
    return this.http.get<UserResponse>(`${this.API}/users/${collegeId}`).pipe(
      tap(response => {
        console.log('Get User By ID Response: ', response);
      })
    );
  }

  // Method to deactivate a user
  deactivateUser(collegeId: string): Observable<void> {
    console.log(`UserService deactivateUser called with id: ${collegeId}`);  
    return this.http.put<void>(`${this.API}/users/${collegeId}/deactivate`, {}).pipe(
      tap(() => {
        console.log('User deactivated successfully');
      })
    );
  }

  // Method to reactivate a user
  reactivateUser(collegeId: string): Observable<void> {
    console.log(`UserService reactivateUser called with id: ${collegeId}`);  
    return this.http.put<void>(`${this.API}/users/${collegeId}/reactivate`, {}).pipe(
      tap(() => {
        console.log('User reactivated successfully');
      })
    );
  }
}
