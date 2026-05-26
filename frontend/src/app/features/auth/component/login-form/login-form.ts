import { Component, inject, OnInit } from '@angular/core';

import {
  FormBuilder,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';

import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { Auth } from '../../../../core/services/auth';
import { TokenService } from '../../../../core/services/token';

import { LoginRequest } from '../../models/login-request.model';

@Component({
  selector: 'app-login-form',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule
  ],
  templateUrl: './login-form.html',
  styleUrl: './login-form.scss',
})
export class LoginForm implements OnInit {

  private readonly fb = inject(FormBuilder);
  private readonly authService = inject(Auth);
  private readonly tokenService = inject(TokenService);
  private readonly router = inject(Router);

  isLoading = false;

  errorMessage = '';

  private readonly DASHBOARD_ROUTES: Record<string, string> = {
    SUPER_ADMIN: '/dashboard/system',
    ADMIN: '/dashboard/users',
    INSTRUCTOR: '/dashboard/inventory',
    STUDENT: '/dashboard/inventory'
  };

  loginForm = this.fb.nonNullable.group({

    collegeId: [
      '',
      [
        Validators.required,
        Validators.minLength(3)
      ]
    ],

    password: [
      '',
      [
        Validators.required,
        Validators.minLength(6)
      ]
    ]
  });

  ngOnInit(): void {

    if (this.tokenService.isLoggedIn()) {

      const role = this.tokenService.getRole();

      this.redirectUser(role);
    }
  }

  onLogin(): void {

    if (this.isLoading) {
      return;
    }

    if (this.loginForm.invalid) {

      this.loginForm.markAllAsTouched();

      return;
    }

    this.isLoading = true;

    this.errorMessage = '';

    const credentials: LoginRequest =
      this.loginForm.getRawValue();

    this.authService.login(credentials).subscribe({

      next: (response) => {

        this.isLoading = false;

        this.tokenService.setToken(response.token);

        const role = this.tokenService.getRole();

        this.redirectUser(role);

        console.log('Login successful');
      },

      error: (err) => {

        this.isLoading = false;

        if (err.status === 401) {

          this.errorMessage =
            'Invalid college ID or password.';
        }

        else if (err.status === 403) {

          this.errorMessage =
            'Access denied.';
        }

        else {

          this.errorMessage =
            'Something went wrong. Please try again later.';
        }

        console.error('Login error:', err);
      }
    });
  }

  private redirectUser(role: string | null): void {

    const route =
      this.DASHBOARD_ROUTES[role ?? ''];

    if (route) {

      this.router.navigate([route]);

      return;
    }

    this.router.navigate(['/login']);
  }

  get collegeId() {
    return this.loginForm.controls.collegeId;
  }

  get password() {
    return this.loginForm.controls.password;
  }
}