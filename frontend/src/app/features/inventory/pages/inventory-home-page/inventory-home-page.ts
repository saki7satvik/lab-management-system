import { Component, inject, OnInit } from '@angular/core';
import { FeatureCardComponent } from "../../../../shared/components/feature-card/feature-card";
import { TokenService } from '../../../../core/services/token';
import { PageHeaderComponent } from "../../../../shared/components/page-header/page-header";

@Component({
  selector: 'app-inventory-home-page',
  imports: [FeatureCardComponent, PageHeaderComponent],
  templateUrl: './inventory-home-page.html',
  styleUrl: './inventory-home-page.scss',
})
export class InventoryHomePage implements OnInit {
  private tokenService = inject(TokenService)
  currentUserRole: string | null = '' ;

  ngOnInit(): void {
    this.currentUserRole = this.tokenService.getRole();
  }


}
