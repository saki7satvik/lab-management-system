import { Component, inject, OnInit } from '@angular/core';
import { TokenService } from '../../../core/services/token';
import { Auth } from '../../../core/services/auth';
import { Router } from '@angular/router';
import { LowerCasePipe } from '@angular/common';

@Component({
  selector: 'app-profile-menu',
  imports: [LowerCasePipe],
  templateUrl: './profile-menu.html',
  styleUrl: './profile-menu.scss',
})
export class ProfileMenu implements OnInit {
  private tokenService = inject(TokenService);
  private authService = inject(Auth);
  private router = inject(Router);

  collegeId: string = '';
  role: string = '';
  isDropdownOpen = false;

  ngOnInit(): void {
    this.collegeId = this.tokenService.getCollegeId() ?? 'N/A';
    this.role = this.tokenService.getRole() ?? 'N/A';
    console.log("College Id:", this.collegeId);
    console.log("Role:", this.role);
  }

  toggleDropdown(): void {
    this.isDropdownOpen = !this.isDropdownOpen;
  }

  onLogout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  } 
}
