import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, switchMap, shareReplay } from 'rxjs/operators';

import { TokenService } from '../../../../core/services/token';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { RequestService } from '../../services/request.service';
import { RequestResponseModel } from '../../models/response/request-response.model';

@Component({
  selector: 'app-request-list-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './request-list-page.html',
  styleUrl: './request-list-page.scss'
})
export class RequestListPage implements OnInit {
  private requestService = inject(RequestService);
  private tokenService = inject(TokenService);

  userRole: string = '';

  // 1. STATE & FILTERS (BehaviorSubjects)
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  
  searchQuery$ = new BehaviorSubject<string>('');
  selectedStatus$ = new BehaviorSubject<string>('');

  // 2. DATA STREAMS
  private rawRequests$!: Observable<RequestResponseModel[]>;
  filteredRequests$!: Observable<RequestResponseModel[]>;

  ngOnInit(): void {
    this.userRole = this.tokenService.getRole() ?? 'STUDENT';
    console.log('User Role:', this.userRole); // Debugging line to verify role retrieval

    // 3. FETCH DATA (Automatically triggered by refreshTrigger$)
    this.rawRequests$ = this.refreshTrigger$.pipe(
      switchMap(() => {
        return this.userRole === 'STUDENT'
          ? this.requestService.getMyRequests()
          : this.requestService.getAllRequests();
      }),
      shareReplay(1)
    );

    // 4. REACTIVE FILTERING
    this.filteredRequests$ = combineLatest([
      this.rawRequests$,
      this.searchQuery$,
      this.selectedStatus$
    ]).pipe(
      map(([requests, query, status]) => {
        const lowerQuery = query.trim().toLowerCase();
        
        return requests.filter(req => {
          const matchesId = !lowerQuery || 
                req.id.toLowerCase().includes(lowerQuery) || 
                req.createdBy.toLowerCase().includes(lowerQuery);
                
          const matchesStatus = !status || req.status === status;
          
          return matchesId && matchesStatus;
        });
      })
    );
  }

  // Call this if you add an approve/reject button and need to refresh the list!
  refreshData(): void {
    this.refreshTrigger$.next();
  }
}