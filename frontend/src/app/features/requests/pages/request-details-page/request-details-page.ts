import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';
import { TokenService } from '../../../../core/services/token';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { RequestItemResponseModel } from '../../models/response/request-item-response.model';
import { RequestService } from '../../services/request.service';
import { RequestResponseModel } from '../../models/response/request-response.model';
import { Observable, map } from 'rxjs';

interface EditableItem extends RequestItemResponseModel {
  editableApprovedQty: number;
}

@Component({
  selector: 'app-request-details-page',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeaderComponent],
  templateUrl: './request-details-page.html',
  styleUrl: './request-details-page.scss'
})
export class RequestDetailsPage implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private requestService = inject(RequestService);
  private tokenService = inject(TokenService);
  private http = inject(HttpClient);

  request$: Observable<RequestResponseModel> | null = null;
  items$: Observable<EditableItem[]> | null = null;
  userRole = '';
  isProcessing = false;

  ngOnInit(): void {
    this.userRole = this.tokenService.getRole() ?? 'STUDENT';
    const id = this.route.snapshot.paramMap.get('id');
    if (id) this.loadRequest(id);
  }

  loadRequest(id: string): void {
    this.request$ = this.requestService.getRequestById(id);
    
    // Derive items from request observable
    this.items$ = this.request$.pipe(
      map(req => 
        req.items.map(item => ({
          ...item,
          editableApprovedQty: item.quantityApproved > 0 ? item.quantityApproved : item.quantityRequested
        }))
      )
    );
  }

  processApproval(): void {
    if (!this.request$) return;
    this.isProcessing = true;

    this.request$.subscribe(request => {
      this.items$?.subscribe(items => {
        const payload = {
          items: items.map(item => ({
            requestItemId: item.id,
            quantityApproved: item.editableApprovedQty
          }))
        };

        this.http.put(`${environment.apiUrl}/requests/${request.id}/approve`, payload).subscribe({
          next: () => this.router.navigate(['/dashboard/requests']),
          error: () => { alert('Failed to process approval.'); this.isProcessing = false; }
        });
      });
    });
  }

  processRejection(): void {
    if (!this.request$ || !confirm('Are you sure you want to completely reject this request?')) return;
    this.isProcessing = true;

    this.request$.subscribe(request => {
      this.http.put(`${environment.apiUrl}/requests/${request.id}/reject`, {}).subscribe({
        next: () => this.router.navigate(['/dashboard/requests']),
        error: () => { alert('Failed to reject request.'); this.isProcessing = false; }
      });
    });
  }
}