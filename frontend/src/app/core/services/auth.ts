import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { LoginResponse } from '../../features/auth/models/login-response.model';
import { LoginRequest } from '../../features/auth/models/login-request.model';
import { Observable, tap } from 'rxjs';
import { TokenService } from './token';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class Auth {

  private http = inject(HttpClient);
  private tokenService = inject(TokenService);

  private readonly API = environment.apiUrl ;

  login(data: LoginRequest): Observable<LoginResponse>{
    console.log('Auth service login called with data:', data);
    return this.http.post<LoginResponse>(
      `${this.API}/auth/login`,
      data
    ).pipe(
      tap(response => {
        console.log('Login Response: ', response);
        this.tokenService.setToken(response.token);
        console.log('Token stored successfully');
        console.log('Current Token: ', this.tokenService.getPayload());
      })
    )
  }

  logout(): void {
    this.tokenService.clearToken();
  }
}
