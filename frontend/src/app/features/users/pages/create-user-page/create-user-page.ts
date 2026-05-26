import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { CreateUserRequest } from '../../models/create-user-request.model';
import { CommonModule } from '@angular/common';
import { TokenService } from '../../../../core/services/token';
import { PageHeaderComponent } from "../../../../shared/components/page-header/page-header";

@Component({
  selector: 'app-create-user-page',
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent],
  templateUrl: './create-user-page.html',
  styleUrl: './create-user-page.scss',
})
export class CreateUserPage {
  private fb = inject(FormBuilder);

  private userService = inject(UserService);

  private router = inject(Router);

  private tokenService = inject(TokenService);

  getRole(): string {
    const token = this.tokenService.getToken();
    if (token) {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.role || '';
    }
    return '';
  }

  isLoading = false;
  
  errorMessage = '';

  userForm =
    this.fb.nonNullable.group({

      name: [
        '',
        Validators.required
      ],

      collegeId: [
        '',
        Validators.required
      ],

      email: [
        '',
        [
          Validators.required,
          Validators.email
        ]
      ],

      phone: [
        '',
        Validators.pattern('^[0-9]{10}$')
      ],

      role: [
        '',
        Validators.required
      ]
    });

    onSubmit(): void{
      if (this.userForm.invalid) {
        this.userForm.markAllAsTouched();
        return;
      } 
      
      this.isLoading = true;

      this.userService.createUser(this.userForm.getRawValue() as CreateUserRequest)
      .subscribe({
        next: () => {
          this.isLoading = false;
          this.router.navigate(['dashboard/users']);
        },
        error: (error) => {
          this.isLoading = false;
          this.errorMessage = 'Failed to create user';
          console.error(error); 
        }
      })
    }
}
