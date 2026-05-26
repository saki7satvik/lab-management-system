import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, throwError } from 'rxjs';
import { catchError, map, switchMap, tap } from 'rxjs/operators';

import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { RequestService } from '../../../requests/services/request.service';
import { ReturnService } from '../../services/return.service';
import { RequestResponseModel } from '../../../requests/models/response/request-response.model';
import { ItemCondition } from '../../models/return-enums.model';
import { ProcessReturnRequestModel } from '../../models/process-return.model';

interface ProcessReturnRow {
  requestItemId: string;
  componentId: string;
  componentName: string;
  checkedOutQty: number;
  qtyReturned: number;
  condition: ItemCondition | string;
  damageRemarks: string;
}

@Component({
  selector: 'app-process-return-page',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeaderComponent],
  templateUrl: './process-return-page.html',
  styleUrl: './process-return-page.scss'
})
export class ProcessReturnPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private requestService = inject(RequestService);
  private returnService = inject(ReturnService);

  requestMeta$!: Observable<RequestResponseModel>;
  
  requestId = '';
  returnRows: ProcessReturnRow[] = [];
  conditions = Object.values(ItemCondition);
  isSubmitting = false;
  globalRemarks = '';

  ngOnInit(): void {
    this.requestMeta$ = this.route.paramMap.pipe(
      map(params => params.get('requestId') || ''),
      tap(id => this.requestId = id),
      switchMap(id => this.requestService.getRequestById(id)),
      tap(request => {
        this.returnRows = request.items.map(item => ({
          requestItemId: item.id,
          componentId: item.componentId,
          componentName: item.componentName || item.componentId,
          checkedOutQty: item.quantityApproved,
          qtyReturned: item.quantityApproved,
          condition: 'GOOD',
          damageRemarks: ''
        }));
      }),
      catchError(err => {
        alert('Failed to load the original request data.');
        this.router.navigate(['/dashboard/returns/active']);
        return throwError(() => err);
      })
    );
  }

  requiresRemarks(condition: string): boolean {
    return condition === ItemCondition.DAMAGED || condition === ItemCondition.LOST;
  }

  submitReturn(): void {
    if (this.isSubmitting) return;

    // 1. Explicit Data Cleansing & Mapping
    const sanitizedItems = this.returnRows.map(row => {
      let mappedCondition = String(row.condition).toUpperCase().trim();

      if (mappedCondition === 'EXCELLENT' || mappedCondition === 'FAIR') {
        mappedCondition = 'GOOD';
      }

      return {
        requestItemId: row.requestItemId,
        quantityReturned: Number(row.qtyReturned),
        condition: mappedCondition,
        damageRemarks: row.damageRemarks ? row.damageRemarks.trim() : ''
      };
    });

    // 2. Front-End Validation Guards
    const hasQuantityErrors = sanitizedItems.some((item, index) => 
      item.quantityReturned > this.returnRows[index].checkedOutQty || item.quantityReturned <= 0
    );
    if (hasQuantityErrors) {
      alert('Error: Returned quantity must be greater than 0 and cannot exceed the checked-out amount.');
      return;
    }

    const missingRemarks = sanitizedItems.some(item => 
      this.requiresRemarks(item.condition) && !item.damageRemarks
    );
    if (missingRemarks) {
      alert('Error: Please provide specific damage/loss remarks for the flagged items.');
      return;
    }

    // 3. Dispatch Safe Payload Matching Your Working Swagger Architecture
    this.isSubmitting = true;

    const payload: ProcessReturnRequestModel = {
      requestId: this.requestId,
      remarks: this.globalRemarks ? this.globalRemarks.trim() : '',
      items: sanitizedItems
    };

    console.log('Dispatching Verified Payload to Backend API:', payload);

    this.returnService.processReturn(payload).subscribe({
      next: () => {
        this.returnRows = []; 
        alert('Hardware return processed successfully!');
        this.router.navigate(['/dashboard/returns']);
      },
      error: (err) => {
        console.error('API Process Exception Captured:', err);
        this.isSubmitting = false;

        if (err.status === 409 || err.status === 400) {
          // Captures specific backend messages like "Return exceeds approved quantity" or "Damage remarks required"
          alert(err.error?.message || 'Transaction Conflict: Please check your quantities or condition remarks.');
        } else {
          alert(`Failed to process return (Status: ${err.status || '500'}). Check system service endpoints.`);
        }
      }
    });
  }
}