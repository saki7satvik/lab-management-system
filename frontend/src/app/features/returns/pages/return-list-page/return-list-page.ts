import { Component, inject, OnInit } from '@angular/core';
import { TokenService } from '../../../../core/services/token';
import { ReturnService } from '../../services/return.service';
import { BehaviorSubject, combineLatest, map, Observable, shareReplay, switchMap } from 'rxjs';
import { ReturnResponseModel } from '../../models/return-response.model';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-return-list-page',
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './return-list-page.html',
  styleUrl: './return-list-page.scss',
})
export class ReturnListPage implements OnInit {
  private returnService = inject(ReturnService);
  private tokenService = inject(TokenService);

  userRole: string = '';

  // 1. Reactive State Triggers
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  searchQuery$ = new BehaviorSubject<string>('');
  selectedStatus$ = new BehaviorSubject<string>('');

  // 2. Data Streams
  private rawReturns$!: Observable<ReturnResponseModel[]>;
  filteredReturns$!: Observable<ReturnResponseModel[]>;

  ngOnInit(): void {
    this.userRole = this.tokenService.getRole() ?? 'STUDENT';

    // 3. Fetch data (Backend automatically filters based on token role!)
    this.rawReturns$ = this.refreshTrigger$.pipe(
      switchMap(() => this.returnService.getReturns()),
      shareReplay(1)
    );

    // 4. Client-side reactive filtering for search and dropdowns
    this.filteredReturns$ = combineLatest([
      this.rawReturns$,
      this.searchQuery$,
      this.selectedStatus$
    ]).pipe(
      map(([returns, query, status]) => {
        const lowerQuery = query.trim().toLowerCase();
        
        return returns.filter(ret => {
          const matchesSearch = !lowerQuery || 
                ret.id.toLowerCase().includes(lowerQuery) || 
                ret.requestId.toLowerCase().includes(lowerQuery) ||
                (ret.returnedBy && ret.returnedBy.toLowerCase().includes(lowerQuery));
                
          const matchesStatus = !status || ret.status === status;
          
          return matchesSearch && matchesStatus;
        });
      })
    );
  }
}
