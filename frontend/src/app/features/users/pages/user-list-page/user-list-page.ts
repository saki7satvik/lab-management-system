import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, switchMap, shareReplay } from 'rxjs/operators';

import { UserService } from '../../services/user.service'; 
import { UserResponse } from '../../models/user-response.model';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { TokenService } from '../../../../core/services/token';

@Component({
  selector: 'app-user-list-page',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent, FormsModule],
  templateUrl: './user-list-page.html',
  styleUrl: './user-list-page.scss',
})
export class UserListPage implements OnInit {
  private userService = inject(UserService);
  private tokenService = inject(TokenService);

  currentUserRole: string = '';

  // 1. Reactive Streams for filtering and reloading
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  
  searchQuery$ = new BehaviorSubject<string>('');
  selectedRole$ = new BehaviorSubject<string>('');
  selectedStatus$ = new BehaviorSubject<string>('');

  // 2. Data Streams
  private rawUsers$!: Observable<UserResponse[]>;
  filteredUsers$!: Observable<UserResponse[]>; // Exposed to the HTML template

  ngOnInit(): void {
    this.currentUserRole = this.tokenService.getRole() || 'STUDENT';

    // 3. Setup backend data stream listening to refresh actions
    this.rawUsers$ = this.refreshTrigger$.pipe(
      switchMap(() => this.userService.getAllUsers()),
      shareReplay(1) // Caches data so multiple async pipe checks don't cause duplicate requests
    );

    // 4. Combine inputs and data reactively using combineLatest
    this.filteredUsers$ = combineLatest([
      this.rawUsers$,
      this.searchQuery$,
      this.selectedRole$,
      this.selectedStatus$
    ]).pipe(
      map(([users, search, role, status]) => {
        const query = search.trim().toLowerCase();
        
        return users.filter(user => {
          const matchesSearch = !query || 
            user.name.toLowerCase().includes(query) ||
            user.collegeId.toLowerCase().includes(query) ||
            user.email.toLowerCase().includes(query);

          const matchesRole = !role || user.role === role;
          const matchesStatus = !status || user.status === status;

          return matchesSearch && matchesRole && matchesStatus;
        });
      })
    );
  }

  // Forces the data stream to automatically perform a new HTTP request
  refreshData(): void {
    this.refreshTrigger$.next();
  }

  onDeactivate(collegeId: string): void {
    if (confirm('Are you certain you wish to deactivate this account? This revokes laboratory check-out access.')) {
      this.userService.deactivateUser(collegeId).subscribe({
        next: () => this.refreshData(),
        error: (err) => alert('Action fault error: ' + err.message)
      });
    }
  }

  onReactivate(collegeId: string): void {
    this.userService.reactivateUser(collegeId).subscribe({
      next: () => this.refreshData(),
      error: (err) => alert('Action fault error: ' + err.message)
    });
  }
}