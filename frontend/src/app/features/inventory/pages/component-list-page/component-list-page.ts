import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from "@angular/router";
import { FormsModule } from '@angular/forms';
import { Observable, BehaviorSubject, combineLatest } from 'rxjs';
import { map, switchMap, shareReplay } from 'rxjs/operators';

import { InventoryService } from '../../services/inventory.service';
import { ComponentResponseModel } from '../../models/component-response.model';
import { PageHeaderComponent } from '../../../../shared/components/page-header/page-header';

@Component({
  selector: 'app-component-list-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './component-list-page.html',
  styleUrl: './component-list-page.scss',
})
export class ComponentListPage implements OnInit {
  private inventoryService = inject(InventoryService);

  // 1. Reactive Streams for caching, triggers, and state queries
  private refreshTrigger$ = new BehaviorSubject<void>(undefined);
  searchQuery$ = new BehaviorSubject<string>('');

  // 2. Exposed Data Streams for Template Resolution
  private rawComponents$!: Observable<ComponentResponseModel[]>;
  filteredComponents$!: Observable<ComponentResponseModel[]>;

  ngOnInit(): void {
    // 3. Coordinate fetching whenever a manual refresh trigger occurs
    this.rawComponents$ = this.refreshTrigger$.pipe(
      switchMap(() => this.inventoryService.getAllComponents()),
      shareReplay(1) // Prevents multiple subscriptions from triggering duplicate HTTP calls
    );

    // 4. Bind search field queries and inventory state stream together reactively
    this.filteredComponents$ = combineLatest([
      this.rawComponents$,
      this.searchQuery$
    ]).pipe(
      map(([components, search]) => {
        const query = search.trim().toLowerCase();
        
        return components.filter(item => {
          return !query || 
            item.name.toLowerCase().includes(query) || 
            (item.description && item.description.toLowerCase().includes(query));
        });
      })
    );
  }
}