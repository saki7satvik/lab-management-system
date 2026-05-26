import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BehaviorSubject, combineLatest, Observable } from 'rxjs';
import { map, switchMap, shareReplay } from 'rxjs/operators';

import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { RequestService } from '../../../requests/services/request.service';
import { RequestResponseModel } from '../../../requests/models/response/request-response.model';

@Component({
  selector: 'app-active-checkouts-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './active-checkouts-page.html',
  styleUrl: './active-checkouts-page.scss'
})
export class ActiveCheckoutsPage implements OnInit {
  private requestService = inject(RequestService);

  // Reactive State Triggers
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  searchQuery$ = new BehaviorSubject<string>('');

  // Data Streams
  private rawRequests$!: Observable<RequestResponseModel[]>;
  filteredCheckouts$!: Observable<RequestResponseModel[]>;

  ngOnInit(): void {
    // 1. Fetch the requests payload
    this.rawRequests$ = this.refreshTrigger$.pipe(
      switchMap(() => this.requestService.getAllRequests()),
      shareReplay(1)
    );

    // 2. Reactively filter down to active checkouts & apply search
    this.filteredCheckouts$ = combineLatest([
      this.rawRequests$,
      this.searchQuery$
    ]).pipe(
      map(([requests, query]) => {
        const lowerQuery = query.trim().toLowerCase();
        
        return requests.filter(req => {
          // STRICT RULE: Only show items that are actively checked out
          if (req.status !== 'APPROVED') return false;

          // Apply text search
          return !lowerQuery || 
                 req.id.toLowerCase().includes(lowerQuery) || 
                 req.createdBy.toLowerCase().includes(lowerQuery);
        });
      })
    );
  }
}