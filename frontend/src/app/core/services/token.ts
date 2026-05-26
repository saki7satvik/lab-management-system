import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class TokenService {

  private readonly TOKEN_KEY = 'token';

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  clearToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  getPayload(): any {

    const token = this.getToken();

    if (!token) {
      return null;
    }

    try {

      const payload = token.split('.')[1];

      return JSON.parse(atob(payload));

    } catch (error) {

      return null;
    }
  }

  isTokenExpired(): boolean {

    const payload = this.getPayload();

    if (!payload?.exp) {
      return true;
    }

    const currentTime = Math.floor(Date.now() / 1000);

    return payload.exp < currentTime;
  }

  isLoggedIn(): boolean {

    const token = this.getToken();

    if (!token) {
      return false;
    }

    if (this.isTokenExpired()) {

      this.clearToken();

      return false;
    }

    return true;
  }

  getRole(): string | null {
    return this.getPayload()?.role || null;
  }

  getCollegeId(): string | null {
    return this.getPayload()?.sub || null;
  }

  getStatus(): string | null {
    return this.getPayload()?.status || null;
  }
}