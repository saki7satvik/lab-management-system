import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { InventoryService } from '../../services/inventory.service';
import { Router } from '@angular/router';
import { CreateComponentModel } from '../../models/create-component.model';
import { CommonModule } from '@angular/common';
import { PageHeaderComponent } from "../../../../shared/components/page-header/page-header";

@Component({
  selector: 'app-create-component-page',
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent],
  templateUrl: './create-component-page.html',
  styleUrl: './create-component-page.scss',
})
export class CreateComponentPage {
  private fb = inject(FormBuilder);
  private inventoryService = inject(InventoryService);  
  private router = inject(Router);
  isLoading = false;
  errorMessage = '';

  componentForm = this.fb.nonNullable.group({
    name: [
      '',
      Validators.required
    ],
    description:[
      ''
    ],
    totalQuantity: [
      0,
      [Validators.required, Validators.min(1)]  
    ],
    comments: [ 
      ''
    ]
  });

  onSubmit(): void {
    if (this.componentForm.invalid) {
      this.componentForm.markAllAsTouched();
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    this.inventoryService.createComponent(this.componentForm.getRawValue() as CreateComponentModel)
    .subscribe({
      next: (response) => {
        this.isLoading = false;
        console.log('Component created successfully: ', response);
        this.router.navigate(['/dashboard/inventory']);
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = 'Failed to create component.';
        console.error('Error creating component: ', error);
      }
    });
  }
}
