import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { InventoryService } from '../../../inventory/services/inventory.service';
import { ComponentResponseModel } from '../../../inventory/models/component-response.model';

import { TokenService } from '../../../../core/services/token';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';
import { RequestService } from '../../services/request.service';


interface CartItem { componentId: string; name: string; quantity: number; maxQty: number; }

@Component({
  selector: 'app-create-request-page',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeaderComponent],
  templateUrl: './create-request-page.html',
  styleUrl: './create-request-page.scss'
})
export class CreateRequestPage implements OnInit {
  private inventoryService = inject(InventoryService);
  private requestService = inject(RequestService);
  private tokenService = inject(TokenService);
  private router = inject(Router);

  inventory: ComponentResponseModel[] = [];
  filteredInventory: ComponentResponseModel[] = [];
  searchQuery = '';
  
  cart: CartItem[] = [];
  isSubmitting = false;

  ngOnInit(): void {
    this.inventoryService.getAllComponents().subscribe(data => {
      this.inventory = data.filter(c => c.availableQuantity > 0); // Only show in-stock items
      this.applySearch();
    });
  }

  applySearch(): void {
    const q = this.searchQuery.toLowerCase();
    this.filteredInventory = this.inventory.filter(c => c.name.toLowerCase().includes(q));
  }

  addToCart(component: ComponentResponseModel, qtyInput: HTMLInputElement): void {
    const qty = parseInt(qtyInput.value);
    if (!qty || qty <= 0 || qty > component.availableQuantity) {
      alert('Invalid quantity requested.'); return;
    }
    
    const existing = this.cart.find(c => c.componentId === component.id);
    if (existing) {
      if (existing.quantity + qty > component.availableQuantity) {
        alert('Cannot request more than available stock.'); return;
      }
      existing.quantity += qty;
    } else {
      this.cart.push({ componentId: component.id, name: component.name, quantity: qty, maxQty: component.availableQuantity });
    }
    qtyInput.value = ''; // Reset input
  }

  removeFromCart(index: number): void {
    this.cart.splice(index, 1);
  }

  submitRequest(): void {
    if (this.cart.length === 0) return;
    this.isSubmitting = true;

    const payload = {
      targetType: 'INDIVIDUAL',
      targetId: this.tokenService.getCollegeId() ?? 'UNKNOWN',
      items: this.cart.map(c => ({ componentId: c.componentId, quantity: c.quantity }))
    };

    this.requestService.createRequest(payload).subscribe({
      next: () => this.router.navigate(['/dashboard/requests']),
      error: (err) => { alert('Failed to create request'); this.isSubmitting = false; }
    });
  }
}